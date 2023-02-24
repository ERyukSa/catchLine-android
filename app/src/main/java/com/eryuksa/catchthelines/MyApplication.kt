package com.eryuksa.catchthelines

import android.app.Application
import com.eryuksa.catchthelines.di.ContentViewModelFactory
import com.eryuksa.catchthelines.ui.common.StringProvider

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ContentViewModelFactory.initialize(this)
        StringProvider.initialize(this)
    }
}
