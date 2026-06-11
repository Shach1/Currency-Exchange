package ru.trukhmanov.context;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.trukhmanov.util.MigrationExecutor;

@WebListener
public class MigrationListener implements ServletContextListener{
    @Override
    public void contextInitialized(ServletContextEvent sce){
        ServletContextListener.super.contextInitialized(sce);

        new MigrationExecutor("init.sql").migrate();
    }
}
