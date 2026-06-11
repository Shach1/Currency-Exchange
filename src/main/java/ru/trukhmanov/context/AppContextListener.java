package ru.trukhmanov.context;

import com.google.gson.Gson;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.trukhmanov.dao.CurrenciesDao;
import ru.trukhmanov.dao.CurrenciesDaoImpl;
import ru.trukhmanov.dao.ExchangeRatesDao;
import ru.trukhmanov.dao.ExchangeRatesDaoImpl;
import ru.trukhmanov.service.*;
import ru.trukhmanov.util.DatabaseConnectionProvider;
import ru.trukhmanov.util.MigrationExecutor;

@WebListener
public class AppContextListener implements ServletContextListener{

    @Override
    public void contextInitialized(ServletContextEvent sce){
        ServletContextListener.super.contextInitialized(sce);
        var context = sce.getServletContext();


        CurrenciesDao currenciesDao = new CurrenciesDaoImpl();
        ExchangeRatesDao exchangeRatesDao = new ExchangeRatesDaoImpl();

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
        DatabaseConnectionProvider.closeDataSource();
    }
}