package dhandev.android.composedemo.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dhandev.android.composedemo.data.local.SettingsLocalStorage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {
    @Provides
    @Singleton
    fun provideSettingsLocalStorage(@ApplicationContext context: Context) =
        SettingsLocalStorage(context)
}