package uz.muhammayusuf.kurbonov.mycafe.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import uz.muhammayusuf.kurbonov.mycafe.R
import uz.muhammayusuf.kurbonov.mycafe.databinding.FragmentOfficiantBinding

class OfficiantFragment : Fragment(R.layout.fragment_officiant) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title =
            getString(R.string.app_name) + " - " + getString(R.string.officiant_mode_caption)
        val binding = FragmentOfficiantBinding.bind(view)
        NavigationUI.setupWithNavController(
            binding.bottomAppBar,
            (childFragmentManager.findFragmentById(R.id.navContainer) as NavHostFragment).navController
        )
    }
}