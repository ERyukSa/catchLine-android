package com.eryuksa.catchthelines

import android.app.Application
import com.eryuksa.catchthelines.di.ContentViewModelFactory

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ContentViewModelFactory.initialize(this)
    }
}
