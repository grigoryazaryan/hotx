package app.hotx.helper

import android.content.SharedPreferences
import androidx.core.os.bundleOf
import app.hotx.log.Analytics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Grigory Azaryan on 2019-01-22.
 */

@Singleton
class Settings {
    private val preferences: SharedPreferences
    private val analytics: Analytics

    @Inject
    constructor(preferences: SharedPreferences, analytics: Analytics) {
        this.preferences = preferences
        this.analytics = analytics
    }

    var isAutoFullscreen: Boolean
        get() = preferences.getBoolean(Const.KEY_AUTO_FULLSCREEN, true)
        set(value) {
            preferences.edit().putBoolean(Const.KEY_AUTO_FULLSCREEN, value).apply()
            analytics.logSettingsChange(bundleOf(Const.KEY_AUTO_FULLSCREEN to value))
        }
    var password: String?
        get() = preferences.getString(Const.KEY_PASSWORD, null)
        set(value) {
            preferences.edit().putString(Const.KEY_PASSWORD, value).apply()
            analytics.logSettingsChange(bundleOf(Const.KEY_PASSWORD to (value != null)))
        }
    var fastForwardSec: Int
        get() = preferences.getInt(Const.KEY_FAST_FORWARD, 5)
        set(value) {
            preferences.edit().putInt(Const.KEY_FAST_FORWARD, value).apply()
            analytics.logSettingsChange(bundleOf(Const.KEY_FAST_FORWARD to value))
        }
    var fastRewindSec
        get() = preferences.getInt(Const.KEY_FAST_REWIND, 5)
        set(value) {
            preferences.edit().putInt(Const.KEY_FAST_REWIND, value).apply()
            analytics.logSettingsChange(bundleOf(Const.KEY_FAST_REWIND to value))
        }
    var preferredQuality: String?
        get() = preferences.getString(Const.KEY_PREFERRED_VIDEO_QUALITY, null)
        set(value) {
            preferences.edit().putString(Const.KEY_PREFERRED_VIDEO_QUALITY, value).apply()
            analytics.logSettingsChange(bundleOf(Const.KEY_PREFERRED_VIDEO_QUALITY to value))
        }


    fun isPasswordUsed(): Boolean = !password.isNullOrEmpty()
}