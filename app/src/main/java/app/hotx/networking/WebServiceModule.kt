package app.hotx.networking

import android.content.SharedPreferences
import app.hotx.BuildConfig
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Created by Grigory Azaryan on 11/30/18.
 */


@Module
class WebServiceModule {

    val BASE_URL = "https://www.zzz.biz"
    val PH_BASE_URL = "https://api.pornhub.com/api_android_v3/"

    @Provides
    @Singleton
    fun provideWebService(preferences: SharedPreferences): WebService {

        val clientBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG)
            clientBuilder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        clientBuilder.addInterceptor(HeaderInterceptor(preferences))
                .readTimeout(10, TimeUnit.SECONDS)

        val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(setSslSocketFactory(clientBuilder).build())
                .build()

        return retrofit.create(WebService::class.java)
    }

    private fun setSslSocketFactory(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory

            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }
            return builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    @Provides
    @Singleton
    fun providePhWebService(): PHWebService {

        val clientBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG)
            clientBuilder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        clientBuilder.readTimeout(5, TimeUnit.SECONDS)

        val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(PH_BASE_URL)
                .client(clientBuilder.build())
                .build()

        return retrofit.create(PHWebService::class.java)
    }

    //    @Provides
    //    @Singleton
    //    public ParseLinkWebService provideParseLinkWebService() {
    //
    //        Retrofit retrofit = new Retrofit.Builder()
    //                .baseUrl("https://www.google.com/")
    //                .addConverterFactory(ScalarsConverterFactory.create())
    //                .addConverterFactory(GsonConverterFactory.create())
    ////                .client(new OkHttpClient.Builder()
    ////                        .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
    ////                        .build())
    //                .build();
    //
    //        return retrofit.create(ParseLinkWebService.class);
    //    }
}
