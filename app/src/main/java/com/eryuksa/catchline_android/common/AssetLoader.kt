package com.eryuksa.catchline_android.common

import android.content.Context
import org.json.JSONObject

class AssetLoader(private val context: Context) {

    // JSON String 반환
    fun getJsonObject(fileName: String): JSONObject? {
        return kotlin.runCatching {
            JSONObject(loadAssetAsString(fileName))
        }.getOrNull()
    }

    // asset file 로드 -> 문자열 변환
    private fun loadAssetAsString(fileName: String): String {
        return context.assets.open(fileName).use { inputStream ->
            val size = inputStream.available() // inputStream 길이
            val byteArray = ByteArray(size)    // 길이만큼 ByteArray 선언
            inputStream.read(byteArray)        // byteArray에 inputStream 읽어오기
            String(byteArray)                  // 바이트 -> 문자열로 변환
        }
    }


}