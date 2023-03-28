package com.eryuksa.catchthelines.ui.detail

import androidx.core.view.children
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

@BindingAdapter("checkedIndex")
fun ChipGroup.checkChip(checkedIndex: Int) {
    children.forEachIndexed { index, view ->
        if (index == checkedIndex && view is Chip && !view.isChecked) {
            view.isChecked = true
        }
    }
}

@InverseBindingAdapter(attribute = "checkedIndex", event = "checkedIndexAttrChanged")
fun ChipGroup.getCheckedIndex(): Int =
    children.indexOfFirst { it.id == checkedChipId }

@BindingAdapter("checkedIndexAttrChanged")
fun ChipGroup.setOnCheckedIndexChangedListener(inverseBindingListener: InverseBindingListener) {
    setOnCheckedStateChangeListener { _, _ ->
        inverseBindingListener.onChange()
    }
}

@BindingAdapter("currentAudioIndex")
fun PlayerControlView.setCurrentItemIndex(currentAudioIndex: Int) {
    player?.let { audioPlayer ->
        if (audioPlayer.currentMediaItemIndex != currentAudioIndex) {
            audioPlayer.pause()
            audioPlayer.seekTo(currentAudioIndex, 0)
        }
    }
}
