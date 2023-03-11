package com.eryuksa.catchthelines.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.eryuksa.catchthelines.R
import com.eryuksa.catchthelines.databinding.FragmentGameBinding
import com.eryuksa.catchthelines.di.ContentViewModelFactory
import com.eryuksa.catchthelines.ui.common.removeOverScroll
import com.eryuksa.catchthelines.ui.common.setLayoutVerticalLimit
import com.eryuksa.catchthelines.ui.common.setStatusBarIconColor
import com.eryuksa.catchthelines.ui.game.uistate.AnotherLineHint
import com.eryuksa.catchthelines.ui.game.uistate.CharacterCountHint
import com.eryuksa.catchthelines.ui.game.uistate.ClearerPosterHint
import com.eryuksa.catchthelines.ui.game.uistate.FirstCharacterHint
import com.eryuksa.catchthelines.ui.game.utility.AudioPlayerHandler
import com.eryuksa.catchthelines.ui.game.utility.HintButtonAnimationHandler
import com.eryuksa.catchthelines.ui.game.utility.PosterDragHandlerImpl
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import kotlin.math.abs

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding!!
    private val viewModel: GameViewModel by viewModels { ContentViewModelFactory.getInstance() }

    private lateinit var posterDragHandler: PosterDragHandler
    private lateinit var posterAdapter: PosterViewPagerAdapter
    private val onClickPoster = { position: Int ->
        val currentContent = viewModel.uiState.value.contentUiStates[position].content
        findNavController().navigate(
            GameFragmentDirections.gameToDetail(
                currentContent.id,
                currentContent.lineAudioUrls.toTypedArray()
            )
        )
    }

    private lateinit var audioPlayer: AudioPlayerHandler
    private var playbackPosition = 0L

    private lateinit var hintAnimationHandler: HintButtonAnimationHandler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        outState: Bundle?
    ): View {
        requireActivity().window.run {
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
            setStatusBarIconColor(isDark = true)
            setLayoutVerticalLimit(hasLimit = true)
        }

        _binding = FragmentGameBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@GameFragment.viewModel
            clearerPosterHint = ClearerPosterHint
            firstCharacterHint = FirstCharacterHint
            characterCountHint = CharacterCountHint
            anotherLineHint = AnotherLineHint
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        restoreUiOnlyState(outState)
        initPosterViewPager()
        initViewListener()
        initHintButtonsAnimation(isHintOpen = outState?.getBoolean(HINT_IS_OPEN_KEY) ?: false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(HINT_IS_OPEN_KEY, hintAnimationHandler.isHintOpen)
        outState.putLong(AUDIO_PLAY_POSITION_KEY, audioPlayer.playbackPosition)
    }

    override fun onStart() {
        super.onStart()
        initializeAudioPlayer()
    }

    override fun onStop() {
        super.onStop()
        releaseAudioPlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun restoreUiOnlyState(outState: Bundle?) {
        playbackPosition = outState?.getLong(AUDIO_PLAY_POSITION_KEY) ?: 0
    }

    private fun initHintButtonsAnimation(isHintOpen: Boolean) {
        hintAnimationHandler = HintButtonAnimationHandler(
            hintEntranceButton = binding.btnOpenHint,
            hintButtons = listOf(
                binding.btnHintClearerPoster,
                binding.btnHintFirstCharacter,
                binding.btnHintCharactersCount,
                binding.btnHintAnotherLine
            ),
            wasHintOpened = isHintOpen,
            darkBackgroundView = binding.darkBackgroundCoverForHint
        )
    }

    private fun initViewListener() {
        binding.btnSubmitTitle.setOnClickListener {
            submitUserInputAndClearText()
            hideInputMethod()
        }

        binding.btnPlayStop.setOnClickListener {
            audioPlayer.switchPlayingState()
        }

        binding.edittextInputTitle.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submitUserInputAndClearText()
            }
            true
        }

        binding.btnHintAnotherLine.setOnClickListener {
            if (viewModel.uiState.value.usedHints.contains(AnotherLineHint).not()) {
                viewModel.useHint(AnotherLineHint)
            }
            hintAnimationHandler.closeHintAndDarkBackground()
        }
        binding.btnHintClearerPoster.setOnClickListener {
            viewModel.useHint(ClearerPosterHint)
            hintAnimationHandler.closeHintAndDarkBackground()
        }
        binding.btnHintFirstCharacter.setOnClickListener {
            viewModel.useHint(FirstCharacterHint)
            hintAnimationHandler.closeHintAndDarkBackground()
        }
        binding.btnHintCharactersCount.setOnClickListener {
            viewModel.useHint(CharacterCountHint)
            hintAnimationHandler.closeHintAndDarkBackground()
        }

        binding.btnSwitchLine.setOnClickListener {
            viewModel.switchLineOfCurrentContent()
        }
    }

    private fun observeData() {
        with(viewModel) {
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState.distinctUntilChangedBy { it.contentUiStates }.collect { uiState ->
                        posterAdapter.submitList(uiState.contentUiStates)
                        binding.viewPagerPoster.setCurrentItem(uiState.currentPage, false)
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState.distinctUntilChangedBy { it.audioUris }.collect { uiState ->
                        audioPlayer.setUpAudio(uiState.audioUris)
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState.distinctUntilChangedBy { it.audioIndex }.collect { uiState ->
                        audioPlayer.moveTo(uiState.audioIndex)
                        val lineOrderOfCurrentContent = uiState.audioIndex % 2 + 1
                        binding.btnSwitchLine.text = getString(
                            R.string.game_line_label,
                            lineOrderOfCurrentContent
                        )
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState.distinctUntilChangedBy { it.feedbackText }.collect { uiState ->
                        binding.tvFeedback.text = uiState.feedbackText
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState.distinctUntilChangedBy { it.didUserCatchTheLine }.collect { uiState ->
                        binding.btnOpenHint.isClickable = uiState.didUserCatchTheLine.not()
                        binding.btnSubmitTitle.isEnabled = uiState.didUserCatchTheLine.not()
                        binding.viewPagerPoster.isUserInputEnabled = uiState.didUserCatchTheLine.not()
                        binding.viewPagerPoster.elevation = when (uiState.didUserCatchTheLine) {
                            true -> resources.getDimension(R.dimen.game_poster_elevation_over_dark_cover)
                            false -> 0f
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState.distinctUntilChangedBy { it.hintText }.collect { uiState ->
                        binding.tvHint.text = uiState.hintText
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState.distinctUntilChangedBy { it.hintCount }.collect { uiState ->
                        binding.tvAvailableHintCount.text =
                            getString(R.string.game_available_hint_count, uiState.hintCount)
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState.distinctUntilChangedBy { it.usedHints }.collect { uiState ->
                        binding.usedHints = uiState.usedHints
                    }
                }
            }
        }
    }

    private fun ViewPager2.setHorizontalPadding(padding: Int) {
        this.setPadding(padding, 0, padding, 0)
    }

    private val switchAudioLineOnPageChange = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            viewModel.movePageTo(position)
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

    private fun initPosterViewPager() {
        binding.viewPagerPoster.run {
            offscreenPageLimit = 3
            posterDragHandler = PosterDragHandlerImpl(
                binding,
                removeCaughtContent = viewModel::removeCaughtContent
            )
            adapter = PosterViewPagerAdapter(posterDragHandler, onClickPoster).also {
                this@GameFragment.posterAdapter = it
            }
            this.removeOverScroll()
            this.setHorizontalPadding((resources.displayMetrics.widthPixels * 0.15).toInt())
            setPageTransformer(buildPageTransformer())
            registerOnPageChangeCallback(switchAudioLineOnPageChange)
            scheduleLayoutAnimation()
        }
    }

    private fun initializeAudioPlayer() {
        ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
            binding.playerView.player = exoPlayer
            audioPlayer = AudioPlayerHandler(exoPlayer)
        }
        audioPlayer.initializePlayer(
            playbackPosition = playbackPosition,
            onPlay = { binding.btnPlayStop.setImageResource(R.drawable.icon_pause_24) },
            onPause = {
                binding.btnPlayStop.setImageResource(R.drawable.icon_play_24)
                playbackPosition = audioPlayer.playbackPosition
            }
        )
    }

    private fun releaseAudioPlayer() {
        audioPlayer.releasePlayer()
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

    companion object {
        private const val HINT_IS_OPEN_KEY = "HINT_IS_OPEN_KEY"
        private const val AUDIO_PLAY_POSITION_KEY = "AUDIO_PLAY_POSITION_KEY"
    }
}
