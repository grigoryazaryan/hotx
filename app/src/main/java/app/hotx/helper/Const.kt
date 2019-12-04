package app.hotx.helper

/**
 * Created by Grigory Azaryan on 10/25/18.
 */

class Const {

    companion object {
        const val KEY_AUTH_TOKEN = "auth_token"
        const val KEY_VIDEO_OBJECT = "video_object"
        const val KEY_VIDEO_VKEY = "video_vkey"
        const val KEY_UNIQUE_DEVICE_ID = "unique_device_id"
        const val KEY_PREFERRED_VIDEO_QUALITY = "preferred_video_quality"
        const val KEY_STAR = "star"
        const val KEY_TAG = "tag"
        const val KEY_CATEGORY_ID = "category_id"
        const val KEY_PASSWORD = "password"
        const val KEY_AUTO_FULLSCREEN = "auto_fullscreen"
        const val KEY_FAST_FORWARD = "fast_forward"
        const val KEY_FAST_REWIND = "fast_rewind"
        const val KEY_TITLE = "title"
        const val KEY_PRODUCTION = "production"
        const val KEY_DURATION = "min_duration"
        const val KEY_QUALITY = "hd"

        const val TAG_FRAGMENT_SEARCH = "tag_search"
        const val TAG_FRAGMENT_VIDEO = "tag_video"
        const val TAG_FRAGMENT_STARS_ALL = "tag_stars_all"
        const val TAG_FRAGMENT_RECOMMENDED = "tag_recommended"
        const val TAG_FRAGMENT_FAVORITES = "tag_favorites"
        const val TAG_FRAGMENT_CATEGORIES = "tag_categories"
        const val TAG_FRAGMENT_PROFILE = "tag_profile"
        const val TAG_DIALOG_PIN = "tag_dialog_pin"


        const val PHVideoBaseUrl = "https://www.pornhub.com/view_video.php?viewkey="
        const val PHAppKey = "72d2512a43364263e9d94f0f73"
        const val ORDER_MOST_RECENT = "mr" // Recently Featured
        const val ORDER_MOST_VIEWED = "mv" // Most Viewed
        const val ORDER_TOP_RATED = "tr" // Top Rated
        const val ORDER_HOTTEST = "ht" // hottest
        const val ORDER_LONGEST = "lg" // longest
        const val ORDER_NEWEST = "cm" // newest // including vr videos

        const val FILTER_ALL = "a" // all time
        const val FILTER_MONTH = "m" // monthly
        const val FILTER_WEEK = "w" // weekly
        const val FILTER_DAY = "t" // daily

        const val PRODUCTION_PROFESSIONAL = "professional"
        const val PRODUCTION_HOMEMADE = "homemade"

        const val USER_VIDEOS_FAVORITE = "faves"
        const val USER_VIDEOS_HISTORY = "history"

        const val STATE_EXPANDED = 3
        const val STATE_COLLAPSED = 4

        const val SEARCH_VIDEO = "video"
        const val SEARCH_PORNSTAR = "pornstar"
    }
}