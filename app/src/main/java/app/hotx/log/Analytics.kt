package app.hotx.log

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Created by Grigory Azaryan on 2019-01-30.
 */

class Analytics(context: Context) {

    object Event {
        const val SEARCH = "search"
        const val VIEW_SCREEN = "view_screen"
        const val SETTINGS_CHANGE = "settings_change"
        const val PROBLEM = "problem"
        const val PH_ERROR = "ph_error"
    }

    object Param {
        const val TYPE = "type"
        const val SEARCH_TERM = "search_term"
        const val SCREEN_NAME = "screen_name"
        const val ORIGIN = "origin"
        const val DURATION = "duration"

        const val CODE = "code"
        const val MESSAGE = "message"
        const val LOCATION = "location"
        const val REASON = "reason"
    }

    object Value {
        const val TYPE_SEARCH_TEXT = "search_text"
        const val TYPE_SEARCH_STAR = "search_star"

        const val MAIN = "main"
        const val SEARCH = "search"
        const val CATEGORIES = "categories"
        const val SELECTED = "selected"
        const val FAVORITES = "favorites"
        const val STARS_ALL = "starsAll"
        const val STAR = "star"
        const val VIDEO = "video"
        const val BROWSER_LINK = "browserLink"

        const val VR_NOT_SUPPORTED = "vr_not_supported"
        const val NO_LINKS = "no_links"

    }


    val analytics: FirebaseAnalytics by lazy(context) {
        FirebaseAnalytics.getInstance(context)
    }

    fun logSearch(searchType: String, searchTerm: String) {
        val bundle = Bundle()
        bundle.putString(Param.TYPE, searchType)
        bundle.putString(Param.SEARCH_TERM, searchTerm)
        analytics.logEvent(Event.SEARCH, bundle)
    }

    fun logViewScreen(session: ViewScreenSession) {
        val bundle = Bundle()
        bundle.putString(Param.SCREEN_NAME, session.screenName)
        bundle.putLong(Param.DURATION, session.duration)
        session.origin?.let { bundle.putString(Param.ORIGIN, it) }
        analytics.logEvent(Event.VIEW_SCREEN, bundle)
    }

    fun logPhError(code: Int, message: String) {
        val bundle = Bundle()
        bundle.putInt(Param.CODE, code)
        bundle.putString(Param.MESSAGE, message)
        analytics.logEvent(Event.PH_ERROR, bundle)
    }

    fun logProblem(problem: Problem) {
        val bundle = Bundle()
        bundle.putString(Param.LOCATION, problem.location)
        bundle.putString(Param.REASON, problem.reason)
        bundle.putString(Param.MESSAGE, problem.message)
        analytics.logEvent(Event.PROBLEM, bundle)
    }

    fun logSettingsChange(bundle: Bundle) {
        analytics.logEvent(Event.SETTINGS_CHANGE, bundle)
    }
}