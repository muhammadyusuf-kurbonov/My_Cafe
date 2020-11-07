package uz.muhammayusuf.kurbonov.mycafe.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.chip.Chip
import kotlinx.coroutines.*
import uz.muhammayusuf.kurbonov.mycafe.R
import uz.muhammayusuf.kurbonov.mycafe.databinding.FragmentCreateOrderBinding
import uz.muhammayusuf.kurbonov.mycafe.presenters.officiant.CreateOrderPresenter
import uz.muhammayusuf.kurbonov.mycafe.presenters.officiant.CreateOrderPresenterImpl
import uz.muhammayusuf.kurbonov.mycafe.presenters.officiant.CreateOrderView
import uz.muhammayusuf.kurbonov.mycafe.ui.adapters.MealsAdapter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CreateOrderFragment : Fragment(R.layout.fragment_create_order), CreateOrderView {
    private lateinit var binding: FragmentCreateOrderBinding
    private val presenter: CreateOrderPresenter = CreateOrderPresenterImpl(this)
    private var tableNumber = -1
        set(value) {
            field = value
            if (value > 0) {
                binding.tvTable.text = getString(R.string.table_caption, value.toString())
            }
        }

    private val meals = listOf(
        "Palow",
        "Kebab",
        "Pizza",
        "Hot-dog",
        "Cheeseburger",
        "Shaurma",
        "Lavash",
        "Hamburger",
        "Potato fry",
        "Pepsi",
        "Fanta",
        "Mineral wasser"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateOrderBinding.bind(view)

        lifecycleScope.launchWhenResumed {
            while (isActive) {
                delay(1500)
                presenter.checkInternet()
            }
        }

        with(binding) {
            showOrderList(emptyMap())
            val adapter = MealsAdapter()
            adapter.onClickListener = {
                presenter.addToOrderList(it)
            }

            btnSubmit.setOnClickListener {
                lifecycleScope.launch {
                    if (tableNumber > 0) {
                        presenter.submitOrderList()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.order_added_caption),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.select_table_caption),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            tvTable.text = getString(R.string.select_table_caption)
            tvTable.setOnClickListener {
                val selectFragment = SelectTableFragment {}
                selectFragment.callBack = {
                    tableNumber = it
                    selectFragment.dismiss()
                }
                selectFragment.show(childFragmentManager, "table_selector")
            }

            adapter.submitList(meals)
            mainList.adapter = adapter
            val lm = FlexboxLayoutManager(requireActivity())
            mainList.layoutManager = lm
        }

    }

    private val onChipClickListener: (String) -> Unit = {
        presenter.removeFromOrderList(it)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
    }

    override fun showOrderList(list: Map<String, Int>) {
        with(binding) {
            selectedItemsContainer.removeAllViews()
            var chip: Chip?


            if (list.isEmpty()) {
                LayoutInflater.from(requireActivity())
                    .inflate(R.layout.selected_empty_view, selectedItemsContainer, true)
                return
            }

            list.forEach {
                val name = it.key
                val count = it.value
                chip = Chip(requireActivity())
                chip?.text = getString(R.string.chip_text_template, name, count.toString())
                chip?.setOnClickListener {
                    onChipClickListener(name)
                }
                selectedItemsContainer.addView(chip)
            }
        }
    }

    override fun getTableNumber(): Int = tableNumber

    override suspend fun showWarningDialog(): Boolean = suspendCoroutine {
        AlertDialog.Builder(requireContext())
            .setTitle("Warning")
            .setView(R.layout.dialog_warning)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                it.resume(true)
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                it.resume(false)
                dialog.dismiss()
            }
            .show()
    }

    override suspend fun showAmendDialog(): Boolean = suspendCoroutine {
        AlertDialog.Builder(requireContext())
            .setTitle("Information")
            .setMessage(getString(R.string.amend_message))
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                it.resume(true)
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                it.resume(false)
                dialog.dismiss()
            }
            .show()
    }

    override fun showOfflineWarning() {
        binding.tvWarning.visibility = View.VISIBLE
        val translationYBy = binding.tvWarning.animate().translationY(0f)
        translationYBy.duration = 100
        translationYBy.start()
    }

    override fun hideOfflineWarning() {
        val translationYBy = binding.tvWarning.animate().translationY(-50f)
        translationYBy.duration = 100
        translationYBy.start()
        binding.tvWarning.visibility = View.GONE
    }
}