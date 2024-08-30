package com.example.colorapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
@Dao
interface ColorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertColor(color: ColorEntity)

    @Query("SELECT * FROM colors")
    fun getAllColors(): LiveData<List<ColorEntity>>

    @Query("SELECT * FROM colors WHERE synced = 0")
    fun getUnSyncedColors(): LiveData<List<ColorEntity>>

    @Query("UPDATE colors SET synced = 1 WHERE id = :colorId")
    suspend fun markColorAsSynced(colorId: Int)
}
