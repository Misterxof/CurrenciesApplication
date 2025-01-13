package com.misterioes.currenciesapplication.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RateEntity::class], version = 1)
abstract class CurrenciesDatabase: RoomDatabase() {
    abstract fun getDao(): CurrenciesDao
}