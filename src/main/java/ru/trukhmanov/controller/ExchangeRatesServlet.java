package ru.trukhmanov.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.trukhmanov.exception.InvalidRequestFormat;
import ru.trukhmanov.service.dto.request.CreateExchangeRateRequest;
import ru.trukhmanov.service.ExchangeRatesService;
import ru.trukhmanov.service.dto.request.UpdateExchangeRateRequest;

import java.io.IOException;

@WebServlet(name = "ExchangeRatesServlet", urlPatterns = {"/exchangeRates", "/exchangeRate/*"})
public class ExchangeRatesServlet extends HttpServlet{
    private ExchangeRatesService ratesService;
    private Gson gson;

    @Override
    public void init(ServletConfig config) throws ServletException{
        super.init(config);
        ratesService = new ExchangeRatesService();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        if(req.getPathInfo() != null) processGetSpecificExchangeRate(req, resp);
        else processGetExchangeRates(resp);
    }

    private void processGetSpecificExchangeRate(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        var out = resp.getWriter();
        String codePair = req.getPathInfo().substring(1);

        var result = ratesService.getExchangeRate(codePair);
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
        var out = resp.getWriter();

        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");
        CreateExchangeRateRequest request = new CreateExchangeRateRequest(baseCurrencyCode, targetCurrencyCode, rate);

        var result = ratesService.createExchangeRate(request);
        resp.setStatus(201);
        out.println(gson.toJson(result));
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        var out = resp.getWriter();

        if(req.getPathInfo() == null) throw new InvalidRequestFormat();
        var codePair = req.getPathInfo().substring(1);
        String rate = req.getParameter("rate");

        var result = ratesService.updateExchangeRate(new UpdateExchangeRateRequest(codePair, rate));
        resp.setStatus(200);
        out.println(gson.toJson(result));
    }
}
