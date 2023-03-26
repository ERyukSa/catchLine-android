package com.eryuksa.catchthelines.ui.game.uistate

import com.eryuksa.catchthelines.data.dto.Content

data class ContentItem(
    override val id: Int,
    override val posterUrl: String,
    val title: String,
    val audioUrls: List<String>,
    override val blurDegree: Int
) : PosterItem {

    fun toClearerPosterContent(): ContentItem =
        this.copy(blurDegree = CLEARER_BLUE_DEGREE)

    companion object {

        private const val DEFAULT_BLUE_DEGREE = 6
        private const val CLEARER_BLUE_DEGREE = 3

        fun from(content: Content): ContentItem {
            return ContentItem(
                id = content.id,
                posterUrl = content.posterUrl,
                title = content.title,
                audioUrls = content.lineAudioUrls,
                blurDegree = DEFAULT_BLUE_DEGREE
            )
        }
    }
}
