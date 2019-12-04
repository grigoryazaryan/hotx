package app.hotx.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.hotx.R
import kotlinx.android.synthetic.main.simple_drop_down_item.view.*

/**
 * Created by Grigory Azaryan on 11/14/18.
 */

class SimpleRecyclerAdapter(val context: Context, val items: List<String>) : RecyclerView.Adapter<SimpleRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.simple_drop_down_item, null))
    }

    override fun getItemCount(): Int {
        return this.items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = items[position]
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text = view.text1
    }
}