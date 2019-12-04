package app.hotx.eventbus

import app.hotx.model.Category
import app.hotx.model.PHSmallVideo
import app.hotx.model.Pornstar

/**
 * Created by Grigory Azaryan on 10/8/18.
 */

class Event {
    class SearchText(@JvmField val searchText: String)
    class SearchStar(@JvmField val star: String)
    class SearchTag(@JvmField val tag: String)
    class SearchCategory(@JvmField val categoryId: String)

    class CategoryItemClick(@JvmField val category: Category)
    class VideoItemClick(@JvmField val video: PHSmallVideo, @JvmField val origin: String)
    class StarItemClick(@JvmField val star: Pornstar)
    class StarTagClick(@JvmField val starTag: String)

    class VideoPanelStateChanged(@JvmField val state: Int)
    class VideoPanelSlideOffsetChanged(@JvmField val offset: Float)
    class VideoPreviewRemoveClick
    class FullscreenSettingsChanged(@JvmField val isAutoFullscreen: Boolean)
    class FastForwardSettingsChanged(@JvmField val forwardSec: Int, @JvmField val rewindSec: Int)

}