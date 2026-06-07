package ru.trukhmanov.context;

import com.google.gson.Gson;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.trukhmanov.model.dao.CurrenciesDao;
import ru.trukhmanov.model.dao.ExchangeRatesDao;
import ru.trukhmanov.service.*;

@WebListener
public class AppContextListener implements ServletContextListener{

    @Override
    public void contextInitialized(ServletContextEvent sce){
        ServletContextListener.super.contextInitialized(sce);
        var context = sce.getServletContext();

        CurrenciesDao currenciesDao = new CurrenciesDao();
        ExchangeRatesDao exchangeRatesDao = new ExchangeRatesDao();

        CurrenciesService currenciesService = new CurrenciesServiceImpl(currenciesDao);
        ExchangeRatesService exchangeRatesService = new ExchangeRatesServiceImpl(exchangeRatesDao, currenciesService);
        ExchangeService exchangeService = new ExchangeServiceImpl(exchangeRatesService, currenciesService);

        Gson gson = new Gson();

        context.setAttribute("currenciesService", currenciesService);
        context.setAttribute("exchangeRatesService", exchangeRatesService);
        context.setAttribute("exchangeService", exchangeService);
        context.setAttribute("gson", gson);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce){
        ServletContextListener.super.contextDestroyed(sce);
    }
}