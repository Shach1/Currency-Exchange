package ru.trukhmanov.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.trukhmanov.service.CurrenciesService;
import ru.trukhmanov.dto.request.CreateCurrencyRequest;
import ru.trukhmanov.dto.response.CurrencyResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CurrenciesServlet", urlPatterns = "/currencies")
public class CurrenciesServlet extends HttpServlet{
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
        List<CurrencyResponse> result = currenciesService.getAllCurrencies();
        resp.setStatus(200);
        out.println(gson.toJson(result));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        var out = resp.getWriter();

        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");
        var request = new CreateCurrencyRequest(code, name, sign);

        CurrencyResponse result = currenciesService.createCurrency(request);
        resp.setStatus(201);
        out.println(gson.toJson(result));
    }
}