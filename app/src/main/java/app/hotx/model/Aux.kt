package app.hotx.model

/**
 * Created by Grigory Azaryan on 11/6/18.
 */


class LikeDislikeResponse {
    var state: Int = 0
    var likes: Int = 0
    var dislikes: Int = 0
}

class AdditionalFilterEntry {
    var title: String = ""
    var values: List<String> = emptyList()
}

class OrderFilterEntry {
    var orderKey = ""
    var filterKey = ""
    var orderTitle: String = ""
    var filterValues: List<Pair<String, String>> = emptyList()
}

class AutocompletePornstarEntry {
    var slug = ""
    var name = ""
    var rank = 0
}

class RegisterResponse {
    var token:String? = null
    var notSupportedCategories = emptyList<String>()
}