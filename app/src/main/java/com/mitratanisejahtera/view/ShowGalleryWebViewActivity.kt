package com.mitratanisejahtera.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.mitratanisejahtera.R
import com.mitratanisejahtera.model.Url
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

    webContent.loadUrl(url, mapOf("Authorization" to "Bearer ${user.token}"))
    println(url)
  }
}

