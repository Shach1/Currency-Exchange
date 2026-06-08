package ru.trukhmanov.filter;

import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.trukhmanov.exception.*;
import ru.trukhmanov.dto.response.ErrorMessage;

import java.io.IOException;

@WebFilter(filterName = "ExceptionHandlingFilter",
        servletNames = {
                "CurrenciesServlet",
                "CurrencyServlet",
                "ExchangeRateServlet",
                "ExchangeRatesServlet",
                "ExchangeServlet"})
public class ExceptionHandlingFilter extends HttpFilter{
    private Gson gson;

    @Override
    public void init() throws ServletException{
        super.init();
        gson = (Gson) getServletContext().getAttribute("gson");
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException{
        var out = res.getWriter();
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try{
            super.doFilter(req, res, chain);
        } catch (ValidationException e){
            res.setStatus(400);
            out.println(gson.toJson(new ErrorMessage(e.getMessage())));
        } catch (EntityNotFoundException e){
            res.setStatus(404);
            out.println(gson.toJson(new ErrorMessage(e.getMessage())));
        } catch (EntityAlreadyExistException e){
            res.setStatus(409);
            out.println(gson.toJson(new ErrorMessage(e.getMessage())));
        } catch (DatabaseException e){
            res.setStatus(500);
            out.println(gson.toJson(new ErrorMessage(e.getMessage())));
        } catch (Exception e){
            res.setStatus(500);
            out.println(gson.toJson(new ErrorMessage("Unknown error")));
        }
    }
}
