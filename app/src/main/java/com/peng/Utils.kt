package com.peng

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.res.ResourcesCompat
import java.net.NetworkInterface
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class Utils {

    fun formatCurrency(value: Any): String {
        val valueToBeFormatted: Number =
            if (value is String) value.toDouble() else value as Number
        val df = DecimalFormat("##,###,##0.00")
        return df.format(valueToBeFormatted)
    }

    // used for hiding input keyboard
    fun hideKeyboard(context: Context?, view: View?) {
        if (context != null) {
            val imm =
                context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (view != null) {
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }


}