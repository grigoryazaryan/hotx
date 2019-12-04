package app.hotx.model

import java.io.Serializable

/**
 * Created by Grigory Azaryan on 10/24/18.
 */

data class Pornstar(var slug: String,
                    var name: String,
                    var thumb: String,
                    var rank: Int,
                    var views: String,
                    var numberOfVideos: Int,
                    var rating: String,
                    var isPremium: Boolean,
                    var bio: String,
                    var subscribers: String,
                    var cover: String, // wide photo
                    var previuosPornstarSlug: String,
                    var nextPornstarSlug: String
) : Serializable