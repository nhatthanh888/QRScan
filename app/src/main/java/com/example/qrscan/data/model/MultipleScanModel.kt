package com.example.qrscan.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
@Entity(tableName = "multipleScanModel")
data class MultipleScanModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "imageResult")
    val imageResult: Int = 0,
    @ColumnInfo(name = "value")
    val value: String = "",
    @ColumnInfo(name = "type")
    val type: String = "",
    @ColumnInfo(name = "time")
    val time: String = "",
    @ColumnInfo(name = "typeCode")
    val typeCode: String = "",
    @ColumnInfo(name = "isCheck")
    var isCheck: Boolean = false,
    @ColumnInfo(name = "isDelete")
    var isDelete: Boolean = false
) : Parcelable