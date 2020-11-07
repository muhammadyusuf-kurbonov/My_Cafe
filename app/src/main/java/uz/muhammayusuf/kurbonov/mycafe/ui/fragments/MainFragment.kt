package uz.muhammayusuf.kurbonov.mycafe.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import uz.muhammayusuf.kurbonov.mycafe.LoadingProcessEvents
import uz.muhammayusuf.kurbonov.mycafe.R
import uz.muhammayusuf.kurbonov.mycafe.databinding.FragmentMainBinding
import uz.muhammayusuf.kurbonov.mycafe.models.UserRoles
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.main.MainFragmentViewModel
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.main.MainViewModel

class MainFragment : Fragment() {

    private val model by viewModels<MainFragmentViewModel>()
    private val parentModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    private lateinit var binding: FragmentMainBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMainBinding.bind(view)

        binding.btnManager.setOnClickListener {
            parentModel.startChefMode()
        }

        binding.btnOfficiant.setOnClickListener {
            parentModel.startOfficiantMode()
        }

        model.verificationProcessEvents.observe(viewLifecycleOwner) {
            with(binding) {
                when (it) {
                    is LoadingProcessEvents.Loading -> {
                        tvWelcome.setText(R.string.verifiy_process_placeholder)
                        btnManager.isEnabled = false
                        btnOfficiant.isEnabled = false
                        btnUserManagment.isEnabled = false
                    }
                    is LoadingProcessEvents.Data<*> -> {
                        when (it.data as String) {
                            UserRoles.ADMINISTRATOR -> {
                                tvWelcome.text = getString(
                                    R.string.welcome_caption, "" +
                                            "${FirebaseAuth.getInstance().currentUser?.displayName} (${it.data})" +
                                            ""
                                )
                                flAdminPanel.visibility = VISIBLE
                                btnUserManagment.setOnClickListener {
                                    parentModel.startUserManagementMode()
                                }
                                btnUserManagment.isEnabled = true
                                btnOfficiant.isEnabled = true
                                btnManager.isEnabled = true

                                flAdminPanel.visibility = VISIBLE
                                btnManager.visibility = VISIBLE
                                btnOfficiant.visibility = VISIBLE
                            }
                            UserRoles.GUEST -> {
                                tvWelcome.text = getString(
                                    R.string.welcome_caption, "" +
                                            "${FirebaseAuth.getInstance().currentUser?.displayName} (${it.data})" +
                                            ""
                                )
                                flAdminPanel.visibility = GONE
                                btnManager.visibility = GONE
                                btnOfficiant.visibility = GONE
                            }
                            UserRoles.MANAGER -> {
                                Toast.makeText(
                                    requireContext(), getString(
                                        R.string.welcome_caption, "" +
                                                "${FirebaseAuth.getInstance().currentUser?.displayName} (${it.data})" +
                                                ""
                                    ), Toast.LENGTH_LONG
                                ).show()
                                parentModel.startChefMode()
                            }
                            UserRoles.OFFICIANT -> {
                                Toast.makeText(
                                    requireContext(), getString(
                                        R.string.welcome_caption, "" +
                                                "${FirebaseAuth.getInstance().currentUser?.displayName} (${it.data})" +
                                                ""
                                    ), Toast.LENGTH_LONG
                                ).show()
                                parentModel.startOfficiantMode()
                            }
                            "none" -> throw IllegalArgumentException("No user?")
                        }
                    }
                    LoadingProcessEvents.Empty -> parentModel.gotoEmpty()
                    is LoadingProcessEvents.Error -> parentModel.gotoError()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.title = getString(R.string.app_name)
        model.verifyUser()
    }
}