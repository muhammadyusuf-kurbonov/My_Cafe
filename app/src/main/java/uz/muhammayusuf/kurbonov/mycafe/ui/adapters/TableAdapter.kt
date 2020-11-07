package uz.muhammayusuf.kurbonov.mycafe.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import uz.muhammayusuf.kurbonov.mycafe.BaseAdapter
import uz.muhammayusuf.kurbonov.mycafe.R
import uz.muhammayusuf.kurbonov.mycafe.databinding.TableItemBinding

class TableAdapter : BaseAdapter<Int, TableAdapter.TableViewHolder>(TableDiffCallback()) {

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.layoutManager = FlexboxLayoutManager(recyclerView.context)
    }

    class TableDiffCallback : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean = oldItem == newItem

    }

    class TableViewHolder(val binding: TableItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun OnBindingViewHolder(holder: TableViewHolder, item: Int) {
        holder.binding.root.text =
            holder.itemView.context.getString(R.string.table_caption, item.toString())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder =
        TableViewHolder(
            TableItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
}