package com.misterioes.currenciesapplication.di

import com.misterioes.currenciesapplication.data.CurrenciesRepositoryImpl
import com.misterioes.currenciesapplication.domain.repository.CurrenciesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun provideCurrencyRepository(repository: CurrenciesRepositoryImpl): CurrenciesRepository
}