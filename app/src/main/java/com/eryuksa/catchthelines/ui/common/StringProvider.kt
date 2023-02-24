package com.eryuksa.catchthelines.ui.common

import android.app.Application
import androidx.annotation.StringRes

class StringProvider private constructor() {

    fun getString(@StringRes resId: Int): String =
        application.getString(resId)

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String =
        application.getString(resId, *formatArgs)

    companion object {

        private lateinit var application: Application
        private var instance: StringProvider? = null

        fun initialize(_application: Application) {
            instance ?: synchronized(this) {
                instance ?: StringProvider().also {
                    instance = it
                    application = _application
                }
            }
        }

        fun getInstance(): StringProvider =
            instance ?: throw UninitializedPropertyAccessException()
    }
}
