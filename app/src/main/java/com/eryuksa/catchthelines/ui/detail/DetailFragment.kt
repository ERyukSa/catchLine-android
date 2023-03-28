package com.eryuksa.catchthelines.ui.detail

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.eryuksa.catchthelines.databinding.FragmentDetailBinding
import com.eryuksa.catchthelines.ui.common.getNavigationBarHeight
import com.eryuksa.catchthelines.ui.common.getStatusBarHeight
import com.eryuksa.catchthelines.ui.common.setLayoutVerticalLimit
import com.eryuksa.catchthelines.ui.common.setStatusBarIconColor
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding: FragmentDetailBinding
        get() = _binding!!

    private val viewModel: DetailViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()

    private lateinit var audioPlayer: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.requestDetailUiState(args.contentId, args.audioUrls)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
        }
        setUpWindowAppearance()

        binding.btnNavigateBack.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSharedElementEnterTransition()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    setUpAudioPlayer(uiState.audioUrls)
                    binding.playerView.player = audioPlayer
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        audioPlayer.run {
            viewModel.saveAudioPlayState(playWhenReady, currentMediaItemIndex, currentPosition)
            release()
        }
    }

    private fun setUpWindowAppearance() {
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

    private fun setUpSharedElementEnterTransition() {
        binding.ivMainPoster.transitionName = args.contentId.toString()
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
            .apply { duration = 700 }
        postponeEnterTransition()
        binding.root.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    private fun setUpAudioPlayer(urls: List<String>) {
        audioPlayer = ExoPlayer.Builder(requireContext()).build().apply {
            setMediaItems(urls.map { MediaItem.fromUri(it) })
            seekTo(viewModel.currentAudioIndex.value, viewModel.audioPosition)
            playWhenReady = viewModel.playWhenReady
            pauseAtEndOfMediaItems = true
            repeatMode = ExoPlayer.REPEAT_MODE_ONE
            prepare()
        }
    }
}
