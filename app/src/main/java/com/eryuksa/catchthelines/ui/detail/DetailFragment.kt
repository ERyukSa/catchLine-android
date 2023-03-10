package com.eryuksa.catchthelines.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import com.google.android.exoplayer2.Player
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding: FragmentDetailBinding
        get() = _binding!!

    private val viewModel: DetailViewModel by viewModels { ContentViewModelFactory.getInstance() }
    private val args: DetailFragmentArgs by navArgs()

    private lateinit var audioPlayer: ExoPlayer
    private var lastPlayedLineIndex = 0

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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    bindDataToViews(uiState)
                    setUpAudio(uiState.audioUrls)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        (binding.btnLine1 as MaterialButton).setIconResource(R.drawable.icon_play_24)
        (binding.btnLine2 as MaterialButton).setIconResource(R.drawable.icon_play_24)
        lastPlayedLineIndex = 0
        audioPlayer = ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
            exoPlayer.pauseAtEndOfMediaItems = true
            exoPlayer.addListener(audioPlayerListener)
        }
    }

    override fun onStop() {
        super.onStop()
        audioPlayer.removeListener(audioPlayerListener)
        audioPlayer.release()
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
            setPadding(
                paddingLeft,
                paddingTop,
                paddingRight,
                requireContext().getNavigationBarHeight()
            )
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
        audioPlayer.setMediaItems(
            urls.map { MediaItem.fromUri(it) }
        )
        audioPlayer.prepare()
    }

    private fun initAudioButtons() {
        arrayOf(binding.btnLine1, binding.btnLine2).forEachIndexed { i, button ->
            button.setOnClickListener {
                if (lastPlayedLineIndex == i) {
                    if (audioPlayer.isPlaying) {
                        audioPlayer.pause()
                    } else {
                        audioPlayer.play()
                    }
                } else {
                    lastPlayedLineIndex = i
                    audioPlayer.seekTo(i, 0)
                    audioPlayer.play()
                }
            }
        }
    }

    private val audioPlayerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                if (lastPlayedLineIndex == 0) {
                    (binding.btnLine1 as MaterialButton).setIconResource(R.drawable.icon_pause_24)
                    (binding.btnLine2 as MaterialButton).setIconResource(R.drawable.icon_play_24)
                } else {
                    (binding.btnLine1 as MaterialButton).setIconResource(R.drawable.icon_play_24)
                    (binding.btnLine2 as MaterialButton).setIconResource(R.drawable.icon_pause_24)
                }
            } else {
                if (lastPlayedLineIndex == 0) {
                    (binding.btnLine1 as MaterialButton).setIconResource(R.drawable.icon_play_24)
                } else {
                    (binding.btnLine2 as MaterialButton).setIconResource(R.drawable.icon_play_24)
                }
            }
        }
    }
}
