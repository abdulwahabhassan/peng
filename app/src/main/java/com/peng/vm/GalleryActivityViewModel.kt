package com.peng.vm

import android.app.Application
import android.content.ContentUris
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.peng.model.MediaStoreImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class GalleryActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val _images = MutableLiveData<List<MediaStoreImage>>()
    val images: LiveData<List<MediaStoreImage>> get() = _images

    fun loadImages() {
        viewModelScope.launch {
            _images.postValue(queryImages())
        }
    }

    private suspend fun queryImages(): List<MediaStoreImage> {
        val images = mutableListOf<MediaStoreImage>()

        withContext(Dispatchers.IO) {

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Images.Media.DATA)

            val selection = null

            val selectionArgs = arrayOf<String>()

            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            getApplication<Application>().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->

                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn = cursor.getColumnIndexOrThrow(
                    MediaStore.Images.Media.DISPLAY_NAME)
                val sizeColumn = cursor.getColumnIndex(MediaStore.Images.Media.SIZE)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val size = cursor.getInt(sizeColumn)
                    val data = cursor.getString(dataColumn)

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                    val image = MediaStoreImage(id, contentUri, displayName, size, data)
                    images += image

                    Timber.d("Added image: $image")
                }
            }
        }
        return images
    }

}