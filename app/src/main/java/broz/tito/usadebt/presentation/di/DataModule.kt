package broz.tito.usadebt.presentation.di

import broz.tito.usadebt.data.remote.BaseRemoteService
import broz.tito.usadebt.data.remote.CurrencyRemoteService
import broz.tito.usadebt.data.remote.Model
import dagger.Module
import dagger.Provides
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Named("base")
    fun provideBaseRemoteService(okHttpClient: OkHttpClient): BaseRemoteService {


        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v2/accounting/od/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(BaseRemoteService::class.java)
    }

    @Provides
    @Named("currency")
    fun provideCurrencyV1RemoteService(okHttpClient: OkHttpClient): BaseRemoteService {
        val retrofit1 = Retrofit.Builder()
            .baseUrl("https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit1.create(BaseRemoteService::class.java)
    }

    @Provides
    fun provideCurrencyV2RemoteService(okHttpClient: OkHttpClient): CurrencyRemoteService {
        val retrofit2 = Retrofit.Builder()
            .baseUrl("https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit2.create(CurrencyRemoteService::class.java)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC // Показывает URL
        }

        val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .allEnabledTlsVersions()
            .allEnabledCipherSuites()
            .build()

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .connectionSpecs(listOf(spec))
            .build()
    }

}