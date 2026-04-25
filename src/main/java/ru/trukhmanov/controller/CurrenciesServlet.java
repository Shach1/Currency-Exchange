package ru.trukhmanov.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.trukhmanov.exception.RowAlreadyExist;
import ru.trukhmanov.model.entity.Currency;
import ru.trukhmanov.service.CurrenciesService;
import ru.trukhmanov.service.dto.ErrorMassage;

import java.io.IOException;
import java.util.NoSuchElementException;

@WebServlet(name = "CurrenciesServlet", urlPatterns = "/currencies/*")
public class CurrenciesServlet extends RestServlet{
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

        try{
            var result = currenciesService.createCurrency(code, name, sign);
            resp.setStatus(201);
            out.println(gson.toJson(result));
        } catch (NoSuchFieldException e){
            resp.setStatus(400);
            out.println(gson.toJson(new ErrorMassage(e.getMessage())));
        } catch (RowAlreadyExist e){
            resp.setStatus(409);
            out.println(gson.toJson(new ErrorMassage(e.getMessage())));
        } catch (RuntimeException e){
            resp.setStatus(500);
            out.println(gson.toJson(new ErrorMassage("Database is unavailable")));
        }
    }

    private void processGetSpecificCurrency(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        var out = resp.getWriter();
        String pathVar = req.getPathInfo().substring(1);
        try{
            Currency result = currenciesService.getCurrencyByCode(pathVar);
            resp.setStatus(200);
            out.println(gson.toJson(result));
        } catch (NoSuchElementException e){
            resp.setStatus(404);
            out.println(gson.toJson(new ErrorMassage(e.getMessage())));
        } catch (RuntimeException e){
            resp.setStatus(500);
            out.println(gson.toJson(new ErrorMassage("Database is unavailable")));
        }
    }

    private void processGetCurrencies(HttpServletResponse resp) throws IOException{
        var out = resp.getWriter();
        try{
            var result = currenciesService.getAllCurrencies();
            resp.setStatus(200);
            out.println(gson.toJson(result));
        } catch (RuntimeException e){
            resp.setStatus(500);
            out.println(gson.toJson(new ErrorMassage("Database is unavailable")));
        }
    }
}
