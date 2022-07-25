package com.peng.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.peng.ui.activity.GalleryActivity

class GalleryActivityContract : ActivityResultContract<Unit, String>() {

    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(context, GalleryActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String {
        val imageUri = intent?.getStringExtra(GalleryActivity.IMAGE_URI)
        return if (resultCode == Activity.RESULT_OK && imageUri != null) imageUri else ""
    }


}