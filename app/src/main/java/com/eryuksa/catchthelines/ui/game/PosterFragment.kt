package com.eryuksa.catchthelines.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.eryuksa.catchline_android.databinding.FragmentPosterBinding
import kotlin.properties.Delegates

class PosterFragment : Fragment() {

    private var _binding: FragmentPosterBinding? = null
    private val binding: FragmentPosterBinding
        get() = _binding!!

    private var posterUrl: String by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        posterUrl = requireArguments().getString(KEY_POSTER_URL) ?: throw IllegalArgumentException()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPosterBinding.inflate(inflater, container, false)
        Glide.with(this)
            .load(posterUrl)
            .into(binding.ivPoster)
        return binding.root
    }

    companion object {

        private const val KEY_POSTER_URL = "POSTER_URL"

        fun newInstance(posterUrl: String): PosterFragment {
            val bundle = bundleOf(KEY_POSTER_URL to posterUrl)
            return PosterFragment().apply { arguments = bundle }
        }
    }
}
