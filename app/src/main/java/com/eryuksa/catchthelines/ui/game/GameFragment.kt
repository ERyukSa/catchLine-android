package com.eryuksa.catchthelines.ui.game

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
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
import com.eryuksa.catchthelines.data.dto.GameItem
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlin.math.abs

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding!!
    private val viewModel: GameViewModel by viewModels()

    private val posterAdapter: PosterViewPagerAdapter by lazy {
        PosterViewPagerAdapter(this)
    }

    private val audioPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(requireContext()).build().apply { pauseAtEndOfMediaItems = true }
    }
    private var gameItems: List<GameItem> = emptyList()

    private val switchAudioLineOnPageChange = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            stopLineAudioProcess()
            audioPlayer.seekTo(position, 0)
        }
    }
    var isHintOpen = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
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
        binding.viewPagerPoster.apply {
            offscreenPageLimit = 3
            adapter = posterAdapter
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
            gameItems.observe(viewLifecycleOwner) { gameItems ->
                posterAdapter.posterUrls = gameItems.map(GameItem::posterUrl)
                this@GameFragment.gameItems = gameItems
                audioPlayer.setUpLines()
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

        val anim1 = ObjectAnimator.ofFloat(binding.btnHintLine2, "TranslationY", -230f).setDuration(500)
        val anim12 = ObjectAnimator.ofFloat(binding.btnHintCharactersCount, "TranslationY", -400f).setDuration(500)
        val anim13 = ObjectAnimator.ofFloat(binding.btnHintFirstCharacter, "TranslationY", -570f).setDuration(500)
        val anim14 = ObjectAnimator.ofFloat(binding.btnHintClearerPoster, "TranslationY", -710f).setDuration(500)
        val anim1In = ObjectAnimator.ofFloat(binding.btnHintLine2, "TranslationY", 0f).setDuration(500)

        val txOut = PropertyValuesHolder.ofFloat("translationX", 150f)
        val txIn = PropertyValuesHolder.ofFloat("translationX", 0f)
        val tyOut = PropertyValuesHolder.ofFloat("translationY", -230f)
        val tyIn = PropertyValuesHolder.ofFloat("translationY", 0f)
        val anim2 = ObjectAnimator.ofPropertyValuesHolder(binding.btnHintCharactersCount, txOut, tyOut).setDuration(500)
        val anim2In = ObjectAnimator.ofPropertyValuesHolder(binding.btnHintCharactersCount, txIn, tyIn).setDuration(500)

        val txOut2 = PropertyValuesHolder.ofFloat("translationX", 300f)
        val txOut2In = PropertyValuesHolder.ofFloat("translationX", 0f)
        val tyOut2 = PropertyValuesHolder.ofFloat("translationY", -230f)
        val tyOut2In = PropertyValuesHolder.ofFloat("translationY", 0f)
        val anim3 = ObjectAnimator.ofPropertyValuesHolder(binding.btnHintFirstCharacter, txOut2, tyOut2).setDuration(500)
        val anim3In = ObjectAnimator.ofPropertyValuesHolder(binding.btnHintFirstCharacter, txOut2In, tyOut2In).setDuration(500)

        val txOut3 = PropertyValuesHolder.ofFloat("translationX", 450f)
        val txOut3In = PropertyValuesHolder.ofFloat("translationX", 0f)
        val tyOut3 = PropertyValuesHolder.ofFloat("translationY", -230f)
        val tyOut3In = PropertyValuesHolder.ofFloat("translationY", 0f)
        val anim4 = ObjectAnimator.ofPropertyValuesHolder(binding.btnHintClearerPoster, txOut3, tyOut3).setDuration(500)
        val anim4In = ObjectAnimator.ofPropertyValuesHolder(binding.btnHintClearerPoster, txOut3In, tyOut3In).setDuration(500)

        binding.btnSelectHint.setOnClickListener {
            if (isHintOpen) {
                anim1In.start()
                anim2In.start()
                anim3In.start()
                anim4In.start()
            } else {
                anim1.start()
                anim12.start()
                anim13.start()
                anim14.start()
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
