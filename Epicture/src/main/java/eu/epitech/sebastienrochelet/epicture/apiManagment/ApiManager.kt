package eu.epitech.sebastienrochelet.epicture.apiManagment

import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import io.oauth.OAuthData
import io.oauth.OAuthRequest
import org.json.JSONObject

/**
 * Created by sebastienrochelet on 11/02/2018.
 */

class ApiManager {
    companion object {
        fun getUser(data: OAuthData, callback: (user: UserModel?) -> Unit) {
            println(data.token)
            data.http("/v1/users/self", object : OAuthRequest() {
                override fun onSetURL(url: String?) {
                    println(url)
                    url!!.httpGet().responseJson { _, _, result ->
                        val (data, error) = result
                        if (error == null) {
                            val user = UserModel(data!!.obj()["data"] as JSONObject)
                            println(data)
                            println((data.obj()["data"] as JSONObject)["full_name"])
                            callback(user)
                        } else {
                            println(error)
                            callback(null)
                        }
                    }
                }

                override fun onSetHeader(header: String?, value: String?) {}
                override fun onError(message: String?) {
                    println(message)
                }
            })
        }
    }
}