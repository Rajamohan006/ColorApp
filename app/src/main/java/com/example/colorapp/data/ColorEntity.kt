package com.example.colorapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "colors")
data class ColorEntity(
    @PrimaryKey(autoGenerate = true) val id:Int,
    @ColumnInfo(name = "color") val color: String,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "synced") val synced: Boolean = false
)
