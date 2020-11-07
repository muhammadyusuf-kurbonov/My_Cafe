package uz.muhammayusuf.kurbonov.mycafe.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import uz.muhammayusuf.kurbonov.mycafe.R
import uz.muhammayusuf.kurbonov.mycafe.databinding.FragmentUserManagementBinding
import uz.muhammayusuf.kurbonov.mycafe.models.UserModel
import uz.muhammayusuf.kurbonov.mycafe.presenters.user_managment.UserManagementPresenter
import uz.muhammayusuf.kurbonov.mycafe.presenters.user_managment.UserManagementPresenterImpl
import uz.muhammayusuf.kurbonov.mycafe.presenters.user_managment.UserManagementView
import uz.muhammayusuf.kurbonov.mycafe.ui.adapters.UserAdapter

class UserManagementFragment : Fragment(R.layout.fragment_user_management), UserManagementView {
    private lateinit var userAdapter: UserAdapter
    private lateinit var presenter: UserManagementPresenter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title =
            getString(R.string.app_name) + " - " + getString(R.string.user_mgmt_caption)
        val binding = FragmentUserManagementBinding.bind(view)

        userAdapter = UserAdapter {
            UserRoleFragment(it.userId) { uid, role ->
                presenter.updateRole(uid, role)
            }.show(parentFragmentManager, "dialog")

        }
        presenter = UserManagementPresenterImpl()
        binding.mainUserList.apply {
            adapter = userAdapter
        }

        presenter.attachView(this)
        presenter.startListening()
    }

    override fun onDetach() {
        super.onDetach()
        presenter.destroy()
    }

    override fun submitList(list: List<UserModel>) {
        userAdapter.submitList(list)
    }


}