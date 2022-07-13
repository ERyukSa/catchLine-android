package com.eryuksa.catchline_android.ui.game

import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.request.RequestOptions
import com.eryuksa.catchline_android.R
import com.eryuksa.catchline_android.common.GlideApp
import com.eryuksa.catchline_android.model.Content

import jp.wasabeef.glide.transformations.BlurTransformation

@BindingAdapter("playing")
fun setAudioButton(imageView: ImageView, playing: Boolean) {
    if (playing) {
        imageView.setImageResource(R.drawable.playing_circle_24)
    }else {
        imageView.setImageResource(R.drawable.paused_circle_24)
    }
}

// gameState - 0: 기본, 1: 힌트1, 2: 힌트2, 3: 썸네일 덜 흐리게, 4: 정답 캐치
@BindingAdapter("gameState", "title")
fun setTitleText(textView: TextView, gameState: Int, title: String) {
    when (gameState) {
        0 -> textView.text = textView.context.getString(R.string.question_mark)
        1 -> textView.text = convertToHintText(title)
        2 -> {
            textView.text = textView.text.replaceRange(0, 1, title[0].toString())
        }
        3 -> {}
        else -> {
            textView.text = title
        }
    }
}

// gameState - 0: 기본, 1: 힌트1, 2: 힌트2, 3: 썸네일 덜 흐리게, 4: 정답 캐치
@BindingAdapter("gameState", "thumbnailUrl")
fun setThumbnail(imageView: ImageView, gameState: Int, thumbnailUrl: String) {
    when (gameState) {
        0 -> GlideApp.with(imageView)
            .load(thumbnailUrl)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(24, 7)))
            .into(imageView)
        3 -> GlideApp.with(imageView)
            .load(thumbnailUrl)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(24, 2)))
            .into(imageView)
        4 -> GlideApp.with(imageView)
            .load(thumbnailUrl)
            .into(imageView)
    }
}

private fun convertToHintText(title: String): String {
    val sb = StringBuilder()
    title.forEach {
        if (it.isWhitespace()) {
            sb.append(it)
        } else {
            sb.append('O')
        }
    }
    return sb.toString()
}

@BindingAdapter("gameState")
fun setHintOrNextImage(imageButton: ImageButton, gameState: Int) {
    if (gameState > 3) {
        imageButton.setImageResource(R.drawable.ic_baseline_forward_24)
    } else {
        imageButton.setImageResource(R.drawable.ic_round_hint_24)
    }
}

@BindingAdapter("isVisible")
fun setCustomVisibility(imageButton: ImageButton, isVisible: Boolean) {
    if (isVisible) { imageButton.visibility = View.VISIBLE }
    else { imageButton.visibility = View.GONE }
}

/**
 * Content가 변경되면 입력창을 클리어한다
 */
@BindingAdapter("content")
fun clearEditText(editText: EditText, content: Content) {
    editText.editableText.clear()
}