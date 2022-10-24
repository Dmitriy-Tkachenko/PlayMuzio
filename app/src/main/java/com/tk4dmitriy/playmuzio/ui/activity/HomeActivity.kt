package com.tk4dmitriy.playmuzio.ui.activity

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.allViews
import com.google.android.material.imageview.ShapeableImageView
import com.tk4dmitriy.playmuzio.R
import com.tk4dmitriy.playmuzio.data.api.RetrofitBuilder
import com.tk4dmitriy.playmuzio.ui.fragment.HomeFragment
import com.tk4dmitriy.playmuzio.utils.Constants

private const val TAG_HOME_FRAGMENT = "HOME_FRAGMENT"

class HomeActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupUI(savedInstanceState)
    }

    private fun setupUI(savedInstanceState: Bundle?) {
        val isFragmentContainerEmpty = savedInstanceState == null

        if (isFragmentContainerEmpty) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, HomeFragment(), TAG_HOME_FRAGMENT)
                .commit()
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val currentFocus: View? = currentFocus
            // If EditText has focused
            if (currentFocus is EditText) {
                val outRectEt = Rect()
                currentFocus.getGlobalVisibleRect(outRectEt)
                // If you touched down an area that is not an EditText
                if (!outRectEt.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    currentFocus.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0)
                }

                val fragment: HomeFragment = supportFragmentManager.findFragmentByTag(TAG_HOME_FRAGMENT) as HomeFragment
                val views = fragment.requireView().allViews
                for (view in views) {
                    if (view::class.java == ShapeableImageView::class.java) {
                        val outRectSiv = Rect()
                        view.getGlobalVisibleRect(outRectSiv)
                        // If the ShapeableImageView is touch down, then we do not process the touch
                        if (outRectSiv.contains(event.rawX.toInt(), event.rawY.toInt())) {
                            return false
                        }
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}