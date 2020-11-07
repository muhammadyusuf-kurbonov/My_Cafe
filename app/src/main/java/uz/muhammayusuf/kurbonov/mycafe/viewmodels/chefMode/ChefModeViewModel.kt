package uz.muhammayusuf.kurbonov.mycafe.viewmodels.chefMode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.muhammayusuf.kurbonov.mycafe.base.viewmodels.repository.Repository
import uz.muhammayusuf.kurbonov.mycafe.models.OrderModel
import uz.muhammayusuf.kurbonov.mycafe.models.QueryResult
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.Repositories

class ChefModeViewModel : ViewModel() {

    private var isBeingListened = false

    private val internalData = MutableLiveData<QueryResult>()
    val managerData: LiveData<QueryResult>
        get() = internalData
    private val repository: Repository = Repositories.OrderedFirestoreDataRepository

    init {
        repository.init()
    }

    fun startListening() {
        if (!isBeingListened) {
            viewModelScope.launch {
                internalData.postValue(QueryResult.Loading)
                isBeingListened = true
                repository.listenData().collect {
                    internalData.postValue(it)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.onClear()
    }

    fun setCompleted(order: OrderModel, view: ChefView) {
        view.getScope().launch(Dispatchers.IO) {
            // This var contains if all orders of this table are ready
            repository.delete(order)


            val completedFirestoreDataRepository = Repositories.CompletedFirestoreDataRepository
            completedFirestoreDataRepository.init()
            completedFirestoreDataRepository.addValue(order)


            val listOfActiveOrders = repository.query {
                it.whereEqualTo("table", order.table)
            }?.size ?: throw IllegalStateException("Throw!")
            val completedFull = listOfActiveOrders == 0


            if (completedFull) {
                Repositories.NotificationsDataStore.sendNotification(order.author, order.table)
                view.showCompletedDialog(
                    completedFirestoreDataRepository.query {
                        it.whereEqualTo("groupId", order.groupId)
                    } ?: throw IllegalStateException("No completed order???")
                )
            }
        }
    }

    interface ChefView {
        suspend fun showCompletedDialog(list: List<OrderModel>)
        fun getScope(): CoroutineScope
    }
}