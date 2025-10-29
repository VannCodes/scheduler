package com.scheduler;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;

public class TestServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        out.print("{\"test\": \"Hello World\", \"status\": \"success\"}");
        out.flush();
    }
}
