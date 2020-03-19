package com.mitratanisejahtera.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import com.mitratanisejahtera.R
import com.mitratanisejahtera.config.Loading
import com.mitratanisejahtera.controller.UploadImageController
import com.mitratanisejahtera.model.User
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.concurrent.schedule

class ImageActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var response: JSONObject
  private lateinit var imageKTP: ImageView
  private lateinit var imageSelfAndKTP: ImageView
  private lateinit var image: Uri
  private var nameBody: String = "identity_card_image"
  private var filePath: String = ""
  private var fileName: String = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_image)

    user = User(this)
    loading = Loading(this)
    imageKTP = findViewById(R.id.ktpImageView)
    imageSelfAndKTP = findViewById(R.id.userAndKtpImageView)

    imageKTP.setOnClickListener {
      try {
        nameBody = "identity_card_image"
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "image")
        values.put(MediaStore.Images.Media.DESCRIPTION, "image")
        values.put(MediaStore.Images.Media.SIZE, 5)
        image = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image)
        callCameraIntent.putExtra(MediaStore.Images.Media.SIZE, 5)
        startActivityForResult(callCameraIntent, 0)
      } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(
          this, "Terjadi Kesalahan saatmembuka kemera coba ulangi lagi", Toast.LENGTH_LONG
        ).show()
      }
    }

    imageSelfAndKTP.setOnClickListener {
      try {
        nameBody = "identity_card_image_salve"
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "image")
        values.put(MediaStore.Images.Media.DESCRIPTION, "image")
        values.put(MediaStore.Images.Media.SIZE, 5)
        image = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image)
        callCameraIntent.putExtra(MediaStore.Images.Media.SIZE, 5)
        startActivityForResult(callCameraIntent, 0)
      } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(
          this, "Terjadi Kesalahan saat membuka kemera coba ulangi lagi", Toast.LENGTH_LONG
        ).show()
      }
    }
  }

  @SuppressLint("Recycle")
  private fun getRealPathFromImageURI(contentUri: Uri): String {
    val data: Array<String> = Array(100) { MediaStore.Images.Media.DATA }
    val cursor = this.contentResolver.query(contentUri, data, null, null, null)!!
    val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
    cursor.moveToFirst()
    return cursor.getString(columnIndex)
  }

  private fun getImageUri(inImage: Bitmap): Uri {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 80, bytes)
    return Uri.parse(
      MediaStore.Images.Media.insertImage(
        this.contentResolver, inImage, "image", null
      )
    )
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK) {
      try {
        loading.openDialog()
        Timer().schedule(100) {
          filePath = getRealPathFromImageURI(image)
          val convertArray = filePath.split("/").toTypedArray()
          fileName = convertArray.last()
          val thumbnails = MediaStore.Images.Media.getBitmap(contentResolver, image)
          val bitmap = Bitmap.createScaledBitmap(thumbnails, 500, 500, true)
          val matrix = Matrix()
          matrix.postRotate(90F)
          val rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
          runOnUiThread {
            Handler().postDelayed({
              if (nameBody == "identity_card_image") {
                imageKTP.setImageBitmap(rotateBitmap)
              } else {
                imageSelfAndKTP.setImageBitmap(rotateBitmap)
              }
              loading.closeDialog()
            }, 100)

            response = UploadImageController(
              getRealPathFromImageURI(getImageUri(rotateBitmap)), nameBody, user.token
            ).execute().get()
            if (response["code"] == 200) {
              Toast.makeText(
                applicationContext, "Foto Sukses di upload", Toast.LENGTH_LONG
              ).show()
            } else {
              Toast.makeText(
                applicationContext, response["data"].toString(), Toast.LENGTH_LONG
              ).show()
              loading.closeDialog()
            }
          }
        }
      } catch (ex: Exception) {
        loading.closeDialog()
        Toast.makeText(this, "Ada Kesalah saat mengambil gambar", Toast.LENGTH_LONG).show()
      }
    } else {
      Toast.makeText(this, "Anda belum mengisi gambar", Toast.LENGTH_LONG).show()
    }
  }
}
