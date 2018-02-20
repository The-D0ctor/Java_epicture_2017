package eu.epitech.sebastienrochelet.epicture.fragments

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import eu.epitech.sebastienrochelet.epicture.R
import eu.epitech.sebastienrochelet.epicture.apiManagment.MediaModel
import eu.epitech.sebastienrochelet.epicture.fragments.FeedFragment.OnListFragmentInteractionListener

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */

//adapted for the feed fragment
class MyFeedRecyclerViewAdapter(private val mValues: List<MediaModel?>, private val mListener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<MyFeedRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_feed, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        Glide.with(holder.mMediaImage).load(mValues[position]!!.imageUrl).into(holder.mMediaImage)
        holder.mTitleMedia.text = mValues[position]!!.title

        holder.mView.setOnClickListener {
            mListener?.onListFragmentInteraction(holder.mItem!!)
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mMediaImage: ImageView = mView.findViewById(R.id.media_image) as ImageView
        val mTitleMedia: TextView = mView.findViewById(R.id.media_title) as TextView
        var mItem: MediaModel? = null
    }
}
