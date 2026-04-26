package ru.trukhmanov.controller;

import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.trukhmanov.exception.*;
import ru.trukhmanov.service.dto.ErrorMessage;

import java.io.IOException;

@WebFilter(filterName = "RestServlet", servletNames = {"CurrenciesServlet", "ExchangeRatesServlet"})
public class RestFilter extends HttpFilter{
    Gson gson = new Gson();

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException{
        var out = res.getWriter();
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try{
            super.doFilter(req, res, chain);
        } catch (InvalidRequestFormat | InvalidValue | MissingFormField | IllegalArgumentException e){
            res.setStatus(400);
            out.println(gson.toJson(new ErrorMessage(e.getMessage())));
        } catch (CurrencyNotFound | ExchangeRateNotFound e){
            res.setStatus(404);
            out.println(gson.toJson(new ErrorMessage(e.getMessage())));
        } catch (CurrencyAlreadyExist | ExchangeRateAlreadyExist e){
            res.setStatus(409);
            out.println(gson.toJson(new ErrorMessage(e.getMessage())));
        } catch (DatabaseException | UnsuspectedException e){
            res.setStatus(500);
            out.println(gson.toJson(new ErrorMessage(e.getMessage())));
        } catch (Exception e){
            res.setStatus(500);
            out.println(gson.toJson(new ErrorMessage("Unknown error")));
        }
    }
}
