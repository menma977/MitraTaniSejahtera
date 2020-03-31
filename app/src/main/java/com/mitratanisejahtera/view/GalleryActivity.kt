package com.mitratanisejahtera.view

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mitratanisejahtera.R
import com.mitratanisejahtera.config.Loading
import com.mitratanisejahtera.controller.TreeController
import com.mitratanisejahtera.model.User
import java.util.*
import kotlin.concurrent.schedule

class GalleryActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var goTo: Intent
  private lateinit var contentLinearLayout: LinearLayout

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_gallery)

    user = User(this)
    loading = Loading(this)
    loading.openDialog()

    contentLinearLayout = findViewById(R.id.body)

    setView()
  }

  private fun setView() {
    Timer().schedule(100) {
      val response = TreeController.Gallery(user.token).execute().get()
      runOnUiThread {
        if (response["code"] == 200) {
          val mainLinear = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
          )
          mainLinear.setMargins(10, 10, 10, 10)

          val header = LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1F
          )
          header.setMargins(20, 20, 20, 20)

          val text = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
          )

          val lineDraft = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 2
          )

          val imageButtonTheme = LinearLayout.LayoutParams(
            100, 100
          )
          imageButtonTheme.setMargins(10, 10, 10, 10)

          for (i in 0 until response.getJSONArray("response").length()) {
            val linearLayout = LinearLayout(applicationContext)
            linearLayout.setBackgroundResource(R.drawable.card_secondary)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.gravity = Gravity.CENTER
            linearLayout.elevation = 5F
            linearLayout.layoutParams = mainLinear

            val linearLayoutHeader = LinearLayout(applicationContext)
            linearLayoutHeader.orientation = LinearLayout.VERTICAL
            linearLayoutHeader.layoutParams = header

            val textQR = TextView(applicationContext)
            textQR.setTextColor(ContextCompat.getColor(applicationContext, R.color.textPrimary))
            textQR.textSize = 18F
            textQR.layoutParams = text
            textQR.text = response.getJSONArray("response").getJSONObject(i)["code"].toString()
            linearLayoutHeader.addView(textQR)

            val line = View(applicationContext)
            line.layoutParams = lineDraft
            line.setBackgroundResource(R.color.colorAccent)
            linearLayoutHeader.addView(line)

            val status = TextView(applicationContext)
            status.setTextColor(ContextCompat.getColor(applicationContext, R.color.textPrimary))
            status.textSize = 12F
            status.layoutParams = text
            val parameterStatus: String =
              if (response.getJSONArray("response").getJSONObject(i)["yield"].toString().isEmpty()) {
                "Belum Panen"
              } else {
                "Sudah Panen - Hasil Panen:" + response.getJSONArray("response").getJSONObject(i)["yield"].toString()
              }
            status.text = parameterStatus
            linearLayoutHeader.addView(status)
            linearLayout.addView(linearLayoutHeader)

            val imageButton = ImageButton(applicationContext)
            imageButton.setBackgroundResource(R.drawable.button_default_1)
            imageButton.setColorFilter(R.color.colorAccent)
            imageButton.setImageResource(R.drawable.ic_navigate_next_black_24dp)
            imageButton.layoutParams = imageButtonTheme
            imageButton.setOnClickListener {
              goTo = Intent(applicationContext, ShowGalleryWebViewActivity::class.java)
              goTo.putExtra("id", response.getJSONArray("response").getJSONObject(i)["id"].toString())
              startActivity(goTo)
            }
            linearLayout.addView(imageButton)

            contentLinearLayout.addView(linearLayout)
          }
        } else {
          Toast.makeText(applicationContext, "Koneksi Tidak Setabil", Toast.LENGTH_SHORT).show()
        }
        loading.closeDialog()
      }
    }
  }
}
