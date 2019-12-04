package app.hotx.social

import app.hotx.helper.Const
import app.hotx.helper.Utils
import app.hotx.model.PHSmallVideo
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import net.dean.jraw.http.OkHttpNetworkAdapter
import net.dean.jraw.http.UserAgent
import net.dean.jraw.models.SubmissionKind
import net.dean.jraw.oauth.Credentials
import net.dean.jraw.oauth.OAuthHelper

/**
 * Created by Grigory Azaryan on 2019-02-08.
 */

class ManagerRedditClient : RedditClient {
    val redditClient: Observable<net.dean.jraw.RedditClient>

    init {



        redditClient = Observable.fromCallable {
            val userAgent = UserAgent("www.zzz", "www.zzz", "v0.1", "www.zzz")
            val credentials = Credentials.script("www.zzz", "www.zzz", "www.zzz", "www.zzz")

            val adapter = OkHttpNetworkAdapter(userAgent)
            // Authenticate and get a RedditClient instance
            // Use Credentails.script, Credentials.userless, or Credentials.userlessApp
            return@fromCallable OAuthHelper.automatic(adapter, credentials)
        }.subscribeOn(Schedulers.io())
    }
    override fun submitVideo(video: PHSmallVideo) {

        redditClient
                .subscribe({ reddit ->
                    reddit.subreddit("www.zzz")
                            .submit(SubmissionKind.LINK, video.title + " [" + Utils.formatSecondsToTime(video.duration.toLong()) + "]",
                                    Const.PHVideoBaseUrl + video.vkey, true)
                }, { })
    }

}