package com.example.blogger.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BlogEntity::class],version = 1)
abstract class BlogDatabase(): RoomDatabase(){

    abstract fun blogDao():BlogDao
}