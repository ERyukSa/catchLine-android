package com.eryuksa.catchthelines.ui.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.eryuksa.catchline_android.databinding.FragmentGameBinding

class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding
    private val viewModel: GameViewModel by viewModels()

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
        observeData()
        binding.btnSubmitTitle.setOnClickListener {
            viewModel.checkIsUserTitleMatched()
        }
    }

    private fun observeData() {
        with(viewModel) {
            gameItems.observe(viewLifecycleOwner) { gameItems ->
                Log.d("GameFragment", "gameItems: $gameItems")
            }

            hintText.observe(viewLifecycleOwner) { hintText ->
                binding.tvHint.text = hintText
            }

            feedbackText.observe(viewLifecycleOwner) { feedbackText ->
                binding.tvFeedback.text = feedbackText
            }

            availableHintCount.observe(viewLifecycleOwner) { hintCount ->
                binding.tvAvailableHintCount.text = hintCount.toString()
            }

            isLinePlaying.observe(viewLifecycleOwner) { linePlaying ->
                if (linePlaying) {
                } else {
                }
            }
        }
    }
}
