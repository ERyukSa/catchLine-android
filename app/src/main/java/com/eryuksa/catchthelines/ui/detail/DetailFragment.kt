package com.eryuksa.catchthelines.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.eryuksa.catchthelines.R
import com.eryuksa.catchthelines.databinding.FragmentDetailBinding
import com.eryuksa.catchthelines.di.ContentViewModelFactory
import com.eryuksa.catchthelines.ui.common.getNavigationBarHeight
import com.eryuksa.catchthelines.ui.common.getStatusBarHeight
import com.eryuksa.catchthelines.ui.common.setLayoutVerticalLimit
import com.eryuksa.catchthelines.ui.common.setStatusBarIconColor
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding: FragmentDetailBinding
        get() = _binding!!

    private val viewModel: DetailViewModel by viewModels { ContentViewModelFactory.getInstance() }
    private val args: DetailFragmentArgs by navArgs()

    private val audioPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(requireContext()).build().apply { pauseAtEndOfMediaItems = true }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getDetailUiState(args.contentId, args.audioUrls)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        initWindowAppearance()

        binding.ivMainPoster.clipToOutline = true
        binding.btnNavigateBack.setOnClickListener {
            findNavController().navigateUp()
        }
        initAudioButtons()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            bindDataToViews(uiState)
            setUpAudio(uiState.audioUrls)
        }
    }

    private fun initWindowAppearance() {
        requireActivity().window.run {
            setLayoutVerticalLimit(hasLimit = false)
            setStatusBarIconColor(isDark = false)
        }

        (binding.btnNavigateBack.layoutParams as ConstraintLayout.LayoutParams).run {
            topMargin = requireContext().getStatusBarHeight()
            binding.btnNavigateBack.layoutParams = this
        }
        binding.root.run {
            setPadding(paddingLeft, paddingTop, paddingRight, requireContext().getNavigationBarHeight())
        }
    }

    private fun initAudioButtons() {
        binding.btnLine1.setOnClickListener {
            audioPlayer.seekTo(0, 0)
            audioPlayer.play()
        }
        binding.btnLine2.setOnClickListener {
            audioPlayer.seekTo(1, 0)
            audioPlayer.play()
        }
    }

    private fun bindDataToViews(uiState: ContentDetailUiState) {
        with(uiState) {
            Glide.with(this@DetailFragment)
                .load(backdropPosterUrl)
                .into(binding.ivBackdropPoster)

            Glide.with(this@DetailFragment)
                .load(mainPosterPathUrl)
                .into(binding.ivMainPoster)

            binding.tvTitle.text = title
            binding.tvGenres.text = genres.joinToString(", ") { it.name }
            binding.tvReleaseDate.text = getString(R.string.detail_release_date, releaseDate)
            binding.tvRunningTime.text = getString(R.string.detail_running_time, runningTime)
            binding.tvShortSummary.text = "\"$shortSummary\""
            binding.tvOverview.text = overview
        }
    }

    private fun setUpAudio(urls: List<String>) {
        audioPlayer.addMediaItems(
            urls.map { MediaItem.fromUri(it) }
        )
        audioPlayer.prepare()
    }
}
