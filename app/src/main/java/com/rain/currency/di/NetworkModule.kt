package com.rain.currency.di

import com.google.gson.Gson
import com.rain.currency.BuildConfig
import com.rain.currency.data.network.CurrencyApi
import com.rain.currency.di.application.ApplicationScope
import com.rain.currency.utils.Constant
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class NetworkModule {

    @Provides
    @ApplicationScope
    fun provideHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loggingInterceptor)
        }

        return builder.addInterceptor(accessTokenInterceptor())
                .build()
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

    @Provides
    @ApplicationScope
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    @Provides
    @ApplicationScope
    fun provideCurrencyRepo(retrofit: Retrofit): CurrencyApi {
        return retrofit.create(CurrencyApi::class.java)
    }
}
