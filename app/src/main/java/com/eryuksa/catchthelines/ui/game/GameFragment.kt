package com.eryuksa.catchthelines.ui.game

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.eryuksa.catchline_android.databinding.FragmentGameBinding
import com.eryuksa.catchthelines.data.dto.GameItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding!!
    private val viewModel: GameViewModel by viewModels()

    private val posterAdapter: PosterViewPagerAdapter by lazy {
        PosterViewPagerAdapter(this)
    }

    lateinit var gameItem: GameItem
    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(requireContext(), Uri.parse(gameItem.lineAudioUrls[0])).apply {
            this.setOnCompletionListener {
                lineAudioTimer.cancel()
            }
        }
    }
    private lateinit var lineAudioTimer: Job

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        binding.viewPagerPoster.adapter = posterAdapter
        binding.btnSubmitTitle.setOnClickListener {
            viewModel.checkUserTitleIsMatched()
        }
        binding.btnPlay.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                stopLineAudioProcess()
            } else {
                startLineAudioProcess()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    private fun stopLineAudioProcess() {
        mediaPlayer.pause()
        lineAudioTimer.cancel()
    }

    private fun startLineAudioProcess() {
        lineAudioTimer = lifecycleScope.launch(Dispatchers.Main.immediate) {
            binding.sliderLinePlayingProgress.valueTo = mediaPlayer.duration.toFloat()
            mediaPlayer.start()
            while (mediaPlayer.isPlaying) {
                binding.sliderLinePlayingProgress.value = mediaPlayer.currentPosition.toFloat()
                delay(500)
            }
        }
    }

    private fun observeData() {
        with(viewModel) {
            gameItems.observe(viewLifecycleOwner) { gameItems ->
                gameItem = gameItems.getOrNull(1) ?: return@observe
                posterAdapter.posterUrls = gameItems.map(GameItem::posterUrl)
            }

            hintText.observe(viewLifecycleOwner) { hintText ->
                binding.tvHint.text = hintText
            }

            feedbackText.observe(viewLifecycleOwner) { feedbackText ->
                binding.tvFeedback.text = feedbackText
            }

            availableHintCount.observe(viewLifecycleOwner) { hintCount ->
                binding.tvAvailableHintCount.text = hintCount.toString()
            }
        }
    }
}
