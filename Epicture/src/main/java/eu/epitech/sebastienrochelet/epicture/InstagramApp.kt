package eu.epitech.sebastienrochelet.epicture

import android.app.ProgressDialog
import android.widget.ProgressBar
import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.Log
import org.json.JSONObject
import org.json.JSONTokener
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by nathan on 06/02/2018.
 */

class InstagramApp(private val mCtx: Context, private val mClientId: String, private val mClientSecret: String,
                   callbackUrl: String) {
    private val mSession: InstagramSession = InstagramSession(mCtx)
    private val mDialog: InstagramDialog
    private var mListener: OAuthAuthenticationListener? = null
    private val mProgress: ProgressBar
    private val mAuthUrl: String
    private val mTokenUrl: String
    private var mAccessToken: String? = null


    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == WHAT_ERROR) {
                mProgress.dismiss()
                if (msg.arg1 == 1) {
                    mListener!!.onFail("Failed to get access token")
                } else if (msg.arg1 == 2) {
                    mListener!!.onFail("Failed to get user information")
                }
            } else if (msg.what == WHAT_FETCH_INFO) {
                fetchUserName()
            } else {
                mProgress.dismiss()
                mListener!!.onSuccess()
            }
        }
    }

    val userName: String
        get() = mSession.getUsername()

    val id: String
        get() = mSession.getId()
    val name: String
        get() = mSession.getName()

    init {
        mAccessToken = mSession.getAccessToken()
        mCallbackUrl = callbackUrl
        mTokenUrl = (TOKEN_URL + "?client_id=" + mClientId + "&client_secret="
                + mClientSecret + "&redirect_uri=" + mCallbackUrl + "&grant_type=authorization_code")
        mAuthUrl = (AUTH_URL + "?client_id=" + mClientId + "&redirect_uri="
                + mCallbackUrl + "&response_type=code&display=touch&scope=likes+comments+relationships")
        val listener = object : InstagramDialog.OAuthDialogListener {
            override fun onComplete(code: String) {
                getAccessToken(code)
            }

            override fun onError(error: String) {
                mListener!!.onFail("Authorization failed")
            }
        }

        mDialog = InstagramDialog(mCtx, mAuthUrl, listener)
        mProgress = ProgressDialog(mCtx)
        mProgress.setCancelable(false)
    }

    private fun getAccessToken(code: String) {
        mProgress.setMessage("Getting access token ...")
        mProgress.show()

        object : Thread() {
            override fun run() {
                Log.i(TAG, "Getting access token")
                var what = WHAT_FETCH_INFO
                try {
                    val url = URL(TOKEN_URL)
                    //URL url = new URL(mTokenUrl + "&code=" + code);
                    Log.i(TAG, "Opening Token URL " + url.toString())
                    val urlConnection = url.openConnection() as HttpURLConnection
                    urlConnection.requestMethod = "POST"
                    urlConnection.doInput = true
                    urlConnection.doOutput = true
                    //urlConnection.connect();
                    val writer = OutputStreamWriter(urlConnection.outputStream)
                    writer.write("client_id=" + mClientId +
                            "&client_secret=" + mClientSecret +
                            "&grant_type=authorization_code" +
                            "&redirect_uri=" + mCallbackUrl +
                            "&code=" + code)
                    writer.flush()
                    val response = streamToString(urlConnection.inputStream)
                    Log.i(TAG, "response " + response)
                    val jsonObj = JSONTokener(response).nextValue() as JSONObject
                    mAccessToken = jsonObj.getString("access_token")
                    Log.i(TAG, "Got access token: " + mAccessToken!!)
                    val id = jsonObj.getJSONObject("user").getString("id")
                    val user = jsonObj.getJSONObject("user").getString("username")
                    val name = jsonObj.getJSONObject("user").getString("full_name")
                    mSession.storeAccessToken(mAccessToken, id, user, name)
                } catch (ex: Exception) {
                    what = WHAT_ERROR
                    ex.printStackTrace()
                }

                mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0))
            }
        }.start()
    }

    private fun fetchUserName() {
        mProgress.setMessage("Finalizing ...")
        object : Thread() {
            override fun run() {
                Log.i(TAG, "Fetching user info")
                var what = WHAT_FINALIZE
                try {
                    val url = URL(API_URL + "/users/" + mSession.getId() + "/?access_token=" + mAccessToken)

                    Log.d(TAG, "Opening URL " + url.toString())
                    val urlConnection = url.openConnection() as HttpURLConnection
                    urlConnection.requestMethod = "GET"
                    urlConnection.doInput = true
                    urlConnection.connect()
                    val response = streamToString(urlConnection.inputStream)
                    println(response)
                    val jsonObj = JSONTokener(response).nextValue() as JSONObject
                    val name = jsonObj.getJSONObject("data").getString("full_name")
                    val bio = jsonObj.getJSONObject("data").getString("bio")
                    Log.i(TAG, "Got name: $name, bio [$bio]")
                } catch (ex: Exception) {
                    what = WHAT_ERROR
                    ex.printStackTrace()
                }

                mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0))
            }
        }.start()
    }

    fun hasAccessToken(): Boolean {
        return if (mAccessToken == null) false else true
    }

    fun setListener(listener: OAuthAuthenticationListener) {
        mListener = listener
    }

    fun authorize() {
        //Intent webAuthIntent = new Intent(Intent.ACTION_VIEW);
        //webAuthIntent.setData(Uri.parse(AUTH_URL));
        //mCtx.startActivity(webAuthIntent);
        mDialog.show()
    }

    @Throws(IOException::class)
    private fun streamToString(`is`: InputStream?): String {
        var str = ""

        if (`is` != null) {
            val sb = StringBuilder()
            var line: String

            try {
                val reader = BufferedReader(
                        InputStreamReader(`is`))

                while ((line = reader.readLine()) != null) sb.append(line)

                reader.close()
            } finally {
                `is`.close()
            }

            str = sb.toString()
        }

        return str
    }

    fun resetAccessToken() {
        if (mAccessToken != null) {
            mSession.resetAccessToken()
            mAccessToken = null
        }
    }

    interface OAuthAuthenticationListener {
        fun onSuccess()

        fun onFail(error: String)
    }

    companion object {

        private val WHAT_FINALIZE = 0
        private val WHAT_ERROR = 1
        private val WHAT_FETCH_INFO = 2

        /**
         * Callback url, as set in 'Manage OAuth Costumers' page
         * (https://developer.github.com/)
         */

        var mCallbackUrl = ""
        private val AUTH_URL = "https://api.instagram.com/oauth/authorize/"
        private val TOKEN_URL = "https://api.instagram.com/oauth/access_token"
        private val API_URL = "https://api.instagram.com/v1"

        private val TAG = "InstagramAPI"
    }
}
