package com.eryuksa.catchline_android.repository

import android.media.MediaPlayer
import android.util.Log
import com.eryuksa.catchline_android.common.AssetLoader
import com.eryuksa.catchline_android.common.MediaLoader
import com.eryuksa.catchline_android.model.Content
import org.json.JSONObject

class GameDataSourceImpl(
    private val assetLoader: AssetLoader,
    private val mediaLoader: MediaLoader
) : GameAssetDataSource, GameMediaDataSource {

    /**
     * game.json 파일을 JSONObject로 변환한다.
     */
    private val gameDataJson: JSONObject? by lazy {
        assetLoader.getJsonObject("game.json")
    }

    override fun getCaughtNumber(): Int {
        return gameDataJson?.getInt("caught_contents_number") ?: 0
    }

    override fun getChallengeNumber(): Int {
        return gameDataJson?.getInt("challenged_contents_number") ?: 0
    }

    override fun getUncaughtContents(): List<Content> {
        return gameDataJson?.let {
            getContentsFromJson(it)
        } ?: emptyList()
    }

    /**
     * gameDataJsonString에서 Content List를 파싱한다.
     */
    private fun getContentsFromJson(jsonObject: JSONObject): List<Content> {
        val contentList = mutableListOf<Content>()
        val contentsJsonArray = jsonObject.getJSONArray("contents")

        for (i in 0 until contentsJsonArray.length()) {
            val contentJson = contentsJsonArray.getJSONObject(i)
            val id = contentJson.getInt("id")
            val title = contentJson.getString("title")
            val audioName = contentJson.getString("audio_name")
            val posterUrl = contentJson.getString("poster_url")
            val lineSummary = contentJson.getString("line_summary")
            val showed = contentJson.getBoolean("showed")
            val caught = contentJson.getBoolean("caught")
            val apiId = contentJson.getInt("api_id")
            val content =
                Content(id, title, audioName, posterUrl, lineSummary, showed, caught, apiId)

            contentList.add(content)
        }

        return contentList
    }

    override fun getMediaPlayer(fileName: String): MediaPlayer {
        return mediaLoader.getMediaPlayer(fileName)
    }
}