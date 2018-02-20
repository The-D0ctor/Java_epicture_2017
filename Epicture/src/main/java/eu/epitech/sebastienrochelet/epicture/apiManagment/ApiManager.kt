package eu.epitech.sebastienrochelet.epicture.apiManagment

import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import io.oauth.OAuthData
import io.oauth.OAuthRequest
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by sebastienrochelet on 11/02/2018.
 */

//class that manage the calls to api for getting infos
class ApiManager {
    companion object {
        //get the user
        fun getUser(data: OAuthData, callback: (user: UserModel) -> Unit) {
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
                            callback(UserModel())
                        }
                    }
                }

                override fun onSetHeader(header: String?, value: String?) {}
                override fun onError(message: String?) {
                    println(message)
                }
            })
        }

        //get the medias associated to this user
        fun getFeed(data: OAuthData, callback: (medias: List<MediaModel>) -> Unit) {
            data.http("/v1/users/self/media/recent", object : OAuthRequest() {
                override fun onSetURL(url: String?) {
                    url!!.httpGet().responseJson { _, _, result ->
                        val (data , error) = result
                        val medias: MutableList<MediaModel> = mutableListOf()
                        if (error == null) {
                            val jsonArray = data!!.obj()["data"] as JSONArray
                            (0 until jsonArray.length()).mapTo(medias) { MediaModel(jsonArray[it] as JSONObject) }
                            callback(medias)
                        }
                        else {
                            println(error)
                            callback(listOf())
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