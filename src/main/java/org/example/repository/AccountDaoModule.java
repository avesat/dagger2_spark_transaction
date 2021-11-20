package org.example.repository;

import dagger.Binds;
import dagger.Module;


@Module
public abstract class AccountDaoModule {
    @Binds
    abstract AccountDao accountDao(AccountDaoImpl accountDao);
}
