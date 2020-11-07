package uz.muhammayusuf.kurbonov.mycafe.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import uz.muhammayusuf.kurbonov.mycafe.R
import uz.muhammayusuf.kurbonov.mycafe.checkInternetConnection
import uz.muhammayusuf.kurbonov.mycafe.databinding.FragmentNoInternetBinding

class NoConnectionFragment :
    Fragment(R.layout.fragment_no_internet) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentNoInternetBinding.bind(view)

        binding.btnRetry.setOnClickListener {
            lifecycleScope.launch {
                if (checkInternetConnection()) {
                    findNavController().navigateUp()
                }
            }
        }
    }
}