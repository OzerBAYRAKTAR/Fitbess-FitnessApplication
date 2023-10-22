package com.bayraktar.healthybackandneck.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.bayraktar.healthybackandneck.data.Repository.RoomRepository
import com.bayraktar.healthybackandneck.data.Room.AppDatabase
import com.bayraktar.healthybackandneck.data.Room.MovesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {



    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context):AppDatabase =
        Room.databaseBuilder(context,AppDatabase::class.java,"fitness_db")
            .build()


    @Provides
    fun providesMovesdao(appDb: AppDatabase):MovesDao =
        appDb.moveDao()

    @Provides
    fun providesPostRepository(moveDao: MovesDao):RoomRepository =
        RoomRepository(moveDao)
}