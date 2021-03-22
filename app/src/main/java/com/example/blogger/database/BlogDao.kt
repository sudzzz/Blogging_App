package com.example.blogger.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BlogDao {

    @Insert
    fun insertBlog(blogEntity: BlogEntity)

    @Delete
    fun deleteBlog(blogEntity: BlogEntity)

    @Query("UPDATE data SET blog_title = :title, blog_description = :blog WHERE id = :id")
    fun update(title:String,blog:String,id:Int)

    @Query("SELECT * from data order by id DESC")
    fun getAllBlogs():MutableList<BlogEntity>

    @Query("SELECT * FROM data where blogger_email = :email order by id DESC")
    fun getBlogs(email:String): MutableList<BlogEntity>

    @Query("SELECT * FROM data where blogger_name = :name order by id DESC")
    fun getBlogsByName(name:String): List<BlogEntity>
}