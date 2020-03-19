package com.mitratanisejahtera.view.barcode

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.zxing.Result
import com.mitratanisejahtera.R
import com.mitratanisejahtera.config.Loading
import me.dm7.barcodescanner.zxing.ZXingScannerView

class QRActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
  private lateinit var loading: Loading
  private lateinit var scannerView: ZXingScannerView
  private lateinit var goTo: Intent

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_qr)

    loading = Loading(this)
    scannerView = ZXingScannerView(this)
    setContentView(scannerView)
  }

  override fun handleResult(rawResult: Result?) {
    if (rawResult != null) {
      loading.openDialog()
      goTo = Intent(applicationContext, ResultQrActivity::class.java)
      goTo.putExtra("qr", rawResult.text)
      finishAndRemoveTask()
      loading.closeDialog()
      startActivity(goTo)
    }
    scannerView.resumeCameraPreview(this)
  }

  override fun onResume() {
    super.onResume()
    scannerView.setResultHandler(this)
    scannerView.startCamera()
  }

  override fun onPause() {
    super.onPause()
    scannerView.stopCamera()
  }

  override fun onBackPressed() {
    super.onBackPressed()
    finishAndRemoveTask()
  }
}
