package uz.muhammayusuf.kurbonov.mycafe.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import timber.log.Timber
import uz.muhammayusuf.kurbonov.mycafe.R
import uz.muhammayusuf.kurbonov.mycafe.databinding.FragmentUserRoleBinding
import uz.muhammayusuf.kurbonov.mycafe.models.UserRoles
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.Repositories

class UserRoleFragment(val uid: String, val callBack: (uid: String, role: String) -> Unit) :
    DialogFragment(R.layout.fragment_user_role) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentUserRoleBinding.bind(view)


        lifecycleScope.launch {
            val user = Repositories.UserDataRepository.getUser(uid)
                ?: throw IllegalArgumentException("Invalid uid. No such user with id $uid")
            with(binding) {
                val radioButton = when (user.role) {
                    UserRoles.ADMINISTRATOR -> rbAdmin
                    UserRoles.GUEST -> rbGuest
                    UserRoles.MANAGER -> rbManager
                    UserRoles.OFFICIANT -> rbOfficiant
                    else -> throw IllegalArgumentException("Which role is it? ${user.role}")
                }

                radioButton.isChecked = true
                tvSubTitle.text = getString(R.string.change_role_caption, user.name)
                btnCancel.setOnClickListener {
                    dismiss()
                }
                btnConfirm.setOnClickListener {
                    val role = when (radioGroup.checkedRadioButtonId) {
                        R.id.rbAdmin -> UserRoles.ADMINISTRATOR
                        R.id.rbGuest -> UserRoles.GUEST
                        R.id.rbManager -> UserRoles.MANAGER
                        R.id.rbOfficiant -> UserRoles.OFFICIANT
                        else -> {
                            Timber.d("Selected is ${radioGroup.checkedRadioButtonId}")
                            throw IllegalStateException("No Selected?")
                        }
                    }

                    lifecycleScope.launch {
                        callBack(uid, role)
                        Timber.d("Updated $user")
                    }
                    dismiss()
                }
            }
        }

    }
}