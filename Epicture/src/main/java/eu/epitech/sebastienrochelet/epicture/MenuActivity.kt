package eu.epitech.sebastienrochelet.epicture

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.bumptech.glide.Glide
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpPost
import eu.epitech.sebastienrochelet.epicture.apiManagment.UserModel
import eu.epitech.sebastienrochelet.epicture.apiManagment.ApiManager
import eu.epitech.sebastienrochelet.epicture.apiManagment.MediaModel
import eu.epitech.sebastienrochelet.epicture.fragments.*
import io.oauth.OAuthCallback
import io.oauth.OAuthData
import io.oauth.OAuthRequest
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.app_bar_menu.*

class MenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, FeedFragment.OnListFragmentInteractionListener {
    private lateinit var homeFragment: HomeFragment
    private val feedFragment = FeedFragment()
    private val searchFragment = SearchFragment()
    private val favoritesFragment = FavoritesFragment()
    private val filtersFragment = FiltersFragment()
    private lateinit var data: OAuthData
    private var user: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        data = intent.getParcelableExtra("data")
        println(data.token)
        //feedFragment = FeedFragment.newInstance(data)
        ApiManager.getUser(data) {
            user = it
            println(user)
            if (user != null) {
                val header = nav_view.getHeaderView(0)
                header.findViewById<TextView>(R.id.full_name).text = user!!.fullName
                header.findViewById<TextView>(R.id.username).text = user!!.username
                Glide.with(this).load(user!!.profilPicture).into(header.findViewById(R.id.profile_image))
                homeFragment = HomeFragment.newInstance(user!!)
                supportFragmentManager.beginTransaction().add(R.id.fragment_container, homeFragment).commit()
            }
        }

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.setCheckedItem(R.id.nav_home)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                replaceFragment(homeFragment)
            }
            R.id.nav_feed -> {
                ApiManager.getFeed(data) {
                    feedFragment.medias = it!!
                    replaceFragment(feedFragment)
                }
            }
            R.id.nav_search -> {
                replaceFragment(searchFragment)
            }
            R.id.nav_favorites -> {
                replaceFragment(favoritesFragment)
            }
            R.id.nav_filters -> {
                replaceFragment(filtersFragment)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun replaceFragment(newFragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.fragment_container, newFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onListFragmentInteraction(media: MediaModel) {
        val alert = AlertDialog.Builder(this)
        alert.setMessage("Enter a new comment")
        val view = TextInputEditText(this)
        alert.setView(view)
        alert.setPositiveButton("Add") {dialog, positiveButton ->
            if (view.text != null) run {
                var text = ""
                for (c in view.text) {
                    text += if (c == ' ')
                        '+'
                    else c

                }
                ("https://api.instagram.com/v1/media/" + media.id + "/comments").httpPost(listOf(Pair("access_token", data.token), Pair("text", view.text))).responseJson{_, _, result ->
                    val (data, error) = result
                    if (error != null) {
                        println(error)
                    }
                }
            }
            dialog.dismiss()
        }
        alert.setNegativeButton("Cancel") {dialog, positiveButton ->
            dialog.dismiss()
        }
        alert.show()
    }
}
