package uz.muhammayusuf.kurbonov.mycafe.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import uz.muhammayusuf.kurbonov.mycafe.R
import uz.muhammayusuf.kurbonov.mycafe.databinding.FragmentSelectTableBinding
import uz.muhammayusuf.kurbonov.mycafe.ui.adapters.TableAdapter
import uz.muhammayusuf.kurbonov.mycafe.viewmodels.Repositories

class SelectTableFragment(var callBack: (Int) -> Unit) :
    DialogFragment(R.layout.fragment_select_table) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSelectTableBinding.bind(view)

        val tableAdapter = TableAdapter()
        tableAdapter.onClickListener = {
            callBack(it)
        }
        binding.tableContainer.adapter = tableAdapter

        lifecycleScope.launch {
            val max = Repositories.SharedSettings.getPref("table_count")?.toInt() ?: 0
            if (max != 0) {
                tableAdapter.submitList((1..max).toList())
            }
        }
    }
}