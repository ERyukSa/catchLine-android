package com.eryuksa.catchthelines.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eryuksa.catchline_android.R
import com.eryuksa.catchline_android.databinding.FragmentGameBinding
import com.eryuksa.catchthelines.common.ViewModelFactory

class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding
    private val viewModel: GameViewModel by viewModels { ViewModelFactory(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = this.viewModel

        binding.thumbnailImageGame.clipToOutline = true

        binding.playAudioGame.apply {
            setOnClickListener {
                viewModel.audioBtnClicked()
            }
        }

        binding.hintOrNextButtonGame.setOnClickListener {
            viewModel.hintOrNextBtnClicked()
        }

        binding.submitTitleGame.setOnClickListener {
            val isAnswer = viewModel.isAnswer(binding.inputTitleTextGame.text.toString())
            if (isAnswer) {
                binding.detailButtonGame.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "정답!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "땡!!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.detailButtonGame.setOnClickListener {
            findNavController().navigate(R.id.gameFragment_to_detailFragment)
        }
    }
}
