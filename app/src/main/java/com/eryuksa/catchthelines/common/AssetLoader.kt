package com.eryuksa.catchthelines.common

import android.content.Context

class AssetLoader(private val context: Context) {

    // JSON String 반환
    fun getJsonString(fileName: String): String? {
        return kotlin.runCatching {
            loadAssetAsString(fileName)
        }.getOrNull()
    }

    // asset file 로드 -> 문자열 변환
    private fun loadAssetAsString(fileName: String): String {
        return context.assets.open(fileName).use { inputStream ->
            val size = inputStream.available()
            val byteArray = ByteArray(size)
            inputStream.read(byteArray)
            String(byteArray)
        }
    }
}
