package com.example.colorapp.di

import android.content.Context
import androidx.room.Room
import com.example.colorapp.data.AppDatabase
import com.example.colorapp.data.ColorDao
import com.example.colorapp.data.ColorRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ColorModule {

    @Provides
    @Singleton
    fun provideColorDao(database: AppDatabase): ColorDao {
        return database.colorDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideColorRepository(colorDao: ColorDao): ColorRepository {
        return ColorRepository(colorDao)
    }
}