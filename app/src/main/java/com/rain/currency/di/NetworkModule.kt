package com.rain.currency.di

import android.content.Context
import com.google.gson.Gson
import com.rain.currency.BuildConfig
import com.rain.currency.data.api.CurrencyApi
import com.rain.currency.support.NetworkManager
import com.rain.currency.utils.Constant
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

@Module
object NetworkModule {

    @JvmStatic
    @Provides
    @ApplicationScope
    fun provideHttpClient(networkManager: NetworkManager, context: Context): OkHttpClient {
        val builder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loggingInterceptor)
        }

        builder.networkInterceptors().add(cacheInterceptor(networkManager))

        return builder.addInterceptor(accessTokenInterceptor())
                .cache(getCache(context.cacheDir))
                .build()
    }

    private fun getCache(cacheDir: File): Cache {
        val httpCacheDirectory = File(cacheDir, "responses")
        val cacheSize = 10 * 1024 * 1024L
        return Cache(httpCacheDirectory, cacheSize)
    }

    private fun accessTokenInterceptor(): Interceptor {
        return Interceptor {
            val url = it.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter(Constant.ACCESS_TOKEN, BuildConfig.ACCESS_TOKEN)
                    .build()

            val request = it.request()
                    .newBuilder()
                    .url(url)
                    .build()

            return@Interceptor it.proceed(request)
        }
    }

    private fun cacheInterceptor(networkManager: NetworkManager): Interceptor {
        return Interceptor {
            val originalResponse = it.proceed(it.request())
            val maxAge = 60 * 30

            return@Interceptor if (networkManager.isNetworkAvailable()) {
                originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=$maxAge")
                        .build()
            } else {
                originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=$maxAge")
                        .build()
            }
        }
    }

    @JvmStatic
    @Provides
    @ApplicationScope
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    @JvmStatic
    @Provides
    @ApplicationScope
    fun provideCurrencyApi(retrofit: Retrofit): CurrencyApi {
        return retrofit.create(CurrencyApi::class.java)
    }
}
