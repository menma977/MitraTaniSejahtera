package com.mitratanisejahtera.view

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mitratanisejahtera.R
import com.mitratanisejahtera.model.Url
import com.mitratanisejahtera.model.User

class LedgerWebViewActivity : AppCompatActivity() {
  private lateinit var webContent: WebView
  private lateinit var user: User
  private lateinit var url: String

  @SuppressLint("SetJavaScriptEnabled")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_ledger_web_view)

    user = User(this)
    url = "${Url.get()}/android/ledger"

    webContent = findViewById(R.id.webContent)
    webContent.removeAllViews()
    webContent.webViewClient = WebViewClient()
    webContent.webChromeClient = WebChromeClient()
    webContent.settings.javaScriptEnabled = true
    webContent.settings.domStorageEnabled = true
    webContent.settings.javaScriptCanOpenWindowsAutomatically = true

    webContent.loadUrl(url, mapOf("Authorization" to "Bearer ${user.token}"))
  }
}
