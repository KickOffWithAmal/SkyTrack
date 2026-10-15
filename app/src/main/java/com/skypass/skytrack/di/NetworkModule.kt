package com.skypass.skytrack.di

import com.skypass.skytrack.BuildConfig
import com.skypass.skytrack.data.local.SessionDataStore
import com.skypass.skytrack.data.remote.SkyTrackApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(sessionDataStore: SessionDataStore): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        // Intercepts any API response: if error is invalid_session, immediately clears session to trigger login screen
        val sessionValidationInterceptor = Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val responseBody = response.body
            if (responseBody != null) {
                try {
                    val source = responseBody.source()
                    source.request(Long.MAX_VALUE)
                    val buffer = source.buffer.clone()
                    val jsonString = buffer.readUtf8()
                    if (jsonString.contains("invalid_session", ignoreCase = true) ||
                        jsonString.contains("session_expired", ignoreCase = true)
                    ) {
                        runBlocking {
                            sessionDataStore.clear()
                        }
                    }
                } catch (_: Exception) {
                    // Ignore parsing error, response continues normally
                }
            }
            response
        }

        return OkHttpClient.Builder()
            .addInterceptor(sessionValidationInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://example.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideSkyTrackApi(retrofit: Retrofit): SkyTrackApi =
        retrofit.create(SkyTrackApi::class.java)

    @Provides
    @Singleton
    fun provideConnectivityObserver(
        networkConnectivityObserver: com.skypass.skytrack.data.local.NetworkConnectivityObserver
    ): com.skypass.skytrack.data.local.ConnectivityObserver = networkConnectivityObserver
}
