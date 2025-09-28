import time

class Job:
    """
        A job has the following attributes:
        id: unique identifier
        bt: burst time
        at: arrival time
        rt: remaining time
        ct: completion time
        tat: turn-around time (completion - arrival time)
        wt: waiting time (turn-around - burst time)
        priority (optional): used for priority scheduling
    """
    def __init__(self, id:str, bt:int, at:int=0, priority:int=0):
        self.id = id
        self.bt = bt
        self.at = at
        self.rt = bt
        self.ct = None
        self.tat = None
        self.wt = None
        self.priority = priority

    def show_attrs(self):
        print(f"""
            Burst Time: {self.bt}
            Arrival Time: {self.at}
            Completion Time: {"Processing..." if self.ct is None else self.ct}
            Turn-Around Time: {"Processing..." if self.tat is None else self.tat}
            Waiting Time: {"Processing..." if self.wt is None else self.wt}     
              """, end="")
        if self.priority != 0:
            print(f"Priority: {self.priority}", end="")
        print("") 

class Scheduler:
    """
        Parent class of scheduling algorithms
    """
    def __init__(self, jobs):
        self.jobs = jobs
        self.timer = 0
        self.gantt = []

    def process_job(self, job):
        #time.sleep(1) # simulate time passing by 1 sec
        job.rt -= 1
        self.timer += 1

    def complete_job(self, job):
        # compute job attributes
        job.ct = self.timer
        job.tat = job.ct - job.at
        job.wt = job.tat - job.bt
    
    def list_jobs(self):
        for job in self.jobs:
            print(f"Job [{job.id}]:", end="")
            job.show_attrs()
    
    def update_gantt(self, id, start, end):
        self.gantt.append({'id': id,
                            'start': start,
                            'end': end
                            })

    def show_gantt(self):
        print(self.gantt)
    
    def avg_wt(self):
        """
            returns the average waiting time of processed jobs
        """
        total = 0
        n_jobs = len(self.jobs)
        for job in self.jobs:
            if job.rt == 0:
                total += job.wt
        return total / n_jobs
    
    def avg_tat(self):
        """
            returns the average turn-around time of processed jobs
        """
        total = 0
        n_jobs = len(self.jobs)
        for job in self.jobs:
            if job.rt == 0:
                total += job.tat
        return total / n_jobs

class FCFS(Scheduler):
    """
        First Come, First Serve
    """
    def __init__(self, jobs):
        super().__init__(jobs)

    def start(self):
        # sort jobs based on their arrival time
        self.jobs = sorted(self.jobs, key=lambda job:job.at)

        for job in self.jobs:
            start = self.timer
            while job.rt > 0: # keep processing the job until completion
                self.process_job(job)
            end = self.timer

            self.complete_job(job)
            self.update_gantt(job.id, start, end)

class SJF(Scheduler):
    """
        Shortest Job First
    """
    def __init__(self, jobs, preempt=False):
        super().__init__(jobs)
        self.preempt = preempt
    
    def start(self):
        # sort jobs based on their burst time
        self.jobs = sorted(self.jobs, key=lambda job:job.bt)

        for job in self.jobs:
            start = self.timer
            while job.rt > 0:
                self.process_job(job)


            
            


            
