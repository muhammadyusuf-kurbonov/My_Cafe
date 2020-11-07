package uz.muhammayusuf.kurbonov.mycafe.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import uz.muhammayusuf.kurbonov.mycafe.R
import uz.muhammayusuf.kurbonov.mycafe.databinding.FragmentChefModeBinding
import uz.muhammayusuf.kurbonov.mycafe.models.OrderModel
import uz.muhammayusuf.kurbonov.mycafe.models.QueryResult
import uz.muhammayusuf.kurbonov.mycafe.ui.activities.MainActivity
import uz.muhammayusuf.kurbonov.mycafe.ui.adapters.OrderAdapter
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.chefMode.ChefModeViewModel
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.main.MainViewModel

class ChefFragment : Fragment(R.layout.fragment_chef_mode) {

    private val model by viewModels<ChefModeViewModel>()
    private val parentViewModel by activityViewModels<MainViewModel>()

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title =
            getString(R.string.app_name) + " - " + getString(R.string.chef_mode_caption)
        val binding = FragmentChefModeBinding.bind(view)

        val context = requireContext()

        val orderAdapter = OrderAdapter()
        orderAdapter.onClickListener = {
            model.setCompleted(it, activity as MainActivity)
        }
        binding.mainList.adapter = orderAdapter
        binding.mainList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        model.managerData.observe(viewLifecycleOwner) {
            binding.materialProgressBar.visibility = View.GONE
            when (it) {
                is QueryResult.Loading -> {
                    binding.materialProgressBar.visibility = View.VISIBLE
                }
                is QueryResult.Data<*> -> {
                    if (it.list.isNotEmpty() && it.list[0] is OrderModel)
                        orderAdapter.submitList(it.list.map { model ->
                            model as OrderModel
                        })
                }

                is QueryResult.Empty -> parentViewModel.gotoEmpty()
                is QueryResult.Error -> parentViewModel.gotoError()
            }
        }

        model.startListening()
    }
}