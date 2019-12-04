package app.hotx.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.hotx.R
import app.hotx.model.Pornstar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.util.*

/**
 * Created by Grigory Azaryan on 11/1/18.
 */

class StarsAllAdapter(private var context: Context) : RecyclerView.Adapter<StarsAllAdapter.ViewHolder>() {

    private val recyclerView: RecyclerView? = null
    private val data = ArrayList<Pornstar>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_stars_all_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val star = data[position]

        viewHolder.title.text = star.name

        Glide.with(context)
                .load(star.thumb)
                //                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .into(viewHolder.preview)
    }


    fun clear() {
        val count = data.size
        data.clear()
        notifyItemRangeRemoved(0, count)
    }

    fun addItems(items: List<Pornstar>) {
        val positionStart = data.size
        data.addAll(items)
        notifyItemRangeInserted(positionStart, items.size)
    }

    fun getItem(position: Int): Pornstar {
        return data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }


    inner class ViewHolder internal constructor(root: View) : RecyclerView.ViewHolder(root) {
        internal var title: TextView = root.findViewById(R.id.title)
        internal var preview: ImageView = root.findViewById(R.id.preview)
    }
}