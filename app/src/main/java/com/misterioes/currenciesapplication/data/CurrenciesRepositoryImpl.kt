package com.misterioes.currenciesapplication.data

import com.misterioes.currenciesapplication.domain.model.Currency
import com.misterioes.currenciesapplication.domain.model.CurrencyDto
import com.misterioes.currenciesapplication.domain.model.Rate
import com.misterioes.currenciesapplication.domain.model.Result
import com.misterioes.currenciesapplication.domain.repository.CurrenciesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrenciesRepositoryImpl @Inject constructor(
    private val service: CurrenciesService,
    private val currenciesDao: CurrenciesDao
) : CurrenciesRepository {

    override suspend fun getCurrency(symbols: String, base: String): Flow<Result> = flow {
        try {
            service.let {
                val response = toCurrency(it.getCurrency(symbols, base))
                emit(Result.Success(response))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun getAllRates(): Flow<List<Rate>> {
        return flow {
            currenciesDao.getAllRates().collect {
                emit(it.map { toRate(it) })
            }
        }
    }

    override suspend fun upsert(rate: Rate) {
        currenciesDao.upsertRate(toRateDto(rate))
    }

    override suspend fun delete(rate: Rate) {
        currenciesDao.deleteRate(toRateDto(rate))
    }

    fun toRateDto(rate: Rate): RateEntity {
        return RateEntity(
            base = rate.base,
            symbol = rate.symbol,
            rate = rate.rate,
            selected = rate.selected
        )
    }

    fun toRate(rate: RateEntity): Rate {
        return Rate(
            base = rate.base,
            symbol = rate.symbol,
            rate = rate.rate,
            selected = rate.selected
        )
    }

    fun toCurrency(currencyDto: CurrencyDto): Currency {
        val hashSet = HashSet<Rate>()

        currencyDto.rates.forEach { s, d ->
            hashSet.add(
                Rate(currencyDto.base, s, d, false)
            )
        }

        return Currency(currencyDto.base, hashSet)
    }
}