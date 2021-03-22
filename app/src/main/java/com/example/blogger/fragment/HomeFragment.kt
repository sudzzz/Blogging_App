package com.example.blogger.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.blogger.R
import com.example.blogger.adapter.HomeAdapter
import com.example.blogger.database.BlogDatabase
import com.example.blogger.database.BlogEntity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


class HomeFragment : Fragment() {

    lateinit var recyclerHome : RecyclerView
    lateinit var addBlog : FloatingActionButton
    lateinit var imgEmptyHome : ImageView
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var recyclerHomeAdapter: HomeAdapter
    lateinit var searchName : SearchView

    var blogList = mutableListOf<BlogEntity>()
    var blogListByName = mutableListOf<BlogEntity>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        addBlog = view.findViewById(R.id.fabAddBlog)
        imgEmptyHome = view.findViewById(R.id.imgEmptyBlogHome)
        searchName = view.findViewById(R.id.searchView)
        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)

        blogList = RetrieveAllBlogs(requireActivity().applicationContext).execute().get()
        blogListByName.addAll(blogList)

        showAllBlogs()

        searchName.setOnQueryTextListener(object :  SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!!.isNotEmpty())
                {
                    blogList.clear()
                    val search = newText.toLowerCase(Locale.getDefault())
                    blogListByName.forEach {
                        if(it.name.toLowerCase(Locale.getDefault()).contains(search))
                        {
                            blogList.add(it)
                        }
                    }
                    recyclerHomeAdapter.notifyDataSetChanged()
                }
                else
                {
                    blogList.clear()
                    blogList.addAll(blogListByName)
                    recyclerHomeAdapter.notifyDataSetChanged()
                }
                return true
            }

        })

        addBlog.setOnClickListener {
            val fragPost = PostFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame,fragPost)
                .commit()
        }

        return view
    }

    fun showAllBlogs()
    {
        //If list is not empty and activity is not null, we will attach recycler view to Adapter class and set its layout
        //Here I used Linear Layout
        if(blogList.isNotEmpty())
        {
            if(activity!=null)
            {
                imgEmptyHome.visibility = View.INVISIBLE
                recyclerHomeAdapter = HomeAdapter(activity as Context,blogList)
                recyclerHome.adapter = recyclerHomeAdapter
                recyclerHome.layoutManager = layoutManager
            }
            else
            {
                Toast.makeText(activity as Context, "Some error occurred!", Toast.LENGTH_SHORT).show()
            }
        }

        //If list is empty, we will display the backgroung image
        else
        {
            searchName.visibility = View.GONE
            imgEmptyHome.visibility = View.VISIBLE
        }


    }


    class RetrieveAllBlogs(val context: Context) : AsyncTask<Void, Void, MutableList<BlogEntity>>() {
        override fun doInBackground(vararg p0: Void?): MutableList<BlogEntity> {
            val db = Room.databaseBuilder(context, BlogDatabase::class.java, "blogs-db").build()
            return db.blogDao().getAllBlogs()
        }
    }

    class RetrieveBlogsByName(val context: Context,val name : String) : AsyncTask<Void, Void, List<BlogEntity>>() {
        override fun doInBackground(vararg p0: Void?): List<BlogEntity> {
            val db = Room.databaseBuilder(context, BlogDatabase::class.java, "blogs-db").build()
            return db.blogDao().getBlogsByName(name)
        }
    }



}