package com.mitratanisejahtera.config

import android.app.Activity
import android.app.Dialog
import android.annotation.SuppressLint
import android.R.style.Theme_Translucent_NoTitleBar
import com.mitratanisejahtera.R

@SuppressLint("InflateParams")
class Loading(activity: Activity) {
  private val dialog = Dialog(activity, Theme_Translucent_NoTitleBar)

  init {
    val view = activity.layoutInflater.inflate(R.layout.loading_model, null)
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