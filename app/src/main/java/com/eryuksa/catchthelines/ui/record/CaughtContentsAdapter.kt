package com.eryuksa.catchthelines.ui.record

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eryuksa.catchthelines.data.dto.Content
import com.eryuksa.catchthelines.databinding.ItemCaughtContentBinding
import com.eryuksa.catchthelines.ui.common.preloadImage

class CaughtContentsAdapter(private val onClick: (content: Content, sharedElements: Pair<ImageView, String>) -> Unit) :
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
        val futurePosition = (position + 5).coerceAtMost(contents.lastIndex)
        Glide.get(holder.itemView.context).preloadImage(contents[futurePosition].posterUrl)
    }

    override fun getItemCount(): Int = contents.size

    inner class ViewHolder(private val binding: ItemCaughtContentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val content = contents[layoutPosition]
                onClick(content, Pair(binding.ivPoster, binding.ivPoster.transitionName))
            }
        }

        fun bind(content: Content) {
            binding.content = content
            binding.executePendingBindings()
        }
    }
}
