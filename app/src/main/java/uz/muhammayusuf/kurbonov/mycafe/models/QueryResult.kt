package uz.muhammayusuf.kurbonov.mycafe.models

import androidx.annotation.Keep

@Keep
sealed class QueryResult {
    object Loading : QueryResult()
    object Empty : QueryResult()
    data class Data<T : BaseModel>(val list: List<T>) : QueryResult()
    data class Error(val e: Exception) : QueryResult()
}