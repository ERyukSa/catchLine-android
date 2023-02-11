package com.eryuksa.catchthelines.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.eryuksa.catchline_android.R

class DetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val posterView = requireActivity().findViewById<ImageView>(R.id.poster_img_detail)
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/original/AvMbd9rvH6cpbhXc1YTQsdc9bAh.jpg")
            .into(posterView)
    }
}