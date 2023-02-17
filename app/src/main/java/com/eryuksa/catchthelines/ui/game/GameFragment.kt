package com.eryuksa.catchthelines.ui.game

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.eryuksa.catchline_android.R
import com.eryuksa.catchline_android.databinding.FragmentGameBinding
import com.eryuksa.catchthelines.common.removeOverScroll
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlin.math.abs

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding!!
    private val viewModel: GameViewModel by viewModels()

    private val posterAdapter: PosterViewPagerAdapter by lazy {
        PosterViewPagerAdapter()
    }

    private val audioPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(requireContext()).build().apply { pauseAtEndOfMediaItems = true }
    }
    private val gameItems: List<GameItem>
        get() = viewModel.gameItems.value ?: emptyList()

    private val switchAudioLineOnPageChange = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            stopLineAudioProcess()
            audioPlayer.seekTo(position, 0)
            viewModel.currentPagePosition = position
        }
    }
    var isHintOpen = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false).apply {
            viewModel = this@GameFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
            playerViewLine.player = audioPlayer
        }
        binding.playerViewLine.player = audioPlayer
        initPosterViewPager()
        initOnClickListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    private fun initPosterViewPager() {
        binding.viewPagerPoster.run {
            offscreenPageLimit = 3
            adapter = posterAdapter
            this.setCurrentItem(viewModel.currentPagePosition, false)
            this.removeOverScroll()
            this.setHorizontalPadding((resources.displayMetrics.widthPixels * 0.15).toInt())
            setPageTransformer(buildPageTransformer())
            registerOnPageChangeCallback(switchAudioLineOnPageChange)
        }
    }

    private fun initOnClickListener() {
        binding.btnSubmitTitle.setOnClickListener {
            viewModel.checkUserInputMatchedWithAnswer()
        }

        binding.btnPlayStop.setOnClickListener {
            when (audioPlayer.isPlaying) {
                true -> stopLineAudioProcess()
                false -> startLineAudioProcess()
            }
        }

        binding.btnHintClearerPoster.setOnClickListener {
            viewModel.useHintClearerPoster()
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
        audioPlayer.pause()
        binding.btnPlayStop.setImageResource(R.drawable.icon_play_24)
    }

    private fun startLineAudioProcess() {
        audioPlayer.play()
        binding.btnPlayStop.setImageResource(R.drawable.icon_pause_24)
    }

    private fun observeData() {
        with(viewModel) {
            gameItems.observe(viewLifecycleOwner) { items ->
                posterAdapter.submitList(items)
                audioPlayer.setUpLines()
            }

            hintText.observe(viewLifecycleOwner) { hintText ->
                binding.tvHint.text = hintText
            }

            feedbackText.observe(viewLifecycleOwner) { feedbackText ->
                binding.tvFeedback.text = feedbackText
            }
        }

        val animOut1 = ObjectAnimator.ofFloat(binding.btnHintLine2, "TranslationY", -180f).setDuration(400)
        val animOut2 = ObjectAnimator.ofFloat(binding.btnHintCharactersCount, "TranslationY", -360f).setDuration(400)
        val animOut3 = ObjectAnimator.ofFloat(binding.btnHintFirstCharacter, "TranslationY", -540f).setDuration(400)
        val animOut4 = ObjectAnimator.ofFloat(binding.btnHintClearerPoster, "TranslationY", -720f).setDuration(400)
        val anim1In1 = ObjectAnimator.ofFloat(binding.btnHintLine2, "TranslationY", 0f).setDuration(400)
        val anim1In2 = ObjectAnimator.ofFloat(binding.btnHintCharactersCount, "TranslationY", 0f).setDuration(400)
        val anim1In3 = ObjectAnimator.ofFloat(binding.btnHintFirstCharacter, "TranslationY", 0f).setDuration(400)
        val anim1In4 = ObjectAnimator.ofFloat(binding.btnHintClearerPoster, "TranslationY", 0f).setDuration(400)

        binding.btnSelectHint.setOnClickListener {
            if (isHintOpen) {
                anim1In1.start()
                anim1In2.start()
                anim1In3.start()
                anim1In4.start()
            } else {
                animOut1.start()
                animOut2.start()
                animOut3.start()
                animOut4.start()
            }
            isHintOpen = isHintOpen.not()
        }
    }

    private fun ExoPlayer.setUpLines() {
        audioPlayer.addMediaItems(
            gameItems.map { item ->
                MediaItem.fromUri(item.lineAudioUrls[0])
            }
        )
        audioPlayer.prepare()
    }
}
