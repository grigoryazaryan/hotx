package app.hotx.social

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
    fun provideRedditClient(): RedditClient = ManagerRedditClient()

}