package ru.trukhmanov.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.trukhmanov.service.ExchangeRatesService;
import ru.trukhmanov.service.dto.ErrorMassage;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.NoSuchElementException;

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
            var result = ratesService.getExchangeRateByCodePaid(pathVar);
            resp.setStatus(200);
            out.println(gson.toJson(result));
        } catch (InvalidPropertiesFormatException e){
            resp.setStatus(400);
            out.println(gson.toJson(e.getMessage()));
        } catch (NoSuchElementException e){
            resp.setStatus(404);
            out.println(gson.toJson(e.getMessage()));
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        configureRestResponse(resp);
        super.doPost(req, resp);
    }
}
