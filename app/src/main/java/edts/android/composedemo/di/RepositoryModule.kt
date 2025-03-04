package edts.android.composedemo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edts.android.composedemo.data.local.DemoLocalDataSource
import edts.android.composedemo.data.local.SettingsLocalStorage
import edts.android.composedemo.data.remote.data_source.DemoRemoteDataSource
import edts.android.composedemo.data.repository.DemoRepository
import edts.android.composedemo.data.repository.SettingsRepository
import edts.android.composedemo.domain.irepository.IDemoRepository
import edts.android.composedemo.domain.irepository.ISettingsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideDemoRepository(
        demoRemoteDataSource: DemoRemoteDataSource,
        demoLocalDataSource: DemoLocalDataSource
    ): IDemoRepository = DemoRepository(demoRemoteDataSource, demoLocalDataSource)

    @Provides
    @Singleton
    fun provideSettingsRepository(
        settingsLocalStorage: SettingsLocalStorage
    ): ISettingsRepository = SettingsRepository(settingsLocalStorage)

}