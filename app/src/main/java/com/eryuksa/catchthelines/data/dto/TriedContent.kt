package com.eryuksa.catchthelines.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tried_content")
data class TriedContent(
    @PrimaryKey
    val id: Int,
    val updatedTime: Long,
    val isCaught: Boolean
)
