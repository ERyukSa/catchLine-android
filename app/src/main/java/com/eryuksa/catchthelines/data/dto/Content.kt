package com.eryuksa.catchthelines.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "content")
data class Content(
    @PrimaryKey
    @SerializedName("id")
    val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("posterUrl") val posterUrl: String,
    @SerializedName("lineAudioUrls") val lineAudioUrls: List<String>,
    @SerializedName("type") val type: String
)
