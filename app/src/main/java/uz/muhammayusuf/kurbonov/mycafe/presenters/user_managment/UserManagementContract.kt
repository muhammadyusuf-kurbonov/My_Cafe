package uz.muhammayusuf.kurbonov.mycafe.presenters.user_managment

import uz.muhammayusuf.kurbonov.mycafe.models.UserModel

interface UserManagementView {
    fun submitList(list: List<UserModel>)
}

interface UserManagementPresenter {
    fun attachView(view: UserManagementView)
    fun destroy()

    fun startListening()
    fun updateRole(uid: String, role: String)
}