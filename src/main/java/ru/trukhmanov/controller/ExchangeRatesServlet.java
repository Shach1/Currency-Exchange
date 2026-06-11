package ru.trukhmanov.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.trukhmanov.dto.request.CreateExchangeRateRequestDto;
import ru.trukhmanov.dto.response.ExchangeRateDto;
import ru.trukhmanov.service.ExchangeRatesService;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ExchangeRatesServlet", urlPatterns = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet{
    private ExchangeRatesService ratesService;
    private Gson gson;

    @Override
    public void init(ServletConfig config) throws ServletException{
        super.init(config);
        ratesService = (ExchangeRatesService) getServletContext().getAttribute("exchangeRatesService");
        gson = (Gson) getServletContext().getAttribute("gson");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        var out = resp.getWriter();
        List<ExchangeRateDto> result = ratesService.getAllExchangeRates();
        resp.setStatus(200);
        out.println(gson.toJson(result));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        var out = resp.getWriter();

        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");
        var request = new CreateExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, rate);

        ExchangeRateDto result = ratesService.createExchangeRate(request);
        resp.setStatus(201);
        out.println(gson.toJson(result));
    }
}
