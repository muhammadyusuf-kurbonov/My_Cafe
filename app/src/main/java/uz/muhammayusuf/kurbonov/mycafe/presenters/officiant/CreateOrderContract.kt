package uz.muhammayusuf.kurbonov.mycafe.presenters.officiant

import uz.muhammayusuf.kurbonov.mycafe.BasePresenter

interface CreateOrderView {
    fun showOrderList(list: Map<String, Int>)
    fun getTableNumber(): Int

    suspend fun showWarningDialog(): Boolean
    suspend fun showAmendDialog(): Boolean

    fun showOfflineWarning()
    fun hideOfflineWarning()
}

interface CreateOrderPresenter : BasePresenter<CreateOrderView> {

    fun addToOrderList(order: String)
    fun removeFromOrderList(order: String)
    fun clearOrderList()

    suspend fun submitOrderList()
    suspend fun checkInternet(): Boolean
}