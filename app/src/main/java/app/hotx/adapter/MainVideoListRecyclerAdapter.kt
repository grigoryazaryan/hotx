package app.hotx.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.hotx.R
import app.hotx.app.App
import app.hotx.helper.Utils
import app.hotx.helper.VideoPreviewHelper
import app.hotx.model.PHSmallVideo
import app.hotx.model.PHSmallVideo_
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.exoplayer2.ui.PlayerView
import io.objectbox.BoxStore
import kotlinx.android.synthetic.main.item_main_videos_list.view.*
import java.util.*
import javax.inject.Inject

/**
 * Created by Grigory Azaryan on 2019-02-01.
 */


class MainVideoListRecyclerAdapter(private val context: Context, private val data: MutableList<PHSmallVideo>) :
        RecyclerView.Adapter<MainVideoListRecyclerAdapter.ViewHolder>() {

    private var recyclerView: RecyclerView? = null
    private var currentPlayingPreview: VideoPreviewHelper? = null

    @Inject
    lateinit var boxStore: BoxStore

    init {
        App.getAppComponent().inject(this)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_main_videos_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) = viewHolder.bind(data[position])

    fun startPreview(video: PHSmallVideo, position: Int) {
        val viewHolder = recyclerView!!.findViewHolderForAdapterPosition(position) as MainVideoListRecyclerAdapter.ViewHolder?
        viewHolder?.apply {

            currentPlayingPreview?.let {
                if (it.video.vkey == video.vkey)
                    return
                it.release()
            }

            previewVideo?.apply {
                currentPlayingPreview = VideoPreviewHelper(context, previewVideo!!, video).apply { start() }
            }
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        val position = holder.adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            val video = data[position]
            currentPlayingPreview?.takeIf { it.video.vkey == video.vkey }?.let {
                it.release()
            }
        }
        super.onViewRecycled(holder)
    }

    fun clear() {
        val count = data.size
        data.clear()
        notifyItemRangeRemoved(0, count)
    }

    fun addItems(items: List<PHSmallVideo>) {
        val positionStart = data.size
        data.addAll(items)
        notifyItemRangeInserted(positionStart, items.size)
    }

    fun getItem(position: Int): PHSmallVideo {
        return data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        var previewVideo: PlayerView? = null

        fun bind(video: PHSmallVideo): Unit = with(view) {

            title.text = video.title
            duration.text = Utils.formatSecondsToTime(video.duration.toLong())
            view_count.text = Utils.formatDecimal(video.viewCount.toDouble())
            rating.text = String.format(Locale.ENGLISH, "%d%s", video.rating, '%')

            val isSaved = boxStore.boxFor(PHSmallVideo::class.java).query().equal(PHSmallVideo_.vkey, video.vkey).build().findFirst() != null
            saved.visibility = if (isSaved) View.VISIBLE else View.GONE

            Glide.with(context)
                    .load(video.urlThumbnail)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(preview)

            previewVideo = preview_video
        }

    }
}