package uz.muhammayusuf.kurbonov.mycafe.models

import androidx.annotation.Keep

@Keep
data class NotificationMessage(
    var table: String = "",
    var author: String = ""
) : BaseModel()