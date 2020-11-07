package uz.muhammayusuf.kurbonov.mycafe.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import uz.muhammayusuf.kurbonov.mycafe.BuildConfig
import uz.muhammayusuf.kurbonov.mycafe.R
import uz.muhammayusuf.kurbonov.mycafe.base.activity.BaseActivity
import uz.muhammayusuf.kurbonov.mycafe.databinding.ActivityMainBinding
import uz.muhammayusuf.kurbonov.mycafe.databinding.CompletedOrderItemBinding
import uz.muhammayusuf.kurbonov.mycafe.models.OrderModel
import uz.muhammayusuf.kurbonov.mycafe.models.UserRoles
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.Repositories
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.chefMode.ChefModeViewModel
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.main.MainViewModel
import kotlin.coroutines.suspendCoroutine

class MainActivity : BaseActivity(), ChefModeViewModel.ChefView {

    private val model by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        model.initNavController(
            (supportFragmentManager
                .findFragmentById(R.id.fragment) as NavHostFragment).navController
        )

        model.initComponents(this)

    }

    private var isExitConfirmed = false

    override fun onBackPressed() {

        lifecycleScope.launch {
            if (Repositories.UserDataRepository.getCurrentUser().role == UserRoles.ADMINISTRATOR
                && findNavController(R.id.fragment).currentDestination?.id != R.id.mainFragment
            ) {
                model.navigateUp()
                isExitConfirmed = false
            } else {

                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.exit_confirm),
                    Toast.LENGTH_SHORT
                ).show()


                if (isExitConfirmed) {
                    finish()
                }

                isExitConfirmed = true

                lifecycleScope.launch {
                    delay(1500)
                    isExitConfirmed = false
                }

            }
        }

    }

    override suspend fun showCompletedDialog(list: List<OrderModel>) = suspendCoroutine<Unit> {
        var item: CompletedOrderItemBinding
        val layout = LinearLayout(this)
        layout.orientation = VERTICAL
        val inflater = LayoutInflater.from(this)

        list.forEach {
            item = CompletedOrderItemBinding.inflate(inflater, layout, false)
            item.tvOrderItemName.text =
                getString(R.string.order_view_template, it.item, it.count.toString())
            layout.addView(item.root, ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Success")
            .setView(layout)
            .setNeutralButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
        lifecycleScope.launch(Dispatchers.Main) {
            dialog.show()
        }
    }

    override fun getScope(): CoroutineScope = lifecycleScope

}