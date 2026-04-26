package ru.trukhmanov.controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.trukhmanov.service.CurrenciesService;

import java.io.IOException;

@WebServlet(name = "CurrenciesServlet", urlPatterns = "/currencies/*")
public class CurrenciesServlet extends RestServlet{
    private CurrenciesService currenciesService;

    @Override
    public void init(ServletConfig config) throws ServletException{
        super.init(config);
        currenciesService = new CurrenciesService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        configureRestResponse(resp);

        if(req.getPathInfo() != null) processGetSpecificCurrency(req, resp);
        else processGetCurrencies(resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        configureRestResponse(resp);
        var out = resp.getWriter();

        var name = req.getParameter("name");
        var code = req.getParameter("code");
        var sign = req.getParameter("sign");

        var result = currenciesService.createCurrency(code, name, sign);
        resp.setStatus(201);
        out.println(gson.toJson(result));
    }

    private void processGetSpecificCurrency(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        var out = resp.getWriter();
        String pathVar = req.getPathInfo().substring(1);
        var result = currenciesService.getCurrencyByCode(pathVar);
        resp.setStatus(200);
        out.println(gson.toJson(result));
    }

    private void processGetCurrencies(HttpServletResponse resp) throws IOException{
        var out = resp.getWriter();
        var result = currenciesService.getAllCurrencies();
        resp.setStatus(200);
        out.println(gson.toJson(result));
    }
}
