package eu.epitech.sebastienrochelet.epicture.apiManagment

import org.json.JSONObject

/**
 * Created by sebastienrochelet on 18/02/2018.
 */
class MediaModel() {
    var id: String? = null
    var imageUrl: String? = null
    var title: String? = null

    constructor(json: JSONObject) : this() {
        id = json["id"] as String
        imageUrl = ((json["images"] as JSONObject)["standard_resolution"] as JSONObject)["url"] as String
        title = (json["caption"] as JSONObject)["text"] as String
    }
}