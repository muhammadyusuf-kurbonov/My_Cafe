package uz.muhammayusuf.kurbonov.mycafe.base.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.autofill.AutofillManager
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.messaging.FirebaseMessaging
import uz.muhammayusuf.kurbonov.mycafe.R
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.Repositories
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.getSignInIntent
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.registerSignIn

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var signInActivity: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signInActivity = registerSignIn()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.main_logout_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnLogout -> {
                AuthUI.getInstance().signOut(this).addOnCompleteListener {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        val autofillManager = getSystemService(AutofillManager::class.java)
                        autofillManager.commit()
                    }
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(
                        Repositories.LocalSettingsStorage.getPref("uid") ?: "default"
                    )
                    signInActivity.launch(getSignInIntent())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}