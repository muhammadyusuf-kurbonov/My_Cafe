package uz.muhammayusuf.kurbonov.mycafe.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.muhammayusuf.kurbonov.mycafe.databinding.MealItemBinding

class MealsAdapter : ListAdapter<String, MealsAdapter.MealsViewHolder>(MealDiffCallback()) {


    class MealDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem.length == newItem.length

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }

    class MealsViewHolder(val binding: MealItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealsViewHolder =
        MealsViewHolder(
            MealItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: MealsViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.tvItemName.text = item
        onClickListener?.let {
            holder.binding.root.setOnClickListener {
                onClickListener?.invoke(item)
            }
        }
    }

    var onClickListener: ((String) -> Unit)? = null

}