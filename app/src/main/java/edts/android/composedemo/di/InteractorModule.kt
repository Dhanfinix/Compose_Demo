package edts.android.composedemo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import edts.android.composedemo.domain.irepository.IDemoRepository
import edts.android.composedemo.domain.irepository.ISettingsRepository
import edts.android.composedemo.domain.usecase.DemoInteractor
import edts.android.composedemo.domain.usecase.DemoUseCase
import edts.android.composedemo.domain.usecase.SettingsInteractor
import edts.android.composedemo.domain.usecase.SettingsUseCase

@Module
@InstallIn(ViewModelComponent::class)
object InteractorModule {
    @Provides
    fun provideDemoUseCase(
        demoRepository: IDemoRepository
    ): DemoUseCase = DemoInteractor(demoRepository)

    @Provides
    fun provideSettingsUseCase(
        settingsRepository: ISettingsRepository
    ): SettingsUseCase = SettingsInteractor(settingsRepository)
}