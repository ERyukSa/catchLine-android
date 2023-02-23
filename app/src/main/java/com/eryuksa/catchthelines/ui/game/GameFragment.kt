package com.eryuksa.catchthelines.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Transformations
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.eryuksa.catchthelines.R
import com.eryuksa.catchthelines.databinding.FragmentGameBinding
import com.eryuksa.catchthelines.di.ContentViewModelFactory
import com.eryuksa.catchthelines.ui.common.ButtonOpener
import com.eryuksa.catchthelines.ui.common.removeOverScroll
import com.eryuksa.catchthelines.ui.game.uistate.GameItem
import com.eryuksa.catchthelines.ui.game.uistate.Hint
import com.eryuksa.catchthelines.ui.game.uistate.NoInput
import com.eryuksa.catchthelines.ui.game.uistate.UserCaughtTheLine
import com.eryuksa.catchthelines.ui.game.uistate.UserInputWrong
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlin.math.abs

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding!!
    private val viewModel: GameViewModel by viewModels { ContentViewModelFactory.getInstance() }

    private val posterAdapter: PosterViewPagerAdapter by lazy {
        PosterViewPagerAdapter { contentId: Int ->
            findNavController().navigate(
                GameFragmentDirections.gameToDetail(contentId)
            )
        }
    }

    private val audioPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(requireContext()).build().apply { pauseAtEndOfMediaItems = true }
    }

    private lateinit var hintButtonsOpener: ButtonOpener
    private var isHintOpen = false

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
        initPosterViewPager()
        initOnClickListener()
        initHintButtonsAnimation()
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
            this.removeOverScroll()
            this.setHorizontalPadding((resources.displayMetrics.widthPixels * 0.15).toInt())
            setPageTransformer(buildPageTransformer())
            registerOnPageChangeCallback(switchAudioLineOnPageChange)
        }
    }

    private fun initHintButtonsAnimation() {
        binding.btnOpenHint.doOnLayout {
            hintButtonsOpener = ButtonOpener(initialCeilHeight = binding.btnOpenHint.height, margin = 20, duration = 400)
            hintButtonsOpener.run {
                addInnerButton(binding.btnHintClearerPoster)
                addInnerButton(binding.btnHintFirstCharacter)
                addInnerButton(binding.btnHintCharactersCount)
                addInnerButton(binding.btnHintAnotherLine)
            }
        }
    }

    private fun initOnClickListener() {
        binding.btnSubmitTitle.setOnClickListener {
            viewModel.checkUserCatchTheLine(binding.edittextInputTitle.text.toString())
        }

        binding.btnPlayStop.setOnClickListener {
            when (audioPlayer.isPlaying) {
                true -> stopLineAudio()
                false -> startLineAudio()
            }
        }

        binding.btnHintClearerPoster.setOnClickListener {
            viewModel.useHintClearerPoster()
        }
        binding.btnHintFirstCharacter.setOnClickListener {
/*            binding.tvHint.text = getString(
                R.string.game_hint_text_first_character,
                gameItems[viewModel.currentPagePosition].title.first()
            )*/
        }
        binding.btnHintCharactersCount.setOnClickListener {
/*            binding.tvHint.text = getString(
                R.string.game_hint_text_characters_count,
                gameItems[viewModel.currentPagePosition].title.length
            )*/
        }

        binding.btnOpenHint.setOnClickListener {
            if (isHintOpen) {
                hintButtonsOpener.closeButtons()
            } else {
                hintButtonsOpener.openButtons()
            }
            isHintOpen = isHintOpen.not()
        }
    }

    private fun observeData() {
        with(viewModel) {
            gameItems.observe(viewLifecycleOwner) { items ->
                posterAdapter.submitList(items)
            }

            lineAudioUrls.observe(viewLifecycleOwner) { audioUrls ->
                audioPlayer.setUpLines(audioUrls.map { urls -> urls[0] })
            }

            Transformations.distinctUntilChanged(feedbackUiState).observe(viewLifecycleOwner) { feedbackUiState ->
                binding.tvFeedback.text = when (feedbackUiState) {
                    is UserCaughtTheLine ->
                        getString(R.string.game_feedback_catch_the_line, feedbackUiState.title)
                    is UserInputWrong ->
                        getString(R.string.game_feedback_wrong, feedbackUiState.userInput)
                    is NoInput -> ""
                }

                binding.btnSubmitTitle.isEnabled = (feedbackUiState is UserCaughtTheLine).not()
            }

            currentPagePosition.observe(viewLifecycleOwner) { position ->
                stopLineAudio()
                audioPlayer.seekTo(position, 0)
            }

            Transformations.distinctUntilChanged(usedHintState).observe(viewLifecycleOwner) { usedHints ->
                when (usedHints.contains(Hint.CLEARER_POSTER)) {
                    true -> binding.btnHintClearerPoster.setBackgroundResource(R.drawable.circle_button)
                    false -> binding.btnHintClearerPoster.setBackgroundResource(R.drawable.game_unused_hint_circle_button)
                }
            }
        }
    }

    private fun ViewPager2.setHorizontalPadding(padding: Int) {
        this.setPadding(padding, 0, padding, 0)
    }

    private val switchAudioLineOnPageChange = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            viewModel.movePagePosition(position)
        }
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

    private fun stopLineAudio() {
        audioPlayer.pause()
        binding.btnPlayStop.setImageResource(R.drawable.icon_play_24)
    }

    private fun startLineAudio() {
        audioPlayer.play()
        binding.btnPlayStop.setImageResource(R.drawable.icon_pause_24)
    }

    private fun ExoPlayer.setUpLines(audioUrls: List<String>) {
        this.addMediaItems(
            audioUrls.map { url -> MediaItem.fromUri(url) }
        )
        this.prepare()
    }
}
