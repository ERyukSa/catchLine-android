package com.eryuksa.catchline_android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.eryuksa.catchline_android.R

class StartFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(requireActivity()) {
            findViewById<Button>(R.id.play_button_main).setOnClickListener {
                findNavController().navigate(R.id.startFragment_to_gameFragment)
            }
            findViewById<Button>(R.id.record_button_main).setOnClickListener {
                findNavController().navigate(R.id.startFragment_to_recordFragment)
            }
            findViewById<Button>(R.id.about_button_main).setOnClickListener {
                findNavController().navigate(R.id.startFragment_to_aboutFragment)
            }
        }
    }
}