package app.hotx.social

import app.hotx.model.PHSmallVideo

/**
 * Created by Grigory Azaryan on 2019-02-08.
 */

interface RedditClient {
    fun submitVideo(video: PHSmallVideo)
}