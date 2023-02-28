package com.eryuksa.catchthelines.data.datasource.local

import androidx.room.TypeConverter
import com.eryuksa.catchthelines.data.dto.ContentGenre

class ContentTypeConverter {

    @TypeConverter
    fun fromStringsToSeparatedString(strings: List<String>): String =
        strings.joinToString(" ")

    @TypeConverter
    fun toStrings(separatedString: String): List<String> =
        separatedString.split(" ")

    @TypeConverter
    fun fromContentGenres(genres: List<ContentGenre>): String =
        genres.joinToString(" ") { it.name }

    @TypeConverter
    fun toContentGenres(namesString: String): List<ContentGenre> =
        namesString.split(" ").map { name -> ContentGenre(name) }
}
