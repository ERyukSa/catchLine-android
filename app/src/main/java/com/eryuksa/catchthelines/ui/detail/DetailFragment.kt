package com.eryuksa.catchthelines.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.eryuksa.catchthelines.databinding.FragmentDetailBinding
import com.eryuksa.catchthelines.di.ContentViewModelFactory

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding: FragmentDetailBinding
        get() = _binding!!

    private val viewModel: DetailViewModel by viewModels { ContentViewModelFactory.getInstance() }

    private val args: DetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.showContentDetail(args.contentId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        binding.ivMainPoster.clipToOutline = true
        binding.btnNavigateBack.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.contentDetail.observe(viewLifecycleOwner) { contentDetail ->
            with(contentDetail) {
                Glide.with(this@DetailFragment)
                    .load(backdropPosterUrl)
                    .into(binding.ivBackdropPoster)

                Glide.with(this@DetailFragment)
                    .load(mainPosterPathUrl)
                    .into(binding.ivMainPoster)

                binding.tvTitle.text = title
                binding.tvGenres.text = genres.joinToString(", ") { it.name }
                binding.tvReleaseDate.text = "$releaseDate 개봉"
                binding.tvRunningTime.text = "${runningTime}분"
                binding.tvShortSummary.text = shortSummary
                binding.tvOverview.text = overview
            }
        }
    }
}
