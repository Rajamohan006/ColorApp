package com.example.colorapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ColorRepository(
    private val colorDao: ColorDao
) {
    fun getAllColors(): LiveData<List<ColorEntity>> {
        return colorDao.getAllColors()
    }

    suspend fun insertColor(color: ColorEntity) {
        colorDao.insertColor(color)
    }

    fun getUnSyncedColors(): LiveData<List<ColorEntity>> {
        return colorDao.getUnSyncedColors()
    }

    suspend fun markColorAsSynced(colorId: Int) {
        colorDao.markColorAsSynced(colorId)
    }
}

