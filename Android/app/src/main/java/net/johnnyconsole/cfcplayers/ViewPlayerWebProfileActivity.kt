package net.johnnyconsole.cfcplayers

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import net.johnnyconsole.cfcplayers.databinding.ActivityViewPlayerWebProfileBinding


class ViewPlayerWebProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewPlayerWebProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityViewPlayerWebProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        with(binding) {
            enableEdgeToEdge()
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
            webView.loadUrl(url)

            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)
                    return true
                }

                override fun onReceivedError(
                    view: WebView?,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String?
                ) {
                    Toast.makeText(
                        this@ViewPlayerWebProfileActivity,
                        description,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            onBackPressedDispatcher.addCallback(this@ViewPlayerWebProfileActivity,
                object: OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if(webView.canGoBack()) {
                            webView.goBack()
                        }
                        else {
                            finish()
                        }
                    }
                }
            )
        }
    }

    fun onBackClicked(view: View) {
        if(binding.webView.canGoBack()) {
            binding.webView.goBack()
        }
    }

    fun onForwardClicked(view: View) {
        if(binding.webView.canGoForward()) {
            binding.webView.goForward()
        }
    }
}