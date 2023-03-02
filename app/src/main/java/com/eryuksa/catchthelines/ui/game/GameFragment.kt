package com.eryuksa.catchthelines.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.eryuksa.catchthelines.R
import com.eryuksa.catchthelines.databinding.FragmentGameBinding
import com.eryuksa.catchthelines.di.ContentViewModelFactory
import com.eryuksa.catchthelines.ui.common.removeOverScroll
import com.eryuksa.catchthelines.ui.game.uistate.AllKilled
import com.eryuksa.catchthelines.ui.game.uistate.CharacterCountHint
import com.eryuksa.catchthelines.ui.game.uistate.ClearerPosterHint
import com.eryuksa.catchthelines.ui.game.uistate.FirstCharacterHint
import com.eryuksa.catchthelines.ui.game.uistate.NoInput
import com.eryuksa.catchthelines.ui.game.uistate.UserCaughtTheLine
import com.eryuksa.catchthelines.ui.game.uistate.UserInputWrong
import com.eryuksa.catchthelines.ui.game.utility.ButtonOpenHandler
import com.eryuksa.catchthelines.ui.game.utility.PosterEventHandler
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlin.math.abs

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding!!
    private val viewModel: GameViewModel by viewModels { ContentViewModelFactory.getInstance() }

    private lateinit var posterEventHandler: PosterEventHandler
    private lateinit var posterAdapter: PosterViewPagerAdapter

    private val audioPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(requireContext()).build().apply { pauseAtEndOfMediaItems = true }
    }

    private lateinit var hintButtonsOpener: ButtonOpenHandler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        outState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@GameFragment.viewModel
            clearerPosterHint = ClearerPosterHint
            firstCharacterHint = FirstCharacterHint
            characterCountHint = CharacterCountHint
            playerViewLine.player = audioPlayer
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
        initPosterViewPager()
        initViewListener()
        initHintButtonsAnimation(outState?.getBoolean(HINT_IS_OPEN_KEY) ?: false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    private fun initPosterViewPager() {
        binding.viewPagerPoster.run {
            offscreenPageLimit = 3
            posterEventHandler = PosterEventHandler(
                this@GameFragment,
                binding,
                removeCaughtContent = viewModel::removeCaughtContent
            )
            adapter = PosterViewPagerAdapter(posterEventHandler).also {
                this@GameFragment.posterAdapter = it
            }
            this.removeOverScroll()
            this.setHorizontalPadding((resources.displayMetrics.widthPixels * 0.15).toInt())
            setPageTransformer(buildPageTransformer())
            registerOnPageChangeCallback(switchAudioLineOnPageChange)
            scheduleLayoutAnimation()
        }
    }

    private fun initHintButtonsAnimation(isHintOpen: Boolean) {
        binding.root.doOnLayout {
            hintButtonsOpener = ButtonOpenHandler(
                initialCeilHeight = binding.btnOpenHint.height,
                margin = 20,
                duration = 400,
                isHintOpen
            )
            hintButtonsOpener.run {
                addInnerButton(binding.btnHintClearerPoster)
                addInnerButton(binding.btnHintFirstCharacter)
                addInnerButton(binding.btnHintCharactersCount)
                addInnerButton(binding.btnHintAnotherLine)
            }
        }
    }

    private fun initViewListener() {
        binding.btnSubmitTitle.setOnClickListener {
            submitUserInputAndClearText()
            hideInputMethod()
        }

        binding.btnPlayStop.setOnClickListener {
            when (audioPlayer.isPlaying) {
                true -> stopLineAudio()
                false -> startLineAudio()
            }
        }

        binding.btnOpenHint.setOnClickListener {
            hintButtonsOpener.switchOpenState()
            binding.darkBackgroundCoverForHint.isVisible =
                binding.darkBackgroundCoverForHint.isVisible.not()
        }

        binding.edittextInputTitle.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submitUserInputAndClearText()
            }
            true
        }
    }

    private fun observeData() {
        with(viewModel) {
            uiStates.observe(viewLifecycleOwner) { uiStates ->
                posterAdapter.submitList(uiStates)
            }

            groupedLineAudioUrls.observe(viewLifecycleOwner) { audioUrls ->
                audioPlayer.setUpLines(audioUrls.map { urls -> urls[0] })
            }

            feedbackUiState.observe(viewLifecycleOwner) { feedbackUiState ->
                binding.tvFeedback.text = when (feedbackUiState) {
                    is UserCaughtTheLine ->
                        getString(R.string.game_feedback_catch_the_line, feedbackUiState.title)
                    is UserInputWrong ->
                        getString(R.string.game_feedback_wrong, feedbackUiState.userInput)
                    is NoInput -> ""
                    is AllKilled -> getString(R.string.game_feedback_all_killed)
                }

                feedbackUiState.gameCanContinue.also { userInputEnabled ->
                    binding.btnOpenHint.isClickable = userInputEnabled
                    binding.btnSubmitTitle.isEnabled = userInputEnabled
                    binding.viewPagerPoster.isUserInputEnabled = userInputEnabled
                    binding.viewPagerPoster.elevation = when (userInputEnabled) {
                        true -> 0f
                        false -> resources.getDimension(R.dimen.game_poster_elevation_over_dark_cover)
                    }
                }
            }

            currentPagePosition.observe(viewLifecycleOwner) { position ->
                stopLineAudio()
                audioPlayer.seekTo(position, 0)
            }

            hintText.observe(viewLifecycleOwner) { text ->
                binding.tvHint.text = text
            }

            usedHints.observe(viewLifecycleOwner) {
                if (this@GameFragment::hintButtonsOpener.isInitialized.not()) return@observe
                hintButtonsOpener.switchOpenState()
                binding.darkBackgroundCoverForHint.visibility = View.INVISIBLE
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

    private fun submitUserInputAndClearText() {
        viewModel.checkUserCatchTheLine(binding.edittextInputTitle.text.toString())
        binding.edittextInputTitle.text?.clear()
    }

    private fun hideInputMethod() {
        val inputManager =
            getSystemService(requireContext(), InputMethodManager::class.java) as InputMethodManager
        inputManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(HINT_IS_OPEN_KEY, hintButtonsOpener.isOpen)
    }

    companion object {
        private const val HINT_IS_OPEN_KEY = "HINT_IS_OPEN_KEY"
    }
}
