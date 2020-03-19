package com.mitratanisejahtera

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mitratanisejahtera.config.Loading
import com.mitratanisejahtera.controller.LogoutController
import com.mitratanisejahtera.fragment.GalleryFragment
import com.mitratanisejahtera.fragment.HomeFragment
import com.mitratanisejahtera.fragment.ProfileFragment
import com.mitratanisejahtera.model.User
import java.util.*
import kotlin.concurrent.schedule

class NavigationActivity : AppCompatActivity() {
  private lateinit var navView: BottomNavigationView
  private lateinit var logout: ImageButton
  private lateinit var loading: Loading
  private lateinit var goTo: Intent

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_navigation)

    loading = Loading(this)
    logout = findViewById(R.id.logoutButton)
    loading.openDialog()

    navView = findViewById(R.id.nav_view)
    navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    val fragment = HomeFragment()
    addFragment(fragment)

    logout.setOnClickListener {
      loading.openDialog()
      Timer().schedule(100) {
        val response = LogoutController(User(applicationContext).token).execute().get()
        if (response["code"] == 200) {
          runOnUiThread {
            goTo = Intent(applicationContext, MainActivity::class.java)
            User(applicationContext).clear()
            loading.closeDialog()
            finishAndRemoveTask()
            startActivity(goTo)
          }
        } else {
          runOnUiThread {
            loading.closeDialog()
            Toast.makeText(
              applicationContext, applicationContext.getString(
                response["response"].toString().toInt()
              ), Toast.LENGTH_SHORT
            ).show()
          }
        }
      }
    }

    loading.closeDialog()
  }

  private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
    when (item.itemId) {
      R.id.navigation_home -> {
        val fragment = HomeFragment()
        addFragment(fragment)
        return@OnNavigationItemSelectedListener true
      }
      R.id.navigation_profile -> {
        val fragment = ProfileFragment()
        addFragment(fragment)
        return@OnNavigationItemSelectedListener true
      }
      R.id.navigation_gallery -> {
        val fragment = GalleryFragment()
        addFragment(fragment)
        return@OnNavigationItemSelectedListener true
      }
    }
    false
  }

  @SuppressLint("PrivateResource")
  private fun addFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction().setCustomAnimations(
      R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out
    ).replace(R.id.contentFragment, fragment, fragment.javaClass.simpleName).commit()
  }
}
