package com.eryuksa.catchthelines.ui.record

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.eryuksa.catchthelines.R
import com.eryuksa.catchthelines.databinding.FragmentRecordBinding
import com.eryuksa.catchthelines.di.ContentViewModelFactory
import com.eryuksa.catchthelines.ui.common.setLayoutVerticalLimit
import com.eryuksa.catchthelines.ui.common.setStatusBarIconColor
import kotlinx.coroutines.launch

class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null
    private val binding: FragmentRecordBinding
        get() = _binding!!
    private val viewModel: RecordViewModel by viewModels { ContentViewModelFactory.getInstance() }

    private val contentsAdapter: CaughtContentsAdapter by lazy {
        CaughtContentsAdapter { content ->
            findNavController().navigate(
                RecordFragmentDirections.recordToDetail(content.id, content.lineAudioUrls.toTypedArray())
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordBinding.inflate(inflater, container, false)

        requireActivity().window.run {
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
            setStatusBarIconColor(isDark = true)
            setLayoutVerticalLimit(hasLimit = true)
        }

        binding.rvCaughtContents.apply {
            adapter = contentsAdapter
            addItemDecoration(caughtContentsSpaceDecorator)
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    with(uiState) {
                        binding.tvCaughtCount.text = getString(
                            R.string.record_catch_count,
                            caughtContentsCount
                        )
                        binding.tvTryCount.text = getString(
                            R.string.record_encounter_count,
                            encounteredContentsCount
                        )
                        binding.tvStatistics.text = getString(
                            R.string.record_statistics,
                            caughtContentsCount,
                            encounteredContentsCount
                        )
                        contentsAdapter.contents = caughtContents
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val caughtContentsSpaceDecorator = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.right = resources.getDimensionPixelOffset(R.dimen.record_caught_content_item_space)
        }
    }
}
