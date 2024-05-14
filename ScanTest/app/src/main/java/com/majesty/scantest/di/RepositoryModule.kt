package com.majesty.scantest.di

import android.content.Context
import com.majesty.scantest.data.HopeLandRepository
import com.majesty.scantest.domain.RfidInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideRfidRepository(@ApplicationContext context: Context): RfidInterface = HopeLandRepository(context)
}