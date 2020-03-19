package com.mitratanisejahtera.config

import android.app.Dialog
import androidx.fragment.app.FragmentActivity
import android.R.style.Theme_Translucent_NoTitleBar
import android.annotation.SuppressLint
import com.mitratanisejahtera.R

@SuppressLint("InflateParams")
class FragmentLoading(fragmentActivity: FragmentActivity) {
  private val dialog = Dialog(fragmentActivity, Theme_Translucent_NoTitleBar)

  init {
    val view = fragmentActivity.layoutInflater.inflate(R.layout.loading_model, null)
    dialog.setContentView(view)
    dialog.setCancelable(false)
  }

  fun openDialog() {
    dialog.show()
  }

  fun closeDialog() {
    dialog.dismiss()
  }
}