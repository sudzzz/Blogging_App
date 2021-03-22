package com.example.blogger.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blogger.R
import com.example.blogger.database.BlogEntity

class HomeAdapter(val context: Context, val blogList: MutableList<BlogEntity>): RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtBloggerName: TextView = view.findViewById(R.id.txtBloggerName)
        val txtLocation: TextView = view.findViewById(R.id.txtLocation)
        val txtTitleHome: TextView = view.findViewById(R.id.txtTitleHome)
        val txtBlogHome: TextView = view.findViewById(R.id.txtBlogHome)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_home, parent, false)

        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return blogList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val result = blogList[position]
        holder.txtBloggerName.text = result.name
        holder.txtLocation.text = result.location
        holder.txtTitleHome.text = result.title
        holder.txtBlogHome.text = result.desc
    }

}