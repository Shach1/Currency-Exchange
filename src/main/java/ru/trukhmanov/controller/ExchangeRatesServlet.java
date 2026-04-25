package ru.trukhmanov.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.trukhmanov.exception.*;
import ru.trukhmanov.service.ExchangeRatesService;
import ru.trukhmanov.service.dto.ErrorMassage;

import java.io.IOException;

@WebServlet(name = "ExchangeRatesServlet", urlPatterns = "/exchangeRates/*")
public class ExchangeRatesServlet extends RestServlet{
    private Gson gson;
    private ExchangeRatesService ratesService;

    @Override
    public void init(ServletConfig config) throws ServletException{
        super.init(config);
        gson = new Gson();
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

        try{
            var result = ratesService.getExchangeRateByCodePair(pathVar);
            resp.setStatus(200);
            out.println(gson.toJson(result));
        } catch (InvalidRequestFormat e){
            resp.setStatus(400);
            out.println(gson.toJson(new ErrorMassage(e.getMessage())));
        } catch (ExchangeRateNotFound e){
            resp.setStatus(404);
            out.println(gson.toJson(new ErrorMassage(e.getMessage())));
        } catch (RuntimeException e){
            resp.setStatus(500);
            out.println(gson.toJson(new ErrorMassage("Database is unavailable")));
        }
    }

    private void processGetExchangeRates(HttpServletResponse resp) throws IOException{
        var out = resp.getWriter();
        try{
            var result = ratesService.getAllExchangeRates();
            resp.setStatus(200);
            out.println(gson.toJson(result));
        } catch (RuntimeException e){
            resp.setStatus(500);
            out.println(gson.toJson(new ErrorMassage("Database is unavailable")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        configureRestResponse(resp);
        var out = resp.getWriter();

        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        try{
            var result = ratesService.createExchangeRateByCodePair(baseCurrencyCode, targetCurrencyCode, rate);
            resp.setStatus(201);
            out.println(gson.toJson(result));
        } catch (MissingFormField | InvalidRequestFormat e){
            resp.setStatus(400);
            out.println(gson.toJson(new ErrorMassage(e.getMessage())));
        } catch (CurrencyNotFound e){
            resp.setStatus(404);
            out.println(gson.toJson(new ErrorMassage(e.getMessage())));
        } catch (ExchangeRateAlreadyExist e){
            resp.setStatus(409);
            out.println(gson.toJson(new ErrorMassage(e.getMessage())));
        } catch (RuntimeException e){
            resp.setStatus(500);
            out.println(gson.toJson(new ErrorMassage("Database is unavailable")));
        }
    }
}
