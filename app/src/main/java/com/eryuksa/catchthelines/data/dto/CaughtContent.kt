package com.eryuksa.catchthelines.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "caught_content")
data class CaughtContent(@PrimaryKey val id: Int, val updatedTime: Long)
