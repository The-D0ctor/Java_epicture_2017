package eu.epitech.sebastienrochelet.epicture.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import eu.epitech.sebastienrochelet.epicture.R
import eu.epitech.sebastienrochelet.epicture.apiManagment.UserModel


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

//fragment for the home
class HomeFragment : Fragment() {

    private var user: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            user = arguments!!.getParcelable("user")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        view.findViewById<TextView>(R.id.full_name_home).text = user!!.fullName
        view.findViewById<TextView>(R.id.username_home).text = user!!.username
        Glide.with(this).load(user!!.profilPicture).into(view.findViewById(R.id.profile_image_home))
        return view
    }

    companion object {
        fun newInstance(user: UserModel): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putParcelable("user", user)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
