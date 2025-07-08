package com.example.apptutorias.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val BASE_URL: String
        get() = if (
            android.os.Build.MODEL.contains("sdk_gphone", ignoreCase = true) ||
            android.os.Build.FINGERPRINT.contains("generic", ignoreCase = true)
        ) {
            "http://10.0.2.2:8181/api/"
        } else {
           // "http://192.168.1.248:8181/api/"
            "http://172.20.10.3:8181/api/"
        }

    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()

        TokenProvider.token?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        chain.proceed(requestBuilder.build())
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val apiService: TutoriasApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TutoriasApiService::class.java)
    }
}
