package com.eryuksa.catchthelines.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "encountered_content")
data class EncounteredContent(
    @PrimaryKey
    val id: Int,
    val updatedTime: Long,
    val isCaught: Boolean
)
