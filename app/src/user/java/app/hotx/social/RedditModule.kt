package app.hotx.social

import app.hotx.model.PHSmallVideo
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Grigory Azaryan on 2019-02-08.
 */

@Module
class RedditModule {

    @Provides
    @Singleton
    fun provideRedditClient(): RedditClient = object : RedditClient {
        override fun submitVideo(video: PHSmallVideo) {}
    }

}