package com.eryuksa.catchthelines.ui.game.utility

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.eryuksa.catchthelines.R
import com.eryuksa.catchthelines.ui.game.uistate.GameMode
import com.eryuksa.catchthelines.ui.game.uistate.PosterItem
import jp.wasabeef.glide.transformations.BlurTransformation

@BindingAdapter("resultText", "gameMode")
fun TextView.setResultText(resultText: String, gameMode: GameMode) {
    text = when (gameMode) {
        GameMode.WATCHING -> null
        GameMode.IN_GAME -> if (resultText.isBlank()) {
            null
        } else {
            resources.getString(R.string.game_result_wrong, resultText)
        }
        GameMode.CATCH -> resources.getString(R.string.game_result_catch_the_line, resultText)
        GameMode.ALL_CATCH -> resources.getString(R.string.game_result_all_catch)
    }
}

@BindingAdapter("gameMode")
fun ViewPager2.setElevationBy(gameMode: GameMode) {
    elevation = if (gameMode == GameMode.CATCH) {
        resources.getDimension(R.dimen.game_poster_elevation_over_dark_cover)
    } else {
        0f
    }
}

@SuppressLint("CheckResult")
@BindingAdapter("posterItem")
fun ImageView.setPosterImage(posterItem: PosterItem) {
    Glide.with(context)
        .load(posterItem.posterUrl)
        .apply {
            if (posterItem.blurDegree > 0) {
                this.apply(
                    RequestOptions.bitmapTransform(
                        BlurTransformation(25, posterItem.blurDegree)
                    )
                )
            }
        }
        .into(this)
}
