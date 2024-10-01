package com.example.qrscan.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
@Entity(tableName = "resultScanModel")
data class ResultScanModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "typeResult")
    val typeResult: String = "",
    @ColumnInfo(name = "result")
    val result: String = "",
    @ColumnInfo(name = "time")
    val time: String = "",
    @ColumnInfo(name = "typeCode")
    val typeCodeScan: String = "",
    @ColumnInfo(name = "favorite")
    val favorite: Boolean = false,
    @ColumnInfo(name = "scanned")
    val scanned: Boolean = false,
    @ColumnInfo(name = "created")
    val created: Boolean = false,
    @ColumnInfo(name = "isDelete")
    var isDelete: Boolean = false,
    @ColumnInfo(name = "isCheck")
    var isCheck: Boolean = false
) : Parcelable