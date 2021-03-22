package com.example.blogger.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.blogger.R
import com.example.blogger.database.BlogDatabase
import com.example.blogger.database.BlogEntity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONException

class PostFragment : Fragment() {

    lateinit var etTitle : EditText
    lateinit var etBlog : EditText
    lateinit var saveBlog : FloatingActionButton
    var edit : Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_post, container, false)

        etTitle = view.findViewById(R.id.etTitle)
        etBlog = view.findViewById(R.id.etBlog)
        saveBlog = view.findViewById(R.id.fabSaveBlog)



        val value = arguments?.getBoolean("update")
        if(value!=null)
        {
            edit = value
        }

        if(edit)
        {
            val updateTitle = arguments?.getString("title")
            val updateBlog = arguments?.getString("description")

            etTitle.setText(updateTitle)
            etBlog.setText(updateBlog)
            (activity as AppCompatActivity).supportActionBar?.title = "Update Your Blog"
        }
        else
        {
            (activity as AppCompatActivity).supportActionBar?.title = "Create Blog"
        }

        saveBlog.setOnClickListener {
            val acct = GoogleSignIn.getLastSignedInAccount(requireActivity().applicationContext) as GoogleSignInAccount
            val title = etTitle.text.toString()
            val blog = etBlog.text.toString()


            if(title.trim().isNotEmpty() && blog.trim().isNotEmpty()) {
                val queue = Volley.newRequestQueue(activity as Context)
                val url = "http://ip-api.com/json/"


                val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null, Response.Listener{

                    try {
                        val success = it.getString("status")

                        if(success.equals("success")) {
                            val city = it.getString("city").toString()
                            println(city)
                            val blogEntity = BlogEntity(
                                    acct.displayName.toString(),
                                    acct.email.toString(),
                                    city,
                                    title,
                                    blog
                            )
                            if(edit)
                            {
                                val updateBlog = UpdateBlog(requireActivity().applicationContext,title,blog,arguments?.getInt("id")!!).execute().get()
                                if(updateBlog)
                                {
                                    requireActivity().supportFragmentManager.beginTransaction()
                                            .replace(R.id.frame,HomeFragment())
                                            .commit()
                                    (activity as AppCompatActivity).supportActionBar?.title = "Welcome "+acct.displayName
                                    Toast.makeText(activity as Context,"Your Blog is Updataed",Toast.LENGTH_SHORT).show()
                                }
                                else
                                {
                                    Toast.makeText(activity as Context,"Some Error Occured while Updation",Toast.LENGTH_SHORT).show()
                                }
                            }
                            else{
                                val addBlog =  AddBlog(requireActivity().applicationContext,blogEntity).execute().get()

                                if(addBlog)
                                {
                                    requireActivity().supportFragmentManager.beginTransaction()
                                            .replace(R.id.frame,HomeFragment())
                                            .commit()
                                    (activity as AppCompatActivity).supportActionBar?.title = "Welcome "+acct.displayName
                                    Toast.makeText(activity as Context,"Your Blog is Posted",Toast.LENGTH_SHORT).show()
                                }
                                else
                                {
                                    Toast.makeText(activity as Context,"Some Error Occured while Posting Blog",Toast.LENGTH_SHORT).show()
                                }

                            }
                        }
                    }
                    catch (e: JSONException){
                        Toast.makeText(activity as Context, "Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
                    }

                },Response.ErrorListener {
                    //Here we will handle the errors
                    if (activity != null) {
                        Toast.makeText(activity as Context,"Volley error occurred!", Toast.LENGTH_SHORT).show()
                    }
                }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)
            }
            else
            {
                // when either of title or heading is not given but save button is clicked.
                Toast.makeText(activity as Context, "Please write both title and blog",Toast.LENGTH_SHORT).show()
            }

        }
        return view
    }

    //Updates Note in database
    class UpdateBlog(val context: Context,val head:String,val body:String,val id:Int) : AsyncTask<Void, Void, Boolean>(){

        val db = Room.databaseBuilder(context, BlogDatabase::class.java,"blogs-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            db.blogDao().update(head,body,id)
            return true

        }

    }


    //Inserts new note in database
    class AddBlog(val context: Context,val blogEntity: BlogEntity) : AsyncTask<Void, Void, Boolean>(){
        val db = Room.databaseBuilder(context, BlogDatabase::class.java,"blogs-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.blogDao().insertBlog(blogEntity)
            return true
        }

    }


}