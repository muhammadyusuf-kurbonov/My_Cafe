package uz.muhammayusuf.kurbonov.mycafe.viewmodels

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.muhammayusuf.kurbonov.mycafe.base.viewmodels.repository.Repository
import uz.muhammayusuf.kurbonov.mycafe.models.OrderModel
import uz.muhammayusuf.kurbonov.mycafe.models.QueryResult
import kotlin.random.Random

class FakeRepository : Repository {

    private lateinit var mutableList: ArrayList<OrderModel>
    private var state = 0

    override fun init() {
        mutableList = ArrayList()
        state = 1
    }

    override suspend fun addValue(order: OrderModel) {
        checkState()
        mutableList.add(order)
    }

    private fun checkState() {
        if (state <= 0) throw IllegalAccessError()
    }

    override fun listenData(): Flow<QueryResult> = flow {
        checkState()
        var isActive = true
        while (isActive) {
            emit(QueryResult.Loading)
            val seconds = Random.nextInt(0, 7)
            delay(1000L * seconds)
            when {
                seconds * 2 % 3 == 0 -> emit(QueryResult.Data(mutableList))
                seconds * 2 % 3 == 1 -> emit(QueryResult.Empty)
                seconds * 2 % 3 == 2 -> {
                    isActive = false
                }
            }
        }
    }


    override fun onClear() {
        checkState()
        mutableList.clear()
        state = -1
    }

    override suspend fun delete(order: OrderModel) {
        checkState()
        mutableList.remove(order)
    }

    override suspend fun query(
        timeOut: Long,
        query: (CollectionReference) -> Query
    ): List<OrderModel>? {
        TODO("Not yet implemented")
    }

}