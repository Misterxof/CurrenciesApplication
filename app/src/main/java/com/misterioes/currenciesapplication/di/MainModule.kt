package com.misterioes.currenciesapplication.di

import android.content.Context
import androidx.room.Room
import com.misterioes.currenciesapplication.data.CurrenciesDao
import com.misterioes.currenciesapplication.data.CurrenciesDatabase
import com.misterioes.currenciesapplication.data.CurrenciesService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MainModule {
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(
                provideInterceptor()
            )
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    fun provideInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original: Request = chain.request()
            val requestBuilder: Request.Builder = original.newBuilder()
                .addHeader("apikey", "Zw6i9UPopxHUGV8yXMdH8s1dKz4Xj5qi")

            val request: Request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun provideCurrenciesService(
        okHttpClient: OkHttpClient
    ): CurrenciesService {
        return Retrofit.Builder()
            .baseUrl("https://api.apilayer.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrenciesService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): CurrenciesDatabase {
        return Room.databaseBuilder(appContext, CurrenciesDatabase::class.java, "currencies")
            .build()
    }

    @Provides
    @Singleton
    fun provideTransferDao(appDatabase: CurrenciesDatabase): CurrenciesDao {
        return appDatabase.getDao()
    }
}