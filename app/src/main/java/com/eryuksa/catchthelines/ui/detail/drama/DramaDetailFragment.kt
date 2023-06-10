package com.eryuksa.catchthelines.ui.detail.drama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.eryuksa.catchthelines.databinding.FragmentDramaDetailBinding

class DramaDetailFragment : Fragment() {

    private var _binding: FragmentDramaDetailBinding? = null
    private val binding: FragmentDramaDetailBinding get() = _binding!!

    private val args: DramaDetailFragmentArgs by navArgs()
    private val detailUri: String by lazy { "$DETAIL_BASE_URI/${args.contentId}" }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDramaDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webView.run {
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    binding.progressBar.isVisible = false
                }
            }
            webChromeClient = WebChromeClient()

            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.supportZoom()
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.supportMultipleWindows()

            loadUrl(detailUri)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DETAIL_BASE_URI = "https://www.themoviedb.org/tv"
    }
}
