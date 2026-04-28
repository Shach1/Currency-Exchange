package ru.trukhmanov.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.trukhmanov.service.dto.request.CreateCurrencyRequest;
import ru.trukhmanov.service.CurrenciesService;

import java.io.IOException;

@WebServlet(name = "CurrenciesServlet", urlPatterns = {"/currencies", "/currency/*"})
public class CurrenciesServlet extends HttpServlet{
    private CurrenciesService currenciesService;
    private Gson gson;

    @Override
    public void init(ServletConfig config) throws ServletException{
        super.init(config);
        currenciesService = new CurrenciesService();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        if(req.getPathInfo() != null) processGetSpecificCurrency(req, resp);
        else processGetCurrencies(resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        var out = resp.getWriter();

        var name = req.getParameter("name");
        var code = req.getParameter("code");
        var sign = req.getParameter("sign");
        var request = new CreateCurrencyRequest(code, name, sign);

        var result = currenciesService.createCurrency(request);
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
