package edts.android.composedemo.di

import android.content.Context
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edts.android.composedemo.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RestNetworkModule {
    @Provides
    @Singleton
    @Named("AuthInterceptor")
    fun provideAuthInterceptor(): Interceptor = Interceptor {chain->
        val request = try {
            val apiKeyField = BuildConfig::class.java.getField("API_KEY")
            (apiKeyField.get(null) as? String)?.let { apiKey ->
                chain.request().newBuilder()
                    .addHeader("X-API-KEY", apiKey)
                    .addHeader("Accept", "application/json")
                    .build()
            } ?: chain.request().newBuilder().build()
        } catch (e: NoSuchFieldException) {
            chain.request().newBuilder().build()
        }
        chain.proceed(request)
    }

    @Provides
    @Singleton
    @Named("ConnectivityInterceptor")
    fun provideConnectivityInterceptor(@ApplicationContext context: Context): Interceptor =
        Interceptor { chain ->
            if (!isNetworkAvailable(context)) {
                throw NoConnectivityException("No internet connection")
            }
            chain.proceed(chain.request())
        }

    private fun isNetworkAvailable(context: Context): Boolean{
        val connectionManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectionManager.activeNetwork ?: return false
        val capabilites = connectionManager.getNetworkCapabilities(networkInfo) ?: return false
        return capabilites.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Custom exception class for network connectivity issues
    class NoConnectivityException(message: String) : IOException(message)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @Named("AuthInterceptor")authInterceptor : Interceptor,
        @Named("ConnectivityInterceptor")connectivityInterceptor : Interceptor
    ): OkHttpClient{
        val timeOut = 30L
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(connectivityInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .retryOnConnectionFailure(true)
            .connectTimeout(timeOut, TimeUnit.SECONDS)
            .readTimeout(timeOut, TimeUnit.SECONDS)
            .writeTimeout(timeOut, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit{
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}