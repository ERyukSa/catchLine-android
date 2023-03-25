package com.eryuksa.catchthelines.ui.game.uistate

abstract class PosterInfo(
    open val id: Int,
    open val posterUrl: String,
    open val blurDegree: Int
) {
    val canDrag get() = blurDegree == 0
}
