package net.johnnyconsole.cfcplayers

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.johnnyconsole.cfcplayers.databinding.ActivityViewPlayerWebProfileBinding


class ViewPlayerWebProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewPlayerWebProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityViewPlayerWebProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val profile = intent.getStringExtra("profile")!!
        binding.title.text = getString(R.string.webProfileTitle, profile)

        val url = intent.getStringExtra("url")!!
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadUrl(url)


        binding.webView.webViewClient = object: WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean{
                view.loadUrl(url)
                return true
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                Toast.makeText(this@ViewPlayerWebProfileActivity, description, Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onBackPressed() {
        if(binding.webView.canGoBack()) {
            binding.webView.goBack()
        }
        else {
            finish()
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