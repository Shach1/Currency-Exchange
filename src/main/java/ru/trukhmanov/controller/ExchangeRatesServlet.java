package ru.trukhmanov.controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.trukhmanov.service.ExchangeRatesService;

import java.io.IOException;

@WebServlet(name = "ExchangeRatesServlet", urlPatterns = "/exchangeRates/*")
public class ExchangeRatesServlet extends RestServlet{
    private ExchangeRatesService ratesService;

    @Override
    public void init(ServletConfig config) throws ServletException{
        super.init(config);
        ratesService = new ExchangeRatesService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        configureRestResponse(resp);

        if(req.getPathInfo() != null) processGetSpecificExchangeRate(req, resp);
        else processGetExchangeRates(resp);
    }

    private void processGetSpecificExchangeRate(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        var out = resp.getWriter();
        String pathVar = req.getPathInfo().substring(1);

        var result = ratesService.getExchangeRateByCodePair(pathVar);
        resp.setStatus(200);
        out.println(gson.toJson(result));
    }

    private void processGetExchangeRates(HttpServletResponse resp) throws IOException{
        var out = resp.getWriter();
        var result = ratesService.getAllExchangeRates();
        resp.setStatus(200);
        out.println(gson.toJson(result));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        configureRestResponse(resp);
        var out = resp.getWriter();

        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        var result = ratesService.createExchangeRateByCodePair(baseCurrencyCode, targetCurrencyCode, rate);
        resp.setStatus(201);
        out.println(gson.toJson(result));
    }
}
