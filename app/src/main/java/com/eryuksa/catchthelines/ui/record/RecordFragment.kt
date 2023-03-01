package com.eryuksa.catchthelines.ui.record

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.eryuksa.catchthelines.R
import com.eryuksa.catchthelines.databinding.FragmentRecordBinding
import com.eryuksa.catchthelines.di.ContentViewModelFactory

class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null
    private val binding: FragmentRecordBinding
        get() = _binding!!
    private val viewModel: RecordViewModel by viewModels { ContentViewModelFactory.getInstance() }

    private val contentsAdapter: CaughtContentsAdapter by lazy {
        CaughtContentsAdapter { contentId ->
            findNavController().navigate(
                RecordFragmentDirections.recordToDetail(contentId)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordBinding.inflate(inflater, container, false)

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
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            with(uiState) {
                binding.tvCaughtCount.text = getString(R.string.record_catch_count, caughtContentsCount)
                binding.tvTryCount.text = getString(R.string.record_try_count, tryContentsCount)
                binding.tvStatistics.text =
                    getString(R.string.record_statistics, caughtContentsCount, tryContentsCount)
                contentsAdapter.contents = caughtContents
            }
        }
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
