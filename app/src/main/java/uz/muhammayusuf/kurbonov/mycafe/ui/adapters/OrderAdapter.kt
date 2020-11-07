package uz.muhammayusuf.kurbonov.mycafe.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.muhammayusuf.kurbonov.mycafe.R
import uz.muhammayusuf.kurbonov.mycafe.databinding.OrderItemBinding
import uz.muhammayusuf.kurbonov.mycafe.models.OrderModel

class OrderAdapter : ListAdapter<OrderModel, OrderAdapter.OrderViewHolder>(OrderDiffResolver()) {

    class OrderViewHolder(val binding: OrderItemBinding) : RecyclerView.ViewHolder(binding.root)

    class OrderDiffResolver : DiffUtil.ItemCallback<OrderModel>() {
        override fun areItemsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean =
            oldItem == newItem


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder =
        OrderViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        with(holder.binding) {
            tvItemName.text = root.context.getString(
                R.string.order_view_template,
                order.item,
                order.count.toString()
            )

            if (onClickListener != null) {
                btnCompleted.setOnClickListener {
                    onClickListener?.invoke(order)
                }
            }
        }
    }

    var onClickListener: ((order: OrderModel) -> Unit)? = null
}