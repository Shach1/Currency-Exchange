package ru.trukhmanov.controller;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;

public class RestServlet extends HttpServlet {
    protected final Gson gson = new Gson();
    protected final void configureRestResponse(HttpServletResponse resp){
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }
}
