package app.hotx.helper

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import app.hotx.R

/**
 * Created by Grigory Azaryan on 11/12/18.
 */
class Toolbar {
    val title: TextView
    val navigationButton: ImageView
    val searchView: SearchView?

    private constructor(builder: Builder) {
        val root = builder.layout!!
        title = root.findViewById(R.id.toolbar_title)
        navigationButton = root.findViewById(R.id.toolbar_logo)

        title.text = builder.title

        searchView = builder.searchView?.also {
            it.visibility = View.VISIBLE
            it.setOnCloseListener {
                false
            }
            it.setOnSearchClickListener { }
        }

        if (builder.navigationButtonResId == -1) {
            navigationButton.visibility = View.GONE
        } else {
            navigationButton.setImageResource(builder.navigationButtonResId)
            navigationButton.setOnClickListener { builder.navigationButtonListener?.invoke() }
        }
    }

    class Builder {
        var layout: View? = null
            private set
        var context: Activity? = null
            private set
        var title = ""
            private set
        var navigationButtonResId = -1
            private set
        var navigationButtonListener: (() -> Unit)? = null
            private set
        var searchView: SearchView? = null
            private set

        fun create() = Toolbar(this)

        fun withLayout(layout: View) = apply { this.layout = layout }

        fun setTitle(title: String) = apply { this.title = title }

        fun setNavigationButton(iconResId: Int, listener: (() -> Unit)? = null) = apply {
            this.navigationButtonResId = iconResId
            this.navigationButtonListener = listener
        }

        fun setSearchView(searchView: SearchView): Builder = apply { this.searchView = searchView }

    }
}