package com.eryuksa.catchthelines.ui.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.eryuksa.catchline_android.databinding.ItemPosterBinding
import com.eryuksa.catchthelines.data.dto.GameItem
import jp.wasabeef.glide.transformations.BlurTransformation

class PosterViewPagerAdapter :
    ListAdapter<GameItem, PosterViewPagerAdapter.PosterViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosterViewHolder {
        val binding = ItemPosterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.ivPoster.clipToOutline = true
        return PosterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PosterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PosterViewHolder(private val binding: ItemPosterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(gameItem: GameItem) {
            Glide.with(itemView.context)
                .load(gameItem.posterUrl)
                .apply(
                    RequestOptions.bitmapTransform(
                        BlurTransformation(25, gameItem.blurDegree)
                    )
                )
                .into(binding.ivPoster)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<GameItem>() {
            override fun areContentsTheSame(oldItem: GameItem, newItem: GameItem) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: GameItem, newItem: GameItem) =
                oldItem.id == newItem.id
        }
    }
}
