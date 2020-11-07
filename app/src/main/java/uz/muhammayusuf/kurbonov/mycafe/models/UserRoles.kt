package uz.muhammayusuf.kurbonov.mycafe.models

import androidx.annotation.Keep

@Keep
class UserRoles {
    companion object {
        const val ADMINISTRATOR = "admin"
        const val MANAGER = "manager"
        const val OFFICIANT = "officiant"
        const val GUEST = "guest"
    }
}