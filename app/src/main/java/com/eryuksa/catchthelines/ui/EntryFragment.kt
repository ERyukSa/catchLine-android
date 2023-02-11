package com.eryuksa.catchthelines.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.eryuksa.catchline_android.R
import com.eryuksa.catchline_android.databinding.FragmentEntryBinding

class EntryFragment : Fragment() {

    private var _binding: FragmentEntryBinding? = null
    private val binding: FragmentEntryBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntryBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPlay.setOnClickListener {
            findNavController().navigate(R.id.entry_to_game)
        }
        binding.btnGuide.setOnClickListener {
            findNavController().navigate(R.id.entry_to_guide)
        }
        binding.btnRecord.setOnClickListener {
            findNavController().navigate(R.id.entry_to_record)
        }
        binding.btnFinish.setOnClickListener {
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
