package com.eryuksa.catchthelines.ui.game

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
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
import com.eryuksa.catchthelines.ui.game.utility.AudioPlayerHelper
import com.eryuksa.catchthelines.ui.game.utility.HintButtonHelper
import com.eryuksa.catchthelines.ui.game.utility.PosterDragListenerImpl
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    private lateinit var posterDragListener: PosterDragListener
    private lateinit var posterAdapter: PosterViewPagerAdapter
    private val onClickPoster = { position: Int, sharedElements: Pair<ImageView, String> ->
        findNavController().navigate(
            directions = GameFragmentDirections.gameToDetail(
                viewModel.uiState.value.contentItems[position].id,
                viewModel.uiState.value.contentItems[position].audioUrls.toTypedArray()
            ),
            navigatorExtras = FragmentNavigatorExtras(sharedElements)
        )
    }

    private val audioPlayerHelper: AudioPlayerHelper by lazy {
        AudioPlayerHelper(requireContext())
    }

    private val gameStateResetDialog: Dialog by lazy {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.game_reset_game_state_message_title)
            .setMessage(R.string.game_reset_game_state_message_body)
            .setPositiveButton(R.string.game_reset_game_state_message_positive) { _, _ ->
                viewModel.forceToMovePageTo(binding.viewPagerPoster.currentItem)
                gameStateResetDialog.dismiss()
            }
            .setNegativeButton(R.string.game_reset_game_state_message_negative) { _, _ ->
                binding.viewPagerPoster.currentItem = viewModel.uiState.value.currentPage
                gameStateResetDialog.dismiss()
            }
            .setCancelable(false)
            .create()
    }

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
        }

        initPosterViewPager()
        setUpListeners()
        setUpHintButtonsAnimator()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    override fun onStart() {
        super.onStart()
        audioPlayerHelper.initialize()
        binding.playerView.player = audioPlayerHelper.audioPlayer
    }

    override fun onStop() {
        super.onStop()
        audioPlayerHelper.release()
        binding.playerView.player = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initPosterViewPager() {
        binding.viewPagerPoster.run {
            offscreenPageLimit = 2
            posterDragListener = PosterDragListenerImpl(
                binding,
                removeCaughtContent = viewModel::removeCaughtContent
            )
            adapter = PosterViewPagerAdapter(posterDragListener, onClickPoster).also {
                this@GameFragment.posterAdapter = it
            }
            this.removeOverScroll()
            setPageTransformer(buildPageTransformer())
            registerOnPageChangeCallback(onPageChange)
        }
    }

    private fun setUpHintButtonsAnimator() {
        binding.btnOpenHint.doOnLayout {
            binding.hintOpenAnimatorList = HintButtonHelper.createOpenAnimators(
                binding.btnOpenHint,
                binding.btnClearerPosterHint,
                binding.btnFirstCharacterHint,
                binding.btnCharactersCountHint
            )
        }

        binding.btnOpenHint.setOnClickListener {
            viewModel.changeHintOpenState()
        }
    }

    private fun setUpListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSubmitUserGuess.setOnClickListener {
            viewModel.checkUserCatchTheLine()
        }

        binding.edittextInputTitleGuess.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.checkUserCatchTheLine()
            }
            true
        }

        binding.btnClearerPosterHint.setOnClickListener {
            viewModel.useHint(Hint.CLEARER_POSTER)
        }
        binding.btnFirstCharacterHint.setOnClickListener {
            viewModel.useHint(Hint.FIRST_CHARACTER)
        }
        binding.btnCharactersCountHint.setOnClickListener {
            viewModel.useHint(Hint.CHARACTER_COUNT)
        }
        binding.chipLine1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changeSelectedLine(0)
            }
        }
        binding.chipLine2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changeSelectedLine(1)
            }
        }

        binding.darkBackgroundCover.setOnClickListener {
            viewModel.changeHintOpenState()
        }
    }

    private fun observeData() {
        with(viewModel) {
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState.distinctUntilChangedBy { it.contentItems }.collect { uiState ->
                        posterAdapter.submitList(uiState.contentItems)
                        binding.viewPagerPoster.setCurrentItem(uiState.currentPage, false)

                        audioPlayerHelper.setAudioItems(uiState.contentItems.map { it.audioUrls })
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState.distinctUntilChangedBy { it.audioIndex }.collect { uiState ->
                        audioPlayerHelper.moveTo(uiState.audioIndex)
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    hideKeyboard.collect {
                        hideInputMethod()
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    showGameStateResetMessage.collect {
                        gameStateResetDialog.show()
                    }
                }
            }
        }
    }

    private val onPageChange = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            binding.playerView.visibility = when (state) {
                ViewPager2.SCROLL_STATE_IDLE -> View.VISIBLE
                else -> View.GONE
            }
        }

        override fun onPageSelected(position: Int) {
            viewModel.tryToMovePageTo(position)
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

    private fun hideInputMethod() {
        val inputManager =
            getSystemService(requireContext(), InputMethodManager::class.java) as InputMethodManager
        inputManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}
