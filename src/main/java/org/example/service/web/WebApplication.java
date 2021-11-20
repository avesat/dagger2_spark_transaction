package org.example.service.web;


import org.example.config.DataSourceModule;
import org.example.config.MapperModule;
import org.example.repository.AccountDaoModule;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = { DataSourceModule.class, MapperModule.class, AccountDaoModule.class})
@Singleton
public interface WebApplication {
    WebServer webServer();
}
