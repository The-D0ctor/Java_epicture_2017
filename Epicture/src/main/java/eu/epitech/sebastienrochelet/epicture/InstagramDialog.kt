package eu.epitech.sebastienrochelet.epicture

import android.support.test.espresso.web.model.Atoms.getTitle
import android.webkit.WebView
import android.graphics.Bitmap
import android.webkit.WebViewClient
import android.graphics.Typeface
import android.widget.TextView
import android.view.Window.FEATURE_NO_TITLE
import android.webkit.CookieSyncManager
import android.widget.FrameLayout
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.view.Display
import android.widget.LinearLayout
import android.app.ProgressDialog
import android.os.Bundle
import android.view.ViewGroup



/**
 * Created by nathan on 06/02/2018.
 */
class InstagramDialog(context: Context, private val mUrl: String, private val mListener: OAuthDialogListener) : Dialog(context) {
    private var mSpinner: ProgressDialog? = null
    private var mWebView: WebView? = null
    private var mContent: LinearLayout? = null
    private var mTitle: TextView? = null
    protected fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        mSpinner = ProgressDialog(getContext())
        mSpinner!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mSpinner!!.setMessage("Loading...")
        mContent = LinearLayout(getContext())
        mContent!!.orientation = LinearLayout.VERTICAL
        setUpTitle()
        setUpWebView()
        val display = getWindow().getWindowManager().getDefaultDisplay()
        val scale = getContext().getResources().getDisplayMetrics().density
        val dimensions = if (display.getWidth() < display.getHeight()) DIMENSIONS_PORTRAIT else DIMENSIONS_LANDSCAPE
        addContentView(mContent, FrameLayout.LayoutParams((dimensions[0] * scale + 0.5f).toInt(), (dimensions[1] * scale + 0.5f).toInt()))
        CookieSyncManager.createInstance(getContext())
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookie()
    }

    private fun setUpTitle() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        mTitle = TextView(getContext())
        mTitle!!.text = "Instagram"
        mTitle!!.setTextColor(Color.WHITE)
        mTitle!!.typeface = Typeface.DEFAULT_BOLD
        mTitle!!.setBackgroundColor(Color.BLACK)
        mTitle!!.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN)
        mContent!!.addView(mTitle)
    }

    private fun setUpWebView() {
        mWebView = WebView(getContext())
        mWebView!!.isVerticalScrollBarEnabled = false
        mWebView!!.isHorizontalScrollBarEnabled = false
        mWebView!!.webViewClient = OAuthWebViewClient()
        mWebView!!.settings.javaScriptEnabled = true
        mWebView!!.loadUrl(mUrl)
        mWebView!!.layoutParams = FILL
        mContent!!.addView(mWebView)
    }

    private inner class OAuthWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            Log.d(TAG, "Redirecting URL " + url)
            if (url.startsWith(InstagramApp.mCallbackUrl)) {
                val urls = url.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                mListener.onComplete(urls[1])
                this@InstagramDialog.dismiss()
                return true
            }
            return false
        }

        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            Log.d(TAG, "Page error: " + description)
            super.onReceivedError(view, errorCode, description, failingUrl)
            mListener.onError(description)
            this@InstagramDialog.dismiss()
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
            Log.d(TAG, "Loading URL: " + url)
            super.onPageStarted(view, url, favicon)
            mSpinner!!.show()
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            val title = mWebView!!.title
            if (title != null && title.length > 0) {
                mTitle!!.text = title
            }
            Log.d(TAG, "onPageFinished URL: " + url)
            mSpinner!!.dismiss()
        }
    }

    interface OAuthDialogListener {
        fun onComplete(accessToken: String)
        fun onError(error: String)
    }

    companion object {
        internal val DIMENSIONS_LANDSCAPE = floatArrayOf(460f, 260f)
        internal val DIMENSIONS_PORTRAIT = floatArrayOf(280f, 420f)
        internal val FILL = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT)
        internal val MARGIN = 4
        internal val PADDING = 2
        private val TAG = "Instagram-WebView"
    }
}