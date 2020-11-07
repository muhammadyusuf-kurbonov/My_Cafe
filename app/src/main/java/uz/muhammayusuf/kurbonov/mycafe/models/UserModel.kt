package uz.muhammayusuf.kurbonov.mycafe.models

import androidx.annotation.Keep

@Keep
data class UserModel(
    var userId: String = "",
    var name: String = "",
    var email: String = "",
    var registerDate: Long = System.currentTimeMillis(),
    var lastSignIn: Long = System.currentTimeMillis(),
    var role: String = ""
) : BaseModel()

