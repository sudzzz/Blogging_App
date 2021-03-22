package com.example.blogger.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data")
data class BlogEntity(
    @ColumnInfo(name = "blogger_name") val name : String,
    @ColumnInfo(name = "blogger_email") val email : String,
    @ColumnInfo(name = "blogger_location") val location : String,
    @ColumnInfo(name = "blog_title") val title : String,
    @ColumnInfo(name = "blog_description") val desc : String
){
    @PrimaryKey(autoGenerate = true)
    var id = 0
}