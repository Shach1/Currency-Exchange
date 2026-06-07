package ru.trukhmanov.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.trukhmanov.service.ExchangeService;
import ru.trukhmanov.service.dto.request.ExchangeRequest;
import ru.trukhmanov.service.dto.response.ExchangeResponse;

import java.io.IOException;

@WebServlet(name = "ExchangeServlet", urlPatterns = "/exchange")
public class ExchangeServlet extends HttpServlet{
    private ExchangeService exchangeService;
    private Gson gson;

    @Override
    public void init(ServletConfig config) throws ServletException{
        super.init(config);
        exchangeService = (ExchangeService) getServletContext().getAttribute("exchangeService");
        gson = (Gson) getServletContext().getAttribute("gson");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        var out = resp.getWriter();

        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        String amount = req.getParameter("amount");
        var request = new ExchangeRequest(baseCurrencyCode, targetCurrencyCode, amount);

        ExchangeResponse result = exchangeService.calculateExchange(request);
        resp.setStatus(200);
        out.println(gson.toJson(result));
    }
}
