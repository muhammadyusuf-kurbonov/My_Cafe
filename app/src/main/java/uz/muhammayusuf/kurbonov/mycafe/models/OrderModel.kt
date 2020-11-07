package uz.muhammayusuf.kurbonov.mycafe.models

import androidx.annotation.Keep

@Keep
data class OrderModel(
    var item: String = "",
    var count: Int = 0,
    var table: Int = 0,
    var groupId: String = "",
    var author: String = "Androider",
    var time: Long = System.currentTimeMillis()
) : BaseModel()