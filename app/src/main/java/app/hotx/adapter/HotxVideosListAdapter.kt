package app.hotx.adapter

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import app.hotx.R
import app.hotx.app.App
import app.hotx.helper.GifFromImages
import app.hotx.helper.Utils
import app.hotx.model.VideoObject
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.objectbox.BoxStore
import java.util.*
import javax.inject.Inject

/**
 * Created by Grigory Azaryan on 10/28/18.
 */

public class HotxVideosListAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<HotxVideosListAdapter.ViewHolder> {
    internal val TAG = "VideoListRecycleAdapter"

    private var recyclerView: androidx.recyclerview.widget.RecyclerView? = null
    private val context: Context?
    private val data: MutableList<VideoObject>
    //    private PlayPauseGif playingGif;
    private var playingGif: GifFromImages? = null

    @Inject
    lateinit var boxStore: BoxStore
    @Inject
    lateinit var prefs: SharedPreferences

    constructor(context: Context?, data: MutableList<VideoObject>) {
        App.getAppComponent().inject(this)
        this.context = context
        this.data = data
    }

    override fun onAttachedToRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_main_videos_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val video = data[position]

        viewHolder.title.text = video.name
        viewHolder.duration.text = Utils.formatSecondsToTime(video.duration.toLong())
        viewHolder.viewsCount.text = Utils.formatDecimal(video.views.toDouble())
        viewHolder.rating.text = String.format(Locale.ENGLISH, "%d%s", video.likes, '%')

        viewHolder.saved.visibility = View.GONE

        viewHolder.gifPreview = GifFromImages(context, viewHolder.preview, video.thumbs)

        context!!.run {
            Glide.with(context)
                    .load(video.previewLink)
                    .transition(DrawableTransitionOptions.withCrossFade())
//                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(viewHolder.preview)
        }
    }

    internal fun startPreview(position: Int) {
        val viewHolder: ViewHolder = recyclerView!!.findViewHolderForAdapterPosition(position) as ViewHolder

        if (viewHolder.gifPreview.isPlaying) {
            viewHolder.gifPreview.stop()
            return
        }
        playingGif?.stop()

        playingGif = viewHolder.gifPreview
        playingGif?.start()
    }

    fun clear() {
        val count = data.size
        data.clear()
        notifyItemRangeRemoved(0, count)
    }

    fun addItems(items: List<VideoObject>) {
        val positionStart = data.size
        data.addAll(items)
        notifyItemRangeInserted(positionStart, items.size)
    }

    fun getItem(position: Int): VideoObject {
        return data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }


    inner class ViewHolder(root: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(root) {
        internal var title: TextView = root.findViewById(R.id.title)
        internal var duration: TextView = root.findViewById(R.id.duration)
        internal var viewsCount: TextView = root.findViewById(R.id.view_count)
        internal var rating: TextView = root.findViewById(R.id.rating)
        internal var preview: ImageView = root.findViewById(R.id.preview)
        internal var saved: ImageView = root.findViewById(R.id.saved)
        internal lateinit var gifPreview: GifFromImages

    }
}