package uz.muhammayusuf.kurbonov.mycafe.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import timber.log.Timber
import uz.muhammayusuf.kurbonov.mycafe.R
import uz.muhammayusuf.kurbonov.mycafe.models.QueryResult
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.chefMode.ChefModeViewModel
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.main.MainViewModel

class EmptyFragment : Fragment(R.layout.fragment_empty) {
    private val model by viewModels<ChefModeViewModel>()
    private val parentViewModel by activityViewModels<MainViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.managerData.observe(viewLifecycleOwner) {
            Timber.d("recieved $it")
            when (it) {
                is QueryResult.Data<*> -> parentViewModel.startChefMode()
                is QueryResult.Error -> parentViewModel.gotoError()
                else -> Timber.d("Empty fragment")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        model.startListening()
    }

}