package com.eryuksa.catchthelines.data.dto

import com.google.gson.annotations.SerializedName

data class ContentGenre(val name: String)

data class ContentDetail(
    @SerializedName("title") val title: String,
    @SerializedName("backdrop_path") private val backdropPosterPath: String,
    @SerializedName("poster_path") private val mainPosterPath: String,
    @SerializedName("genres") val genres: List<ContentGenre>,
    @SerializedName("overview") val overview: String,
    @SerializedName("tagline") val shortSummary: String,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("runtime") val runningTime: Int
) {
    val backdropPosterUrl
        get() = "$POSTER_BASE_URL$backdropPosterPath"
    val mainPosterPathUrl
        get() = "$POSTER_BASE_URL$mainPosterPath"

    companion object {
        private const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/original"
    }
}
