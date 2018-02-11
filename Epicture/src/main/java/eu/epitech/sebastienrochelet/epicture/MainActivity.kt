package eu.epitech.sebastienrochelet.epicture

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import io.oauth.OAuth
import io.oauth.OAuthCallback
import io.oauth.OAuthData
import io.oauth.OAuthRequest
import org.json.JSONObject

class MainActivity : AppCompatActivity(), OAuthCallback {

    var requestUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun signUp(view: View) {
        val oAuth = OAuth(this)
        oAuth.initialize(getString(R.string.public_key))
        oAuth.popup("instagram", this)
    }

    override fun onFinished(data: OAuthData?) {
        if (data != null) {
            println(data.token)
            println(data.request)
            data.http("/v1/users/self", object: OAuthRequest() {
                override fun onSetURL(url: String?) {
                    println(url)
                    requestUrl = url
                    //TODO implement models
                    url!!.httpGet().responseJson { _, _, result ->
                        val (data, error) = result
                        if (error == null) {
                            println(data)
                            println((data!!.obj()["data"] as JSONObject)["full_name"])
                        } else {
                            println(error)
                        }
                    }
                }

                override fun onSetHeader(header: String?, value: String?) {}
            })
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
        }
    }
}