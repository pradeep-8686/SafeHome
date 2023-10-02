package com.example.safehome.custom

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.safehome.R

class CustomProgressDialog {

    lateinit var dialog: CustomDialog

    /**
     * Method responsible for progress dialog
     */

    @RequiresApi(Build.VERSION_CODES.Q)
    fun progressDialogShow(context: Context, title: CharSequence?) {
        val inflater = (context as Activity).layoutInflater
        val view = inflater.inflate(R.layout.custom_progress_bar, null)

        val cpTitle = view.findViewById<TextView>(R.id.cp_title)
        val cpBar = view.findViewById<ProgressBar>(R.id.cp_pbar)
        if (title != null) {

            cpTitle.text = title
        }
        // Progress Bar Color
        setColorFilter(
            cpBar.indeterminateDrawable,
            ResourcesCompat.getColor(context.resources, androidx.appcompat.R.color.primary_dark_material_dark, null)
        )
        // Text Color
        cpTitle.setTextColor(ContextCompat.getColor(context, androidx.appcompat.R.color.primary_dark_material_dark))

        dialog = CustomDialog(context)


        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)

        // lp.width = (Utils.screenWidth * 0.70).toInt()
        // lp.height = (Utils.screenHeight * 0.30).toInt()
        lp.height = LinearLayout.LayoutParams.MATCH_PARENT
        lp.height = LinearLayout.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.CENTER
        dialog.window!!.attributes = lp

        // set view to dialog
        dialog.setContentView(view)
        dialog.show()
    }

    fun progressDialogDismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    private fun setColorFilter(drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    class CustomDialog(context: Context) : Dialog(context, R.style.CustomDialogTheme) {
        init {
            // Set Semi-Transparent Color for Dialog Background
            window?.decorView?.rootView?.setBackgroundResource(R.color.progress_dialog_background_transparent)
            window?.decorView?.setOnApplyWindowInsetsListener { _, insets ->
                insets.inset(0, 0, 0, 0)
            }
        }
    }
}