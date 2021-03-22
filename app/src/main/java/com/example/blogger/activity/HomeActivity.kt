package com.example.blogger.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.blogger.R
import com.example.blogger.fragment.HomeFragment
import com.example.blogger.fragment.ViewPostFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var sharedPreferences:SharedPreferences

    var previousMenuItem: MenuItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)

        sharedPreferences = getSharedPreferences(getString(R.string.login_preference), Context.MODE_PRIVATE)

        setUpToolbar()
        openHome()

        val acct = GoogleSignIn.getLastSignedInAccount(this@HomeActivity) as GoogleSignInAccount
        val emaiId = acct.email.toString()
        val name = acct.displayName.toString()



        val actionBarDrawerToggle = ActionBarDrawerToggle(this@HomeActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        val convertView = LayoutInflater.from(this@HomeActivity).inflate(R.layout.drawer_header,null)
        val userName: TextView = convertView.findViewById(R.id.txtName)
        val userEmail: TextView = convertView.findViewById(R.id.txtEmail)

        userName.text = name
        userEmail.text = emaiId
        navigationView.addHeaderView(convertView)

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it
            when(it.itemId)
            {
                R.id.home -> {
                    openHome()
                    drawerLayout.closeDrawers()
                }
                R.id.yourPost -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            ViewPostFragment()
                        ).addToBackStack("Home")
                        .commit()
                    supportActionBar?.title = name + "'s Posts"
                    drawerLayout.closeDrawers()
                }
                R.id.logout -> {
                    val dialog = AlertDialog.Builder(this@HomeActivity as Context)
                    dialog.setTitle("Conformation")
                    dialog.setMessage("Are you sure you want to Logout?")
                    dialog.setPositiveButton("Yes"){text, listener ->
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .build()
                        val mGoogleSignInClient = GoogleSignIn.getClient(this@HomeActivity as Context, gso);
                        mGoogleSignInClient.signOut()
                        val intent = Intent(this@HomeActivity,LoginActivity::class.java)
                        startActivity(intent)
                        sharedPreferences.edit().putBoolean("isLoggedIn",false).apply()
                        finish()
                    }

                    dialog.setNegativeButton("No") {text, listener ->

                    }
                    dialog.create()
                    dialog.show()
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }

    fun setUpToolbar()
    {

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if(id==android.R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    fun openHome()
    {
        val acct = GoogleSignIn.getLastSignedInAccount(this@HomeActivity) as GoogleSignInAccount
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame,fragment)
        transaction.commit()
        navigationView.setCheckedItem(R.id.home)
        supportActionBar?.title = "Welcome "+acct.displayName
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)

        when(frag)
        {
            !is HomeFragment -> openHome()

            else->{
                super.onBackPressed()
                finish()
            }
        }
    }
}