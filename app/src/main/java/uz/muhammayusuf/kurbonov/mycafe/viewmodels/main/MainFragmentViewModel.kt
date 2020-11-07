package uz.muhammayusuf.kurbonov.mycafe.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import uz.muhammayusuf.kurbonov.mycafe.LoadingProcessEvents
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.Repositories

class MainFragmentViewModel : ViewModel() {

    private val mutableLiveData = MutableLiveData<LoadingProcessEvents>()
    val verificationProcessEvents: LiveData<LoadingProcessEvents> = mutableLiveData

    fun verifyUser() {
        viewModelScope.launch {
            mutableLiveData.postValue(LoadingProcessEvents.Loading)
            FirebaseAuth.getInstance().currentUser?.let {
                val user =
                    Repositories.UserDataRepository.getCurrentUser()
                mutableLiveData.postValue(LoadingProcessEvents.Data(user.role))
                timber.log.Timber.d("This user is $user")
            }
        }
    }
}