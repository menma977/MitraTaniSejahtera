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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.mitratanisejahtera.MainActivity
import com.mitratanisejahtera.NavigationActivity
import com.mitratanisejahtera.R
import com.mitratanisejahtera.config.Loading
import com.mitratanisejahtera.controller.ImageGeneratorController
import com.mitratanisejahtera.controller.UploadImageController
import com.mitratanisejahtera.controller.UserController
import com.mitratanisejahtera.model.User
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.concurrent.schedule

class EditProfileActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var response: JSONObject
  private lateinit var goTo: Intent
  private lateinit var name: EditText
  private lateinit var address: EditText
  private lateinit var password: EditText
  private lateinit var profileImage: ImageView
  private lateinit var saveButton: Button
  private lateinit var image: Uri
  private var filePath: String = ""
  private var fileName: String = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_edit_profile)

    user = User(this)
    loading = Loading(this)
    loading.openDialog()

    name = findViewById(R.id.nameEditText)
    address = findViewById(R.id.addressEditText)
    profileImage = findViewById(R.id.profileImageView)
    password = findViewById(R.id.passwordEditText)
    saveButton = findViewById(R.id.saveButton)
    profile()

    profileImage.setOnClickListener {
      try {
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

    saveButton.setOnClickListener {
      loading.openDialog()
      Timer().schedule(100) {
        try {
          runOnUiThread {
            val body = HashMap<String, String>()
            body["name"] = name.text.toString()
            body["address"] = address.text.toString()
            body["password"] = password.text.toString()
            response = UserController.Post(user.token, body).execute().get()
            if (response["code"] == 200) {
              if (response["password"] == true) {
                goTo = Intent(applicationContext, MainActivity::class.java)
                User(applicationContext).clear()
                loading.closeDialog()
                finishAndRemoveTask()
                startActivity(goTo)
              } else {
                Toast.makeText(applicationContext, response["response"].toString(), Toast.LENGTH_SHORT).show()
              }
            } else {
              Toast.makeText(applicationContext, response["response"].toString(), Toast.LENGTH_SHORT).show()
            }
            loading.closeDialog()
          }
        } catch (ex: Exception) {
          ex.printStackTrace()
          runOnUiThread {
            Toast.makeText(applicationContext, getString(R.string.code_500), Toast.LENGTH_SHORT).show()
          }
        }
      }
    }
  }

  private fun profile() {
    Timer().schedule(100) {
      response = UserController.Get(user.token).execute().get()
      runOnUiThread {
        when {
          response["code"] == 200 -> {
            name.setText(response.getJSONObject("response")["name"].toString())
            address.setText(response.getJSONObject("response")["address"].toString())
            changeProfileImage(response.getJSONObject("response")["image"].toString())
          }
          response["code"] == 401 -> {
            goTo = Intent(applicationContext, NavigationActivity::class.java)
            loading.closeDialog()
            finish()
            startActivity(goTo)
          }
          else -> {
            loading.closeDialog()
            finish()
          }
        }
      }
    }
  }

  private fun changeProfileImage(urlImage: String) {
    if (urlImage.isNotEmpty()) {
      val imageGeneratorController = ImageGeneratorController(urlImage)
      val gitBitmap = imageGeneratorController.execute().get()
      profileImage.setImageBitmap(gitBitmap)
    }
    loading.closeDialog()
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
              profileImage.setImageBitmap(rotateBitmap)
              loading.closeDialog()
            }, 100)

            response = UploadImageController(
              getRealPathFromImageURI(getImageUri(rotateBitmap)), "image", user.token
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
