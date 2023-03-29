package com.eryuksa.catchthelines.ui.record

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.eryuksa.catchthelines.R
import com.eryuksa.catchthelines.databinding.FragmentRecordBinding
import com.eryuksa.catchthelines.ui.common.setLayoutVerticalLimit
import com.eryuksa.catchthelines.ui.common.setSharedElementComebackTransition
import com.eryuksa.catchthelines.ui.common.setStatusBarIconColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null
    private val binding: FragmentRecordBinding
        get() = _binding!!
    private val viewModel: RecordViewModel by viewModels()

    private val contentsAdapter: CaughtContentsAdapter by lazy {
        CaughtContentsAdapter { content, sharedElements: Pair<View, String> ->
            findNavController().navigate(
                directions = RecordFragmentDirections.recordToDetail(
                    content.id,
                    content.lineAudioUrls.toTypedArray(),
                    content.posterUrl
                ),
                navigatorExtras = FragmentNavigatorExtras(sharedElements)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
        }

        requireActivity().window.run {
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
            setStatusBarIconColor(isDark = true)
            setLayoutVerticalLimit(hasLimit = true)
        }

        binding.rvCaughtContents.run {
            adapter = contentsAdapter
            addItemDecoration(caughtContentsSpaceDecorator)
            this@RecordFragment.setSharedElementComebackTransition(this)
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
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
