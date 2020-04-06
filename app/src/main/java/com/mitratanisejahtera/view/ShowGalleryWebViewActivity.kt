package com.mitratanisejahtera.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.mitratanisejahtera.R
import com.mitratanisejahtera.model.Url
import com.mitratanisejahtera.model.Url.webView
import com.mitratanisejahtera.model.User

class ShowGalleryWebViewActivity : AppCompatActivity() {
  private lateinit var webContent: WebView
  private lateinit var user: User
  private lateinit var url: String

  @SuppressLint("SetJavaScriptEnabled")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_show_gallery_web_view)

    user = User(this)
    val treeId = intent.getSerializableExtra("id").toString()
    url = "${Url.get()}/android/gallery/$treeId"

    webContent = findViewById(R.id.webContent)
    webContent.removeAllViews()
    webContent.webViewClient = WebViewClient()
    webContent.webChromeClient = WebChromeClient()
    webContent.settings.javaScriptEnabled = true
    webContent.settings.domStorageEnabled = true
    webContent.settings.javaScriptCanOpenWindowsAutomatically = true
    webContent.webViewClient = object : WebViewClient() {
      override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        val urlString = request.url.toString()
        if (urlString.contains("/certificate/")) {
          val intent = Intent(Intent.ACTION_VIEW, request.url)
          view.context.startActivity(intent)
        }
        return true
      }
    }

    webContent.loadUrl(url, mapOf("Authorization" to "Bearer ${user.token}"))
  }
}

