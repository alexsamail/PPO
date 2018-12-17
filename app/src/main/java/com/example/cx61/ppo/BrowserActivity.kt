package com.example.cx61.ppo

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BrowserActivity : AppCompatActivity() {
    lateinit var webView: WebView
    lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)
        val intent = getIntent()
        url = intent.getStringExtra("url")
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(getApplicationContext(), "URL not found", Toast.LENGTH_SHORT).show()
            finish()
        }
        webView = findViewById(R.id.webView)
        initWebView()
        webView.loadUrl(url)
    }

    private fun initWebView() {
        webView.setWebChromeClient(MyWebChromeClient(this))
        webView.clearCache(true)
        webView.getSettings().setJavaScriptEnabled(true)
        webView.setHorizontalScrollBarEnabled(false)
        webView.clearCache(true)
        webView.clearHistory()
        webView.getSettings().setJavaScriptEnabled(true)
        webView.setHorizontalScrollBarEnabled(false)
    }

    private class MyWebChromeClient(var context: Context) : WebChromeClient()
}

