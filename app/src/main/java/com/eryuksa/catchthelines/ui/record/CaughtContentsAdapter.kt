package com.eryuksa.catchthelines.ui.record

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eryuksa.catchthelines.data.dto.Content
import com.eryuksa.catchthelines.databinding.ItemCaughtContentBinding

class CaughtContentsAdapter(private val onClick: (content: Content) -> Unit) :
    RecyclerView.Adapter<CaughtContentsAdapter.ViewHolder>() {

    var contents: List<Content> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCaughtContentBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(contents[position])
    }

    override fun getItemCount(): Int = contents.size

    inner class ViewHolder(private val binding: ItemCaughtContentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onClick(contents[layoutPosition])
            }
            binding.ivPoster.clipToOutline = true
        }

        fun bind(content: Content) {
            binding.content = content
            binding.executePendingBindings()
        }
    }
}
