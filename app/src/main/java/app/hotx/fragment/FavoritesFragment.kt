package app.hotx.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import app.hotx.R
import app.hotx.adapter.MainVideoListRecyclerAdapter
import app.hotx.app.App
import app.hotx.eventbus.Event
import app.hotx.eventbus.RxBus
import app.hotx.helper.Const
import app.hotx.helper.RecyclerTouchListener
import app.hotx.helper.Toolbar
import app.hotx.helper.Utils
import app.hotx.log.Analytics
import app.hotx.model.PHSmallVideo
import app.hotx.networking.PHResponseParser
import app.hotx.networking.PHWebService
import app.hotx.networking.PHWebServiceCallback
import app.hotx.networking.WebService
import com.google.gson.JsonObject
import io.objectbox.BoxStore
import kotlinx.android.synthetic.main.fragment_favorites.view.*
import retrofit2.Call
import javax.inject.Inject

/**
 * Created by Grigory Azaryan on 11/8/18.
 */

class FavoritesFragment : Fragment() {

    private var rootView: View? = null

    private lateinit var recyclerAdapter: MainVideoListRecyclerAdapter

    @Inject
    lateinit var boxStore: BoxStore
    @Inject
    lateinit var webService: WebService
    @Inject
    lateinit var phWebService: PHWebService
    @Inject
    lateinit var phResponseParser: PHResponseParser
    @Inject
    lateinit var utils: Utils

    companion object {
        @JvmStatic
        fun getInstance(arguments: Bundle?) =
                FavoritesFragment().apply {
                    this.arguments = arguments
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.getAppComponent().inject(this)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView?.let { return it }
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        Toolbar.Builder().withLayout(view.toolbar)
                .setTitle(getString(R.string.favorites))
                .setNavigationButton(R.drawable.ic_back_arrow) { activity!!.onBackPressed() }
                .create()

        view.swipe_refresh_layout.setProgressViewOffset(false, 0, 200)
        view.swipe_refresh_layout.setOnRefreshListener {
            Handler().postDelayed({
                view.swipe_refresh_layout.isRefreshing = false
                recyclerAdapter.clear()
                val data = boxStore.boxFor(PHSmallVideo::class.java).all
                data.reverse()
                recyclerAdapter.addItems(data)
            }, 1000)
        }

        val data = boxStore.boxFor(PHSmallVideo::class.java).all
        data.reverse()
        recyclerAdapter = MainVideoListRecyclerAdapter(context!!, data)
        view.recycler_view.adapter = recyclerAdapter
        view.recycler_view.layoutManager = GridLayoutManager(activity, 2)
        view.recycler_view.addOnItemTouchListener(RecyclerTouchListener(context, view.recycler_view, object : RecyclerTouchListener.ClickListener {
            var lastClickedPosition = -1
            var lastCall: Call<JsonObject>? = null
            override fun onClick(view: View, position: Int) {
                val video = recyclerAdapter.getItem(position)
                RxBus.publish(Event.VideoItemClick(video, Analytics.Value.FAVORITES))
            }

            override fun onLongClick(view: View, position: Int) {
                if (lastClickedPosition == position) return

                lastCall?.takeUnless { it.isExecuted }?.let { it.cancel() }

                val video = recyclerAdapter.getItem(position)

                lastCall = phWebService.getVideo(Const.PHAppKey, utils.deviceID, video.vkey)
                lastCall?.enqueue(PHWebServiceCallback { response ->
                    if (!response.isSuccess) return@PHWebServiceCallback

                    val videoDetails = phResponseParser.parseVideo(response.data)

                    recyclerAdapter.startPreview(PHSmallVideo(videoDetails), position)

                    lastClickedPosition = position
                })
            }
        }))

        rootView = view
        return rootView
    }
}