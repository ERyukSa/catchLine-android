package com.eryuksa.catchthelines.repository

import com.eryuksa.catchthelines.model.Content

interface GameAssetDataSource {

    // 지금까지 만난 컨텐츠 수
    fun getChallengeNumber(): Int

    // 캐치한 컨텐츠 수
    fun getCaughtNumber(): Int

    // 출제할 컨텐츠 리스트
    fun getUncaughtContents(): List<Content>
}
