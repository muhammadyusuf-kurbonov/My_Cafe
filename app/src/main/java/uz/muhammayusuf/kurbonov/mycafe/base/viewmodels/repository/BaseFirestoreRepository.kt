package uz.muhammayusuf.kurbonov.mycafe.base.viewmodels.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import uz.muhammayusuf.kurbonov.mycafe.models.OrderModel
import uz.muhammayusuf.kurbonov.mycafe.models.QueryResult
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.getData

abstract class BaseFirestoreRepository : Repository {


    abstract val collectionName: String


    private var canceler: ListenerRegistration? = null
    protected lateinit var dataStore: CollectionReference

    override fun init() {
        dataStore = FirebaseFirestore.getInstance().collection(collectionName)
    }

    override suspend fun addValue(order: OrderModel) {
        val destDocument = dataStore.document()
        order.id = destDocument.id
        destDocument.set(order)
    }

    @ExperimentalCoroutinesApi
    override fun listenData(): Flow<QueryResult> = callbackFlow {
        canceler = dataStore.orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                try {
                    if (error != null) {
                        offer(QueryResult.Error(error))
                    } else {
                        if (value != null) {
                            val list = value.toObjects(OrderModel::class.java)
                            if (list.isNotEmpty()) {
                                offer(QueryResult.Data(list))
                            } else {
                                offer(QueryResult.Empty)
                            }
                        } else
                            throw IllegalStateException("What happens???? $value is null and $error is null???")
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }

        awaitClose {
            Timber.d("Closing")
            onClear()
        }
    }

    override fun onClear() {
        canceler?.remove()
        canceler = null
    }

    override suspend fun delete(order: OrderModel) {
        dataStore.document(order.id).delete()
            .addOnFailureListener {
                Timber.d("delete: Deleted with fail ${it.message}")
                it.printStackTrace()
            }
    }

    override suspend fun query(
        timeOut: Long,
        query: (CollectionReference) -> Query
    ): List<OrderModel>? =
        withTimeout(timeOut) {
            Timber.d("Started at ${System.currentTimeMillis()}")

            withContext(Dispatchers.Default) {
                Timber.d("Launched on thread ${Thread.currentThread().name}")
                query(dataStore).getData()
            }
        }

}