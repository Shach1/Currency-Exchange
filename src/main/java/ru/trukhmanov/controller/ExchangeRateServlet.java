package ru.trukhmanov.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.trukhmanov.dto.request.UpdateExchangeRateRequestDto;
import ru.trukhmanov.dto.response.ExchangeRateDto;
import ru.trukhmanov.exception.ValidationException;
import ru.trukhmanov.mapper.ExchangeRateMapper;
import ru.trukhmanov.service.ExchangeRatesService;

import java.io.IOException;

@WebServlet(name = "ExchangeRateServlet", urlPatterns = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet{
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
        String codePair = req.getPathInfo().substring(1).trim();

        ExchangeRateDto result = ExchangeRateMapper.INSTANCE.toExchangeRateDto(ratesService.getExchangeRateByCodePair(codePair));
        resp.setStatus(200);
        out.println(gson.toJson(result));
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        var out = resp.getWriter();

        if (req.getPathInfo() == null) throw new ValidationException("Invalid request format");
        String codePair = req.getPathInfo().substring(1).trim();
        String rate = req.getParameter("rate").trim();
        var request = new UpdateExchangeRateRequestDto(codePair, rate);

        ExchangeRateDto result = ExchangeRateMapper.INSTANCE.toExchangeRateDto(ratesService.updateExchangeRate(request));
        resp.setStatus(200);
        out.println(gson.toJson(result));
    }
}
