package uz.muhammayusuf.kurbonov.mycafe.presenters.officiant

import com.google.firebase.auth.FirebaseAuth
import uz.muhammayusuf.kurbonov.mycafe.BasePresenterImpl
import uz.muhammayusuf.kurbonov.mycafe.checkInternetConnection
import uz.muhammayusuf.kurbonov.mycafe.models.OrderModel
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.Repositories
import java.util.*

class CreateOrderPresenterImpl(mView: CreateOrderView) : BasePresenterImpl<CreateOrderView>(mView),
    CreateOrderPresenter {

    private val currentOrderList = mutableMapOf<String, Int>()

    override fun addToOrderList(order: String) {
        if (currentOrderList.containsKey(order))
            currentOrderList[order] = currentOrderList[order]!! + 1
        else
            currentOrderList[order] = 1

        mView.showOrderList(currentOrderList)
    }

    override fun removeFromOrderList(order: String) {
        currentOrderList[order] = currentOrderList[order]!! - 1
        if (currentOrderList[order] == 0) {
            currentOrderList.remove(order)
        }
        mView.showOrderList(currentOrderList)
    }

    override fun clearOrderList() {
        currentOrderList.clear()
        mView.showOrderList(currentOrderList)
    }

    override suspend fun submitOrderList() {
        if (!checkInternet()) {
            if (!mView.showWarningDialog()) {
                return
            }
        }

        val repo = Repositories.OrderedFirestoreDataRepository.apply {
            init()
        }

        var groupId = UUID.randomUUID().toString()

        val query = repo.query {
            it.whereEqualTo("table", mView.getTableNumber())
        }
        if (query?.isNotEmpty() == true) {
            if (mView.showAmendDialog()) {
                groupId = query[0].groupId
            }
        }

        for ((order, count) in currentOrderList) {
            repo.addValue(
                OrderModel(
                    item = order,
                    count = count,
                    groupId = groupId,
                    table = mView.getTableNumber(),
                    author = FirebaseAuth.getInstance().currentUser!!.uid
                )
            )
        }

        clearOrderList()
    }

    override suspend fun checkInternet(): Boolean {
        val connected = checkInternetConnection()

        if (connected)
            mView.hideOfflineWarning()
        else
            mView.showOfflineWarning()


        return connected
    }


}