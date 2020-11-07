@file:Suppress("unused")

package uz.muhammayusuf.kurbonov.mycafe

import androidx.annotation.NonNull
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.InetAddress
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


suspend fun checkInternetConnection() =
    withContext(Dispatchers.IO) {
        suspendCoroutine<Boolean> {
            try {
                @Suppress("BlockingMethodInNonBlockingContext")
                val ipAddress = InetAddress.getByName("google.com")
                //You can replace it with your name
                it.resume(!ipAddress.equals(""))
            } catch (e: Exception) {
                it.resume(false)
                Timber.e(e)
            }
        }
    }

sealed class LoadingProcessEvents {
    object Loading : LoadingProcessEvents()
    object Empty : LoadingProcessEvents()
    class Data<T>(val data: T) : LoadingProcessEvents()
    class Error(val error: Exception) : LoadingProcessEvents()
}

interface BasePresenter<V> {
    fun destroy()
}

abstract class BasePresenterImpl<V>(@NonNull protected var mView: V) : BasePresenter<V> {
    private var isActive = false

    init {
        isActive = true
    }


    override fun destroy() {
        isActive = false
    }

    fun requireActive() {
        if (!isActive)
            throw IllegalStateException("This presenter is destroyed")
    }
}

abstract class BaseAdapter<T : Any, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, VH>(diffCallback) {

    var onClickListener: ((T) -> Unit)? = null
    var onLongClickListener: ((T) -> Unit)? = null

    abstract fun OnBindingViewHolder(holder: VH, item: T)

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        onClickListener?.let { function ->
            holder.itemView.setOnClickListener {
                function(item)
            }
        }

        onLongClickListener?.let { function ->
            holder.itemView.setOnLongClickListener {
                function(item)
                return@setOnLongClickListener true
            }
        }

        OnBindingViewHolder(holder, item)
    }
}