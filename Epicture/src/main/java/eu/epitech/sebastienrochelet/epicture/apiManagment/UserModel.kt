package eu.epitech.sebastienrochelet.epicture.apiManagment

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

/**
 * Created by sebastienrochelet on 11/02/2018.
 */

class UserModel() : Parcelable {
    var id: String? = null
    var username: String? = null
    var profilPicture: String? = null
    var fullName: String? = null
    var bio: String? = null
    var website: String? = null
    var media: String? = null
    var follows: String? = null
    var followedBy: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        username = parcel.readString()
        profilPicture = parcel.readString()
        fullName = parcel.readString()
        bio = parcel.readString()
        website = parcel.readString()
        media = parcel.readString()
        follows = parcel.readString()
        followedBy = parcel.readString()
    }

    constructor(json: JSONObject) : this() {
        id = json["id"].toString()
        username = json["username"].toString()
        profilPicture = json["profile_picture"].toString()
        fullName = json["full_name"].toString()
        bio = json["bio"].toString()
        website = json["website"].toString()
        val counts = json["counts"] as JSONObject
        media = counts["media"].toString()
        follows = counts["follows"].toString()
        followedBy = counts["followed_by"].toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(username)
        parcel.writeString(profilPicture)
        parcel.writeString(fullName)
        parcel.writeString(bio)
        parcel.writeString(website)
        parcel.writeString(media)
        parcel.writeString(follows)
        parcel.writeString(followedBy)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserModel> {
        override fun createFromParcel(parcel: Parcel): UserModel {
            return UserModel(parcel)
        }

        override fun newArray(size: Int): Array<UserModel?> {
            return arrayOfNulls(size)
        }
    }

}