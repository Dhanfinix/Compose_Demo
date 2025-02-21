package edts.android.composedemo.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edts.android.composedemo.data.local.DemoLocalDataSource
import edts.android.composedemo.data.local.SettingsLocalStorage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {
    @Provides
    @Singleton
    fun provideSettingsLocalStorage(@ApplicationContext context: Context) =
        SettingsLocalStorage(context)

    @Provides
    @Singleton
    fun provideDemoApiLocalStorage(@ApplicationContext context: Context) =
        DemoLocalDataSource(context)
}