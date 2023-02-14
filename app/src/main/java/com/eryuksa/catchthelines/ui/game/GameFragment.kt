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
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.eryuksa.catchline_android.R
import com.eryuksa.catchline_android.databinding.FragmentGameBinding
import com.eryuksa.catchthelines.common.removeOverScroll
import com.eryuksa.catchthelines.data.dto.GameItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

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
        initPosterViewPager()
        initOnClickListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    private fun initPosterViewPager() {
        binding.viewPagerPoster.apply {
            offscreenPageLimit = 3
            adapter = posterAdapter
            this.removeOverScroll()
            this.setHorizontalPadding(padding = (resources.displayMetrics.widthPixels * 0.15).toInt())
            setPageTransformer(buildPageTransformer())
        }
    }

    private fun initOnClickListener() {
        binding.btnSubmitTitle.setOnClickListener {
            viewModel.checkUserInputMatchedWithAnswer()
        }

        binding.btnPlay.setOnClickListener {
            when (mediaPlayer.isPlaying) {
                true -> stopLineAudioProcess()
                false -> startLineAudioProcess()
            }
        }
    }

    private fun ViewPager2.setHorizontalPadding(padding: Int) {
        this.setPadding(padding, 0, padding, 0)
    }

    private fun buildPageTransformer(): CompositePageTransformer {
        return CompositePageTransformer().also {
            it.addTransformer(
                MarginPageTransformer(resources.getDimensionPixelOffset(R.dimen.game_poster_margin))
            )
            it.addTransformer { eachPageView: View, positionFromCenter: Float ->
                val scale = 1 - abs(positionFromCenter)
                eachPageView.scaleY = 0.85f + 0.15f * scale
            }
        }
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
