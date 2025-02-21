package edts.android.composedemo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edts.android.composedemo.data.remote.data_source.DemoRemoteDataSource
import edts.android.composedemo.data.remote.network.ApiDemoService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {
    @Provides
    @Singleton
    fun provideDemoApiService(
        retrofit: Retrofit
    ): ApiDemoService =
        retrofit.create(ApiDemoService::class.java)

    @Provides
    @Singleton
    fun provideDemoRemoteDataSource(
        demoService: ApiDemoService
    ) = DemoRemoteDataSource(demoService)
}