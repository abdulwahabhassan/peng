package com.peng.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.peng.Constants
import timber.log.Timber

@androidx.room.Database(
    entities = [
        CartItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class Database : RoomDatabase() {

    abstract fun cartItemLocalDao(): CartItemLocalDao

    companion object {
        @Volatile private var instance: Database? = null

        fun getInstance(context: Context): Database {
            Timber.d("database get instance")
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(appContext: Context): Database {
            return Room.databaseBuilder(
                appContext,
                Database::class.java,
                Constants.DATABASE_NAME
            ).build()
        }

    }
}