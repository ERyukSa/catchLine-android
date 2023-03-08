package com.eryuksa.catchthelines.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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
import com.eryuksa.catchthelines.ui.common.AudioPlayer
import com.eryuksa.catchthelines.ui.common.removeOverScroll
import com.eryuksa.catchthelines.ui.game.uistate.CharacterCountHint
import com.eryuksa.catchthelines.ui.game.uistate.ClearerPosterHint
import com.eryuksa.catchthelines.ui.game.uistate.FirstCharacterHint
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
        val currentContent = viewModel.uiState1.value.contentUiStates[position].content
        findNavController().navigate(
            GameFragmentDirections.gameToDetail(
                currentContent.id,
                currentContent.lineAudioUrls.toTypedArray()
            )
        )
    }

    private lateinit var audioPlayer: AudioPlayer

    private lateinit var hintAnimationHandler: HintButtonAnimationHandler

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(HINT_IS_OPEN_KEY, hintAnimationHandler.isHintOpen)
    }

    override fun onStart() {
        super.onStart()
        initializeAudioPlayer()
    }

    override fun onStop() {
        super.onStop()
        releaseAudioPlayer()
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
    }

    private fun observeData() {
        with(viewModel) {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState1.distinctUntilChangedBy { it.currentPage }.collect { uiState ->
                        audioPlayer.moveTo(uiState.currentPage)
                    }
                }
            }

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState1.distinctUntilChangedBy { it.contentUiStates }.collect { uiState ->
                        posterAdapter.submitList(uiState.contentUiStates)
                        binding.viewPagerPoster.setCurrentItem(uiState.currentPage, false)
                    }
                }
            }

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState1.distinctUntilChangedBy { it.audioUris }.collect { uiState ->
                        audioPlayer.setUpAudio(uiState.audioUris)
                        audioPlayer.moveTo(uiState.currentPage)
                    }
                }
            }

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState1.distinctUntilChangedBy { it.feedbackText }.collect { uiState ->
                        binding.tvFeedback.text = uiState.feedbackText
                    }
                }
            }

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState1.distinctUntilChangedBy { it.didUserCatchTheLine }.collect { uiState ->
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

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState1.distinctUntilChangedBy { it.hintText }.collect { uiState ->
                        binding.tvHint.text = uiState.hintText
                    }
                }
            }

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState1.distinctUntilChangedBy { it.hintCount }.collect { uiState ->
                        binding.tvAvailableHintCount.text =
                            getString(R.string.game_available_hint_count, uiState.hintCount)
                    }
                }
            }

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState1.distinctUntilChangedBy { it.usedHints }.collect { uiState ->
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
        audioPlayer = AudioPlayer(ExoPlayer.Builder(requireContext()).build())
        audioPlayer.initializePlayer(
            playerView = binding.playerView,
            onPlay = { binding.btnPlayStop.setImageResource(R.drawable.icon_pause_24) },
            onPause = { binding.btnPlayStop.setImageResource(R.drawable.icon_play_24) }
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
    }
}
