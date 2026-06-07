package ru.trukhmanov.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.trukhmanov.service.CurrenciesService;
import ru.trukhmanov.service.dto.response.CurrencyResponse;

import java.io.IOException;

@WebServlet(name = "CurrencyServlet", urlPatterns = "/currency/*")
public class CurrencyServlet extends HttpServlet{
    private CurrenciesService currenciesService;
    private Gson gson;

    @Override
    public void init(ServletConfig config) throws ServletException{
        super.init(config);
        currenciesService = (CurrenciesService) getServletContext().getAttribute("currenciesService");
        gson = (Gson) getServletContext().getAttribute("gson");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        var out = resp.getWriter();
        String pathVar = req.getPathInfo().substring(1);

        CurrencyResponse result = currenciesService.getCurrencyByCode(pathVar);
        resp.setStatus(200);
        out.println(gson.toJson(result));
    }
}
