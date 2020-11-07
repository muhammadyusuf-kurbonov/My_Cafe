package uz.muhammayusuf.kurbonov.mycafe.presenters.user_managment

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.Repositories

class UserManagementPresenterImpl : UserManagementPresenter {

    private lateinit var mView: UserManagementView
    private var isInitialized = false
    private val job = Job()
    private val mScope = CoroutineScope(job)

    override fun attachView(view: UserManagementView) {
        mView = view
        isInitialized = true
    }

    override fun destroy() {
        isInitialized = false
        job.cancel()
    }

    @ExperimentalCoroutinesApi
    override fun startListening() {
        mScope.launch {
            Repositories.UserDataRepository.listenData().collect {
                withContext(Dispatchers.Main) {
                    mView.submitList(it)
                }
            }
        }
    }

    override fun updateRole(uid: String, role: String) {
        mScope.launch {
            Timber.d("Updating ...")
            val user = Repositories.UserDataRepository.getUser(uid)
                ?: throw IllegalArgumentException("No such user with $uid")
            Timber.d(" UID = $uid, role = $role")

            Repositories.UserDataRepository.updateUser(
                user.also {
                    it.role = role
                }
            )
            Timber.d("new role is ${Repositories.UserDataRepository.getUser(uid)?.role}, must be $role")
        }
    }


}