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

import java.io.IOException;

@WebServlet(name = "ExchangeServlet", urlPatterns = "/exchange")
public class ExchangeServlet extends HttpServlet{
    private ExchangeService exchangeService;
    private Gson gson;

    @Override
    public void init(ServletConfig config) throws ServletException{
        super.init(config);
        exchangeService = new ExchangeService();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        var out = resp.getWriter();

        var baseCurrencyCode = req.getParameter("from");
        var targetCurrencyCode = req.getParameter("to");
        var amount = req.getParameter("amount");

        var result = exchangeService.calculateExchange(new ExchangeRequest(baseCurrencyCode, targetCurrencyCode, amount));
        resp.setStatus(200);
        out.println(gson.toJson(result));
    }
}
