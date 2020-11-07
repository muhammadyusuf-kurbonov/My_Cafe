package uz.muhammayusuf.kurbonov.mycafe.base.viewmodels.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import uz.muhammayusuf.kurbonov.mycafe.models.OrderModel
import uz.muhammayusuf.kurbonov.mycafe.models.QueryResult

interface Repository {

    fun init()

    suspend fun addValue(order: OrderModel)

    fun listenData(): Flow<QueryResult>

    fun onClear()

    suspend fun delete(order: OrderModel)

    suspend fun query(
        timeOut: Long = 60000L,
        query: (CollectionReference) -> Query
    ): List<OrderModel>?
}