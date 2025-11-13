from fastapi import FastAPI, HTTPException, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.gzip import GZipMiddleware
from pydantic import BaseModel
from typing import List, Optional, Literal
from scheduler import FCFS, SJF, Priority, RoundRobin, Job
import traceback
import json

app = FastAPI()

# Add middleware to log requests (without consuming body)
from starlette.requests import Request as StarletteRequest

@app.middleware("http")
async def log_requests(request: Request, call_next):
    try:
        if request.url.path == "/run":
            print(f"\n=== REQUEST DEBUG ===")
            print(f"Method: {request.method}")
            print(f"URL: {request.url}")
            print(f"Headers: {dict(request.headers)}")
            print(f"====================\n")
        
        response = await call_next(request)
        return response
    except Exception as e:
        print(f"Middleware error: {e}")
        import traceback
        traceback.print_exc()
        raise

# Add CORS middleware to allow frontend to connect
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # In production, specify the frontend URL
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class ProcessIn(BaseModel):
    id: str
    bt: int
    at: Optional[int] = 0
    priority: Optional[int] = 0

class RunRequest(BaseModel):
    algorithm: Literal['fcfs', 'sjf', 'sjf_preemptive', 'priority', 'priority_preemptive', 'rr']
    processes: List[ProcessIn]
    quantum: Optional[int] = None  # for RR

@app.post("/run")
async def run_schedule(request: Request):
    try:
        # Read body for debugging
        try:
            body_bytes = await request.body()
        except Exception as e:
            print(f"Error reading body: {e}")
            import traceback
            traceback.print_exc()
            raise HTTPException(status_code=400, detail=f"Error reading request body: {str(e)}")
        
        body_str = body_bytes.decode('utf-8') if body_bytes else '{}'
        print(f"\n=== ENDPOINT DEBUG ===")
        print(f"Body received: {len(body_bytes)} bytes")
        print(f"Body content: {body_str[:500]}")  # Limit output
        print(f"====================\n")
        
        if not body_bytes or body_str == '{}':
            raise HTTPException(status_code=422, detail="Request body is empty or invalid")
        
        # Parse JSON manually
        try:
            body_json = json.loads(body_str)
            req = RunRequest(**body_json)
        except json.JSONDecodeError as e:
            print(f"JSON decode error: {e}")
            raise HTTPException(status_code=422, detail=f"Invalid JSON: {str(e)}")
        except Exception as e:
            print(f"Parse error: {e}")
            import traceback
            traceback.print_exc()
            raise HTTPException(status_code=422, detail=f"Failed to parse: {str(e)}")
        
        print(f"Received request with algorithm: {req.algorithm}, processes: {len(req.processes)}")
        jobs = [Job(p.id, p.bt, p.at or 0, p.priority or 0) for p in req.processes]
        
        if req.algorithm == 'fcfs':
            scheduler = FCFS(jobs)
        elif req.algorithm == 'sjf':
            scheduler = SJF(jobs, preempt=False)
        elif req.algorithm == 'sjf_preemptive':
            scheduler = SJF(jobs, preempt=True)
        elif req.algorithm == 'priority':
            scheduler = Priority(jobs, preempt=False)
        elif req.algorithm == 'priority_preemptive':
            scheduler = Priority(jobs, preempt=True)
        elif req.algorithm == 'rr':
            q = req.quantum
            if q is None:
                raise HTTPException(status_code=400, detail='Round Robin needs quantum')
            scheduler = RoundRobin(jobs, t_quantum=q)
        else:
            raise HTTPException(status_code=400, detail='Unknown algorithm')
        
        scheduler.start()
        jobs_out = []
        for j in scheduler.completed:
            job_out = {
                "id": j.id,
                "bt": j.bt,
                "at": j.at,
                "ct": j.ct,
                "tat": j.tat,
                "wt": j.wt,
            }
            if getattr(j, "priority", 0) != 0:
                job_out["priority"] = j.priority
            jobs_out.append(job_out)
        
        resp = {
            "gantt": scheduler.gantt,
            "jobs": jobs_out,
            "avg_tat": scheduler.avg_tat(),
            "avg_wt": scheduler.avg_wt(),
        }
        return resp
    except HTTPException:
        raise
    except Exception as e:
        error_msg = f"Error processing request: {str(e)}\n{traceback.format_exc()}"
        print(error_msg)  # Log to console
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=8001)