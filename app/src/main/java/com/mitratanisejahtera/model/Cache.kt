package com.mitratanisejahtera.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

@SuppressLint("CommitPrefEdits")
class Cache(context: Context) {
  private val sharedPreferences: SharedPreferences
  private val sharedPreferencesEditor: SharedPreferences.Editor

  init {
    sharedPreferences = context.getSharedPreferences(userData, Context.MODE_PRIVATE)
    sharedPreferencesEditor = sharedPreferences.edit()
  }

  fun set(jsonObject: String) {
    sharedPreferencesEditor.putString("json", jsonObject)
    sharedPreferencesEditor.commit()
  }

  fun get(): JSONObject {
    return JSONObject(
      sharedPreferences.getString(
        "json",
        "{'token': '', 'username': '', 'image': '', 'status': 0, 'type': ''}"
      )
    )
  }

  fun remove() {
    sharedPreferences.edit().clear().apply()
    sharedPreferencesEditor.clear()
  }

  companion object {
    private const val userData = "userData"
  }
}