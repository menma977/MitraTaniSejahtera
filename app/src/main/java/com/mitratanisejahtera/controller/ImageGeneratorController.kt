package com.mitratanisejahtera.controller

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import java.net.URL

class ImageGeneratorController(private val url: String) : AsyncTask<Void, Void, Bitmap>() {
  override fun doInBackground(vararg params: Void?): Bitmap {
    return try {
      return if (url.isEmpty() || url == "null") {
        val urlImage = URL("https://mitratanisejahtera.com/img/mts_top.png")
        BitmapFactory.decodeStream(urlImage.openConnection().getInputStream())
      } else {
        val urlImage = URL(url)
        BitmapFactory.decodeStream(urlImage.openConnection().getInputStream())
      }
    } catch (e: Exception) {
      e.printStackTrace()
      val urlImage = URL("https://mitratanisejahtera.com/img/mts_top.png")
      BitmapFactory.decodeStream(urlImage.openConnection().getInputStream())
    }
  }
}