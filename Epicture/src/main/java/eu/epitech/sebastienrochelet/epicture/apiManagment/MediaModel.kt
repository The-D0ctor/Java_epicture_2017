package eu.epitech.sebastienrochelet.epicture.apiManagment

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

/**
 * Created by sebastienrochelet on 18/02/2018.
 */

//class representing the media
class MediaModel() : Parcelable {
    var id: String? = null
    var imageUrl: String? = null
    var title: String? = null
    var user: UserModel? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        imageUrl = parcel.readString()
        title = parcel.readString()
        user = parcel.readParcelable(UserModel::class.java.classLoader)
    }

    constructor(json: JSONObject) : this() {
        id = json["id"] as String
        imageUrl = ((json["images"] as JSONObject)["standard_resolution"] as JSONObject)["url"] as String
        title = (json["caption"] as JSONObject)["text"] as String
        user = UserModel(json["user"] as JSONObject)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(imageUrl)
        parcel.writeString(title)
        parcel.writeParcelable(user, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MediaModel> {
        override fun createFromParcel(parcel: Parcel): MediaModel {
            return MediaModel(parcel)
        }

        override fun newArray(size: Int): Array<MediaModel?> {
            return arrayOfNulls(size)
        }
    }
}