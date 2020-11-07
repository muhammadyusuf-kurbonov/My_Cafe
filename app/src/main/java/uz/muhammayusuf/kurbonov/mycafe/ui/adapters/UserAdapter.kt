package uz.muhammayusuf.kurbonov.mycafe.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import uz.muhammayusuf.kurbonov.mycafe.databinding.UserItemBinding
import uz.muhammayusuf.kurbonov.mycafe.models.UserModel
import java.text.SimpleDateFormat
import java.util.*

class UserAdapter(var onClickListener: ((userModel: UserModel) -> Unit)? = null) :
    ListAdapter<UserModel, UserAdapter.UserViewHolder>(UserDiffResolver()) {

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context, VERTICAL, false)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerView.removeItemDecorationAt(0)
        recyclerView.layoutManager = null
    }

    class UserViewHolder(val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root)

    class UserDiffResolver : DiffUtil.ItemCallback<UserModel>() {
        override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean =
            oldItem.userId == newItem.userId

        override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(
            UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        val user = getItem(position)
        with(holder.binding) {
            tvName.text = user.name
            tvEmail.text = user.email
            tvLastActivity.text =
                SimpleDateFormat("dd MMM yyyy  HH:mm", Locale.getDefault()).format(
                    Date(user.lastSignIn)
                )

            onClickListener?.let {
                root.setOnClickListener {
                    it(user)
                }
            }
        }
    }

}