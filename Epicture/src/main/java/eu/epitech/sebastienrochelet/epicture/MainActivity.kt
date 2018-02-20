package eu.epitech.sebastienrochelet.epicture

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.oauth.OAuth
import io.oauth.OAuthCallback
import io.oauth.OAuthData

//First activity
class MainActivity : AppCompatActivity(), OAuthCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    //Click listener for the log-in button
    //Start oAuth login
    fun signUp(view: View) {
        val oAuth = OAuth(this)
        oAuth.initialize(getString(R.string.public_key))
        oAuth.popup("instagram", this)
    }

    //Called when oAuth login succeeded
    override fun onFinished(data: OAuthData?) {
        if (data != null && data.error == null) {
            println(data.token)
            println(data.request)
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        }
    }
}
