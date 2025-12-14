package broz.tito.usadebt.data.remote


import android.util.Log
import broz.tito.usadebt.model.*
import broz.tito.usadebt.data.remote.parsers.EnglishParser
import broz.tito.usadebt.data.remote.parsers.Parser
import retrofit2.Retrofit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.converter.gson.GsonConverterFactory
import broz.tito.usadebt.data.remote.parsers.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class Model @Inject constructor(
            @param:Named("base")
            val baseService: BaseRemoteService,
            @param:Named("currency")
            val currencyV1Service: BaseRemoteService,
            val currencyV2Service: CurrencyRemoteService) {

    val TAG = "Model"
    var parser: Parser = EnglishParser()

    suspend fun getDebt(): Flow<DebtResult> = flow {
        Log.d(TAG, "getting Debt")
        var result: DebtResult = PendingDebtResult()
        emit(result)
        try {
            val response = baseService.getDebt()
            if (!response.isSuccessful) {
                result = FailureDebtResult()
            }
            else {
                response.body()?.let {
                    val debt = it
                    result = SuccessRawDebtResult(debt)
                }
            }
        }
        catch (e: Exception) {
            Log.d(TAG, "getDebt: $e")
            result = FailureDebtResult()
        }
        emit(result)
    }.flowOn(Dispatchers.IO)

    // Currency V1, mentioned in domain/data layer as currency_V1
    suspend fun getCurrency(): Flow<CurrencyResult> = flow {
        emit(PendingCurrencyResult())
       try {
           val response = currencyV1Service.getCurrency()
           if (!response.isSuccessful) {
               emit(FailureCurrencyResult())
           }
           else {
               response.body()?.let {
                   emit(SuccessRawCurrencyResult(it))
               }
           }
       }
       catch (e: Exception) {
           emit(FailureCurrencyResult())
       }
    }.flowOn(Dispatchers.IO)

    suspend fun getCurrencyV2(): Flow<CurrencyV2Result> = flow {
        var result : CurrencyV2Result = PendingCurrencyV2Result()
        emit(result)
        try {
            val response = currencyV2Service.getCurrency_v2()
            if (!response.isSuccessful) {
                result = FailureCurrencyV2Result()
            }
            else {
                response.body()?.let {
                    result = SuccessRawCurrencyV2Result(it)
                }
            }
        }
        catch (e: Exception) {
            result = FailureCurrencyV2Result()
        }
        emit(result)
    }.flowOn(Dispatchers.IO)

    suspend fun getHistory(days: String): Flow<HistoryResult> = flow {
        var result : HistoryResult = PendingHistoryResult()
        emit(result)
        try {
            val response = baseService.getHistory(days)
            if (!response.isSuccessful) {
                result = FailureHistoryResult()
            }
            else {
                response.body()?.let { body ->
                    val res = parseFromRawHistory(body,this@Model)
                    result = SuccessHistoryResult(res)
                }
            }
        }
        catch (e: Exception) {
            result = FailureHistoryResult()
        }
        emit(result)
    }.flowOn(Dispatchers.IO)

}