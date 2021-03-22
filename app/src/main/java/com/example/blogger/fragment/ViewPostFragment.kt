package com.example.blogger.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.blogger.R
import com.example.blogger.adapter.OnItemClickListener
import com.example.blogger.adapter.ViewPostAdapter
import com.example.blogger.database.BlogDatabase
import com.example.blogger.database.BlogEntity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class ViewPostFragment : Fragment(), OnItemClickListener {

    lateinit var recyclerPost : RecyclerView
    lateinit var layoutManagerPost : RecyclerView.LayoutManager
    lateinit var recyclerPostAdapter : ViewPostAdapter
    lateinit var imgEmptyPost : ImageView
    lateinit var txtEmpty : TextView

    var blogPostList = mutableListOf<BlogEntity>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_post, container, false)

        recyclerPost = view.findViewById(R.id.recyclerPost)
        imgEmptyPost = view.findViewById(R.id.imgEmptyBlogPost)
        txtEmpty = view.findViewById(R.id.txtEmpty)
        layoutManagerPost = LinearLayoutManager(activity)

        val acct = GoogleSignIn.getLastSignedInAccount(requireActivity().applicationContext) as GoogleSignInAccount
        val emaiId = acct.email.toString()

        blogPostList = RetrieveBlogs(requireActivity().applicationContext,emaiId).execute().get()
        showBlogs()



        return view
    }

    fun showBlogs(){

        if(blogPostList.isNotEmpty())
        {
            if(activity!=null)
            {
                imgEmptyPost.visibility = View.GONE
                txtEmpty.visibility = View.GONE
                recyclerPostAdapter = ViewPostAdapter(activity as Context,blogPostList,this)
                recyclerPost.adapter =recyclerPostAdapter
                recyclerPost.layoutManager = layoutManagerPost
            }
            else
            {
                Toast.makeText(activity as Context, "Some error occurred!", Toast.LENGTH_SHORT).show()
            }
        }
        else
        {
            imgEmptyPost.visibility = View.VISIBLE
            txtEmpty.visibility = View.VISIBLE
        }
    }


    class RetrieveBlogs(val context: Context, val email:String) : AsyncTask<Void, Void, MutableList<BlogEntity>>() {
        override fun doInBackground(vararg p0: Void?): MutableList<BlogEntity> {
            val db = Room.databaseBuilder(context, BlogDatabase::class.java, "blogs-db").build()
            return db.blogDao().getBlogs(email)
        }
    }

    override fun onItemClicked(position: Int, action: Int) {
        if(action==1)
        {
            //If edit is clicked, we send data in bundles from home to note fragment so that it can
            //set values of title and notes
            val bundle = Bundle()
            bundle.putString("title",blogPostList[position].title)
            bundle.putString("description",blogPostList[position].desc)
            bundle.putBoolean("update",true)
            bundle.putInt("id",blogPostList[position].id)
            val postFrag = PostFragment()
            postFrag.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame,postFrag)
                .commit()
        }
        if(action==2)
        {
            //This call deletes the note for which delete icon was clicked
            val deleteNote = DeleteBlog(requireActivity().applicationContext,blogPostList[position]).execute().get()
            if(deleteNote)
            {
                //shows dailog box for confirmation
                showAlertDailogDelete(position)
            }
            else
            {
                Toast.makeText(activity as Context,"Some error occured while deletion",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAlertDailogDelete(position: Int) {
        val dailog = AlertDialog.Builder(activity as Context)
        dailog.setTitle("Confirmation")
        dailog.setMessage("Do you want to delete this blog?")
        dailog.setNegativeButton("No"){text,listener ->
            dailog.create().dismiss()
        }
        //when yes is clicked, delete the note from dbNoteList and notify recycler adapter about the changes
        dailog.setPositiveButton("Yes"){text,listener->
            blogPostList.removeAt(position)
            recyclerPostAdapter.notifyDataSetChanged()
            if(blogPostList.isEmpty())
            {
                imgEmptyPost.visibility = View.VISIBLE
                txtEmpty.visibility = View.VISIBLE
            }
            Toast.makeText(activity as Context,"Your Blog was Deleted",Toast.LENGTH_SHORT).show()
        }
        dailog.create()
        dailog.show()
    }

    class DeleteBlog(val context: Context,val blogEntity: BlogEntity) : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            val db = Room.databaseBuilder(context, BlogDatabase::class.java, "blogs-db").build()
            db.blogDao().deleteBlog(blogEntity)
            return true
        }
    }


}