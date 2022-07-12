package com.eryuksa.catchline_android.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eryuksa.catchline_android.R

/*
- API 키 (v3 auth)
312d719efb84a68ee202a8ce06eec62d
- API 요청 예
https://api.themoviedb.org/3/movie/550?api_key=312d719efb84a68ee202a8ce06eec62d
- API 읽기 액세스 토큰 (v4 auth)
eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIzMTJkNzE5ZWZiODRhNjhlZTIwMmE4Y2UwNmVlYzYyZCIsInN1YiI6IjYyYzY3MTM3MDViNTQ5MDA0ZjNkOTUwNiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.8otT_kF-pz9F_OscU_S2flTIhW_qXtR5OUL7FZYuG4A
 */

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}