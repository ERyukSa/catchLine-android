package com.eryuksa.catchthelines.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.doOnLayout
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
import com.eryuksa.catchthelines.ui.common.removeOverScroll
import com.eryuksa.catchthelines.ui.common.setLayoutVerticalLimit
import com.eryuksa.catchthelines.ui.common.setStatusBarIconColor
import com.eryuksa.catchthelines.ui.game.uistate.Hint
import com.eryuksa.catchthelines.ui.game.utility.AudioPlayerHandler
import com.eryuksa.catchthelines.ui.game.utility.HintButtonOpenHelper
import com.eryuksa.catchthelines.ui.game.utility.PosterDragHandlerImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding!!
    private val viewModel: GameViewModel by viewModels()

    private lateinit var posterDragHandler: PosterDragHandler
    private lateinit var posterAdapter: PosterViewPagerAdapter
    private val onClickPoster = { position: Int ->
        findNavController().navigate(
            GameFragmentDirections.gameToDetail(
                viewModel.uiState.value.contentUiStates[position].id,
                viewModel.uiState.value.groupedAudioUrls[position].toTypedArray()
            )
        )
    }

    private lateinit var audioPlayerHandler: AudioPlayerHandler

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
            clearerPosterHint = Hint.ClearerPoster
            firstCharacterHint = Hint.FirstCharacter
            characterCountHint = Hint.CharacterCount
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        initPosterViewPager()
        initViewListener()
        setUpHintButtonsAnimator()
        initializeAudioPlayerHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initPosterViewPager() {
        binding.viewPagerPoster.run {
            offscreenPageLimit = 2
            posterDragHandler = PosterDragHandlerImpl(
                binding,
                removeCaughtContent = viewModel::removeCaughtContent
            )
            adapter = PosterViewPagerAdapter(posterDragHandler, onClickPoster).also {
                this@GameFragment.posterAdapter = it
            }
            this.removeOverScroll()
            setPageTransformer(buildPageTransformer())
            registerOnPageChangeCallback(onPageChange)
        }
    }

    private fun setUpHintButtonsAnimator() {
        val hintButtonElevation = resources.getDimension(R.dimen.game_hintbutton_elevation)

        binding.btnOpenHint.doOnLayout {
            binding.hintOpenAnimatorList = HintButtonOpenHelper.createOpenAnimators(
                binding.btnOpenHint,
                binding.btnClearerPosterHint.apply { elevation = hintButtonElevation },
                binding.btnFirstCharacterHint.apply { elevation = hintButtonElevation },
                binding.btnCharactersCountHint.apply { elevation = hintButtonElevation }
            )
        }

        binding.btnOpenHint.setOnClickListener {
            viewModel.changeHintOpenState()
        }
    }

    private fun initializeAudioPlayerHandler() {
        AudioPlayerHandler(requireContext()).run {
            audioPlayerHandler = this
            binding.playerView.player = this.audioPlayer
        }
    }

    private fun initViewListener() {
        binding.btnSubmitTitle.setOnClickListener {
            submitUserInputAndClearText()
            hideInputMethod()
        }

        binding.edittextInputTitle.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submitUserInputAndClearText()
            }
            true
        }

        binding.btnClearerPosterHint.setOnClickListener {
            viewModel.useHint(Hint.ClearerPoster)
            viewModel.changeHintOpenState()
        }
        binding.btnFirstCharacterHint.setOnClickListener {
            viewModel.useHint(Hint.FirstCharacter)
            viewModel.changeHintOpenState()
        }
        binding.btnCharactersCountHint.setOnClickListener {
            viewModel.useHint(Hint.CharacterCount)
            viewModel.changeHintOpenState()
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
                    uiState.distinctUntilChangedBy { it.groupedAudioUrls }.collect { uiState ->
                        audioPlayerHandler.setAudioItems(uiState.groupedAudioUrls)
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState.distinctUntilChangedBy { it.audioIndex }.collect { uiState ->
                        audioPlayerHandler.moveTo(uiState.audioIndex)
                        val lineNumber = uiState.audioIndex % 2 + 1
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState.distinctUntilChangedBy { it.feedbackText }.collect { uiState ->
                        // binding.tvFeedback.text = uiState.feedbackText
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState.distinctUntilChangedBy { it.didUserCatchTheLine }.collect { uiState ->
                        binding.btnOpenHint.isClickable = uiState.didUserCatchTheLine.not()
                        binding.btnSubmitTitle.isEnabled = uiState.didUserCatchTheLine.not()
                        binding.viewPagerPoster.isUserInputEnabled =
                            uiState.didUserCatchTheLine.not()
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
                        // binding.tvHint.text = uiState.hintText
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
        }
    }

    private val onPageChange = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            viewModel.movePageTo(position)
        }
    }

    private fun buildPageTransformer() = CompositePageTransformer().also {
        it.addTransformer(
            MarginPageTransformer(resources.getDimensionPixelOffset(R.dimen.game_poster_margin))
        )
        it.addTransformer { eachPageView: View, positionFromCenter: Float ->
            val scale = 1 - abs(positionFromCenter)
            eachPageView.scaleY = 0.85f + 0.15f * scale
        }
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
}
