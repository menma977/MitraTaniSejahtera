package com.mitratanisejahtera.fragment

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mitratanisejahtera.R
import com.mitratanisejahtera.config.FragmentLoading
import com.mitratanisejahtera.controller.TreeController
import com.mitratanisejahtera.model.User
import com.mitratanisejahtera.view.ShowGalleryWebViewActivity
import java.util.*
import kotlin.concurrent.schedule

class GalleryFragment : Fragment() {
  private lateinit var user: User
  private lateinit var loadingFragment: FragmentLoading
  private lateinit var goTo: Intent
  private lateinit var contentLinearLayout: LinearLayout

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_gallery, container, false)
    user = User(context)
    loadingFragment = FragmentLoading(this.requireActivity())
    loadingFragment.openDialog()

    contentLinearLayout = view.findViewById(R.id.body)
    contentLinearLayout.removeAllViews()

    setView(view)
    return view
  }

  private fun setView(view: View) {
    Timer().schedule(100) {
      val response = TreeController.Gallery(user.token).execute().get()
      activity?.runOnUiThread {
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
            val linearLayout = LinearLayout(view.context)
            linearLayout.setBackgroundResource(R.drawable.card_secondary)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.gravity = Gravity.CENTER
            linearLayout.elevation = 5F
            linearLayout.layoutParams = mainLinear

            val linearLayoutHeader = LinearLayout(view.context)
            linearLayoutHeader.orientation = LinearLayout.VERTICAL
            linearLayoutHeader.layoutParams = header

            val textQR = TextView(view.context)
            textQR.setTextColor(ContextCompat.getColor(view.context, R.color.textPrimary))
            textQR.textSize = 18F
            textQR.layoutParams = text
            textQR.text = response.getJSONArray("response").getJSONObject(i)["code"].toString()
            linearLayoutHeader.addView(textQR)

            val line = View(view.context)
            line.layoutParams = lineDraft
            line.setBackgroundResource(R.color.colorAccent)
            linearLayoutHeader.addView(line)

            val status = TextView(view.context)
            status.setTextColor(ContextCompat.getColor(view.context, R.color.textPrimary))
            status.textSize = 12F
            status.layoutParams = text
            val parameterStatus: String =
              if (response.getJSONArray("response").getJSONObject(i)["yield"].toString() == "null") {
                "Belum Panen"
              } else {
                "Sudah Panen - Hasil Panen:" + response.getJSONArray("response").getJSONObject(i)["yield"].toString()
              }
            status.text = parameterStatus
            linearLayoutHeader.addView(status)
            linearLayout.addView(linearLayoutHeader)

            val imageButton = ImageButton(view.context)
            imageButton.setBackgroundResource(R.drawable.button_default_1)
            imageButton.setColorFilter(R.color.colorAccent)
            imageButton.setImageResource(R.drawable.ic_navigate_next_black_24dp)
            imageButton.layoutParams = imageButtonTheme
            imageButton.setOnClickListener {
              goTo = Intent(view.context, ShowGalleryWebViewActivity::class.java)
              goTo.putExtra("id", response.getJSONArray("response").getJSONObject(i)["id"].toString())
              startActivity(goTo)
            }
            linearLayout.addView(imageButton)

            contentLinearLayout.addView(linearLayout)
          }
        } else {
          Toast.makeText(view.context, "Koneksi Tidak Setabil", Toast.LENGTH_SHORT).show()
        }
        loadingFragment.closeDialog()
      }
    }
  }
}
