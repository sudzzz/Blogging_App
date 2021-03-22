package com.example.blogger.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blogger.R
import com.example.blogger.database.BlogEntity

class ViewPostAdapter(val context: Context, val blogList: MutableList<BlogEntity>, val itemClickListener: OnItemClickListener):RecyclerView.Adapter<ViewPostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_post, parent, false)

        return PostViewHolder(view)
    }

    override fun getItemCount(): Int {
        return blogList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
       val result = blogList[position]
        holder.txtTitle.text = result.title
        holder.txtBlog.text = result.desc

        //Action 1 on Edit and 2 on Delete
        holder.setOnClick(itemClickListener, position, 1)
        holder.setOnClick(itemClickListener, position, 2)
    }

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val txtTitle : TextView = view.findViewById(R.id.txtTitle)
        val txtBlog : TextView = view.findViewById(R.id.txtBlog)
        val imgEdit : ImageView = view.findViewById(R.id.imgEdit)
        val imgDelete : ImageView = view.findViewById(R.id.imgDelete)

        fun setOnClick(clickListener: OnItemClickListener, position: Int, action: Int) {

            when (action) {
                1 -> {
                    imgEdit.setOnClickListener {
                        clickListener.onItemClicked(position, action)
                    }
                }
                2 -> {
                    imgDelete.setOnClickListener {
                        clickListener.onItemClicked(position, action)
                    }
                }
            }
        }
    }
}

interface OnItemClickListener {
    fun onItemClicked(position: Int, action: Int)
}