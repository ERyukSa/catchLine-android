package com.eryuksa.catchthelines.repository

import android.media.MediaPlayer
import com.eryuksa.catchthelines.common.AssetLoader
import com.eryuksa.catchthelines.common.MediaLoader
import com.eryuksa.catchthelines.model.Content
import com.eryuksa.catchthelines.model.GameData
import com.google.gson.Gson
import java.util.Collections.emptyList

class GameDataSourceImpl(
    private val assetLoader: AssetLoader,
    private val mediaLoader: MediaLoader
) : GameAssetDataSource, GameMediaDataSource {

    /**
     * Gson 라이브러리로 game.json 파일을 GameData 인스턴스로 변환한다.
     */
    private val gameData: GameData? by lazy {
        Gson().fromJson(assetLoader.getJsonString("game.json"), GameData::class.java)
    }

    override fun getCaughtNumber(): Int {
        return gameData?.caughtNumber ?: 0
    }

    override fun getChallengeNumber(): Int {
        return gameData?.challengeNumber ?: 0
    }

    override fun getUncaughtContents(): List<Content> {
        return gameData?.contentList ?: emptyList()
    }

    override fun getMediaPlayer(fileName: String): MediaPlayer {
        return mediaLoader.getMediaPlayer(fileName)
    }

    /* JSONObject로 직접 파싱하는 버전
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
     */
}
