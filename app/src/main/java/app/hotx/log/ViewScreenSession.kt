package app.hotx.log

import app.hotx.app.App
import javax.inject.Inject

/**
 * Created by Grigory Azaryan on 2019-01-30.
 */

class ViewScreenSession(var screenName: String) {
    private var startTime: Long = 0
    var duration: Long = 0
    var origin: String? = null

    @Inject
    lateinit var analytics: Analytics

    init {
        App.getAppComponent().inject(this)
    }

    fun start() {
        startTime = System.currentTimeMillis()
    }

    fun stop() {
        if (startTime > 0) {
            duration = System.currentTimeMillis() - startTime
            analytics.logViewScreen(this)
        }
        startTime = 0
        origin = null
    }
}