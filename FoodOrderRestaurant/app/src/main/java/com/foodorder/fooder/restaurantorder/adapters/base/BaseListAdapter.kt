package com.foodorder.fooder.restaurantorder.adapters.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foodorder.fooder.restaurantorder.adapters.base.BaseListAdapter.BaseViewHolder

abstract class BaseListAdapter<T>(
    private val onClick: ClickListener? = null, diffCallback: DiffUtil
    .ItemCallback<T>
) :
    ListAdapter<T, BaseViewHolder<T>>(diffCallback) {

    /**
     * Set BR.variable ID for data binding.
     */
    abstract fun brVariableId(): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, viewType, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bind(getItem(position), brVariableId())
        holder.itemView.setOnClickListener {
            onClick?.onItemClick(position, getItem(position))
        }
    }

    class BaseViewHolder<T>(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: T, brVariableId: Int) {
            binding.setVariable(brVariableId, item)
            binding.executePendingBindings()
        }
    }

    interface ClickListener {
        fun <T> onItemClick(position: Int, item: T)
    }

}


