package org.example.config;

import org.h2.jdbcx.JdbcDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import dagger.Module;
import dagger.Provides;


@Module
public class DataSourceModule {

    @Inject
    public DataSourceModule() {

    }

    @Singleton
    @Provides
    public DataSource dataSource() {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setUrl(SecretManager.getDbUrl());
        jdbcDataSource.setUser(SecretManager.getUsername());
        jdbcDataSource.setPassword(SecretManager.getPassword());

        return jdbcDataSource;
    }

}
