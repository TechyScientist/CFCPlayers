package net.johnnyconsole.cfcplayers

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import net.johnnyconsole.cfcplayers.databinding.ActivityViewPlayerWebProfileBinding


class ViewPlayerWebProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewPlayerWebProfileBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityViewPlayerWebProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        with(binding) {
            enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(scrim = Color.TRANSPARENT))
            setContentView(root)

            ViewCompat.setOnApplyWindowInsetsListener(main) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
                insets
            }

            val profile = intent.getStringExtra("profile")!!
            title.text = getString(R.string.webProfileTitle, profile)

            val url = intent.getStringExtra("url")!!
            webView.settings.javaScriptEnabled = true
            binding.indicator.visibility = VISIBLE
            webView.loadUrl(url)
            binding.indicator.visibility = GONE

            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    binding.indicator.visibility = VISIBLE
                    view.loadUrl(request.url.toString())
                    binding.indicator.visibility = GONE
                    return true
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest,
                    error: WebResourceError
                ) {
                    Toast.makeText(
                        this@ViewPlayerWebProfileActivity,
                        error.description,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            onBackPressedDispatcher.addCallback(
                this@ViewPlayerWebProfileActivity,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (webView.canGoBack()) {
                            binding.indicator.visibility = VISIBLE
                            webView.goBack()
                            binding.indicator.visibility = GONE
                        } else {
                            finish()
                        }
                    }
                }
            )

            btBack.setOnClickListener { _ ->
                if (webView.canGoBack()) {
                    binding.indicator.visibility = VISIBLE
                    webView.goBack()
                    binding.indicator.visibility = GONE
                }
            }

            btForward.setOnClickListener { _ ->
                if (webView.canGoForward()) {
                    binding.indicator.visibility = VISIBLE
                    webView.goForward()
                    binding.indicator.visibility = GONE
                }
            }
        }
    }
}