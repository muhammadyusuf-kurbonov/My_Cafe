package uz.muhammayusuf.kurbonov.mycafe.viewmodels.main

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import timber.log.Timber
import uz.muhammayusuf.kurbonov.mycafe.R
import uz.muhammayusuf.kurbonov.mycafe.checkInternetConnection
import uz.muhammayusuf.kurbonov.mycafe.ui.fragments.FullscreenFragmentDirections
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.Repositories
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.getSignInIntent
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.registerSignIn
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.subscribeToTopics

class MainViewModel : ViewModel() {

    private lateinit var navController: NavController

    fun initComponents(context: AppCompatActivity) {

        Repositories.LocalSettingsStorage.init(context) { pref, key ->
            if (key == "uid") {
                subscribeToTopics(pref.getString(key, "default") ?: "default")
            }
        }
        subscribeToTopics(Repositories.LocalSettingsStorage.getPref("uid", "default") ?: "default")

        viewModelScope.launch {
            if (Repositories.SharedSettings.getPref("table_count").isNullOrBlank()) {
                Repositories.SharedSettings.setPref("table_count", "15")
            }

        }
        // Initializing FirebaseMessagingService
        Timber.d("This is user ${FirebaseAuth.getInstance().currentUser?.displayName}")

        if (FirebaseAuth.getInstance().currentUser == null) {
            context.registerSignIn().launch(getSignInIntent())
        }
    }

    fun initNavController(navController: NavController) {
        this.navController = navController
    }

    fun showMainFragment() {
        if (navController.currentDestination?.id == R.id.fullscreenFragment)
            navController.navigate(FullscreenFragmentDirections.actionFullscreenFragmentToMainFragment())
    }

    fun startChefMode() {
        if (navController.currentDestination?.id in arrayListOf(
                R.id.mainFragment,
                R.id.emptyFragment
            )
        )
            navController.navigate(R.id.chefFragment)
    }


    fun startOfficiantMode() {
        if (navController.currentDestination?.id == R.id.mainFragment)
            navController.navigate(R.id.createOrderFragment2)
    }


    fun startUserManagementMode() {
        if (navController.currentDestination?.id == R.id.mainFragment)
            navController.navigate(R.id.userManagementFragment)
    }

    fun navigateUp() {
        if (navController.currentDestination?.id != R.id.mainFragment)
            navController.navigateUp()
    }


    fun gotoError() {
        navController.navigate(R.id.errorFragment, null, navOptions {
            popUpTo(R.id.mainFragment) {
                inclusive = true
            }
        })
    }

    fun gotoEmpty() {
        navController.navigate(R.id.emptyFragment, null, navOptions {
            popUpTo(R.id.mainFragment) {
                inclusive = true
            }
        })
    }


    fun validateInternetConnection() {
        viewModelScope.launch {
            if (!checkInternetConnection()) {
                navController.navigate(FullscreenFragmentDirections.actionFullscreenFragmentToNoConnectionFragment())
            }
        }
    }

}