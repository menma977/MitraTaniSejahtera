package com.mitratanisejahtera.controller

import android.os.AsyncTask
import com.mitratanisejahtera.R
import com.mitratanisejahtera.model.Url
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody

class UploadTransferController(
  private val token: String, private val index: String, private val path: String
) : AsyncTask<Void, Void, JSONObject>() {
  override fun doInBackground(vararg params: Void?): JSONObject {
    try {
      val client = OkHttpClient()
      val sendBody = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("index", index)
        .addFormDataPart("image", path, File(path).asRequestBody("application/octet-stream".toMediaTypeOrNull())).build()
      val request: Request = Request.Builder().url("${Url.get()}/tree/transfer").method("POST", sendBody)
        .addHeader("X-Requested-With", "XMLHttpRequest").addHeader("Content-Type", "application/x-www-form-urlencoded")
        .addHeader("Authorization", "Bearer $token").build()
      val response: Response = client.newCall(request).execute()
      val input = BufferedReader(InputStreamReader(response.body!!.byteStream()))
      val inputData: String = input.readLine()
      val convertJSON = JSONObject(inputData)
      input.close()
      return if (response.isSuccessful) {
        JSONObject().put("code", response.code).put("data", convertJSON["response"])
      } else {
        JSONObject().put("code", response.code).put(
          "data", convertJSON.getJSONObject("errors").getJSONArray(
            convertJSON.getJSONObject("errors").names()[0].toString()
          )[0]
        )
        JSONObject().put("code", response.code).put("data", convertJSON["errors"].toString())
      }
    } catch (e: Exception) {
      e.printStackTrace()
      return JSONObject().put("code", 500).put("data", R.string.code_500)
    }
  }
}