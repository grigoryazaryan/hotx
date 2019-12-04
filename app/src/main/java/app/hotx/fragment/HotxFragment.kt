package app.hotx.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.hotx.R
import app.hotx.adapter.MainVideoListRecyclerAdapter
import app.hotx.app.App
import app.hotx.eventbus.Event
import app.hotx.eventbus.RxBus
import app.hotx.helper.*
import app.hotx.log.Analytics
import app.hotx.model.PHSmallVideo
import app.hotx.networking.*
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_hotx.view.*
import retrofit2.Call
import javax.inject.Inject

class HotxFragment : Fragment(), HostMainFragment.IFragmentContainer {
    private lateinit var endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener
    private lateinit var adapter: MainVideoListRecyclerAdapter
    private var rootView: View? = null

    @Inject
    lateinit var utils: Utils
    @Inject
    lateinit var appHelper: AppHelper
    @Inject
    lateinit var webService: WebService
    @Inject
    lateinit var serverResponseParser: ServerResponseParser
    @Inject
    lateinit var phWebService: PHWebService
    @Inject
    lateinit var phResponseParser: PHResponseParser

    companion object {
        @JvmStatic
        fun newInstance(arguments: Bundle? = null) =
                HotxFragment().apply {
                    this.arguments = arguments
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.getAppComponent().inject(this)
        arguments?.let {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        data?.also { AppHelper.log(this, Utils.toString(it)) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView != null) return rootView
        val view = inflater.inflate(R.layout.fragment_hotx, container, false)

        view.swipe_refresh_layout.setProgressViewOffset(false, 0, 200)
        view.swipe_refresh_layout.setOnRefreshListener { loadPage(1, true) }

        Toolbar.Builder().withLayout(view.toolbar)
                .setTitle(getString(R.string.selected))
                .setNavigationButton(R.drawable.ic_logo_round, null)
                .create()

        adapter = MainVideoListRecyclerAdapter(context!!, ArrayList())
        view.recycler_view.addOnItemTouchListener(RecyclerTouchListener(context, view.recycler_view, object : RecyclerTouchListener.ClickListener {
            var lastClickedPosition = -1
            var lastCall: Call<JsonObject>? = null
            override fun onClick(view: View, position: Int) {
                val video = adapter.getItem(position)
                RxBus.publish(Event.VideoItemClick(video, Analytics.Value.SELECTED))
            }

            override fun onLongClick(view: View, position: Int) {
                if (lastClickedPosition == position) return

                lastCall?.takeUnless { it.isExecuted }?.let { it.cancel() }

                val video = adapter.getItem(position)

                lastCall = phWebService.getVideo(Const.PHAppKey, utils.deviceID, video.vkey)
                lastCall?.enqueue(PHWebServiceCallback { response ->
                    if (!response.isSuccess) return@PHWebServiceCallback

                    val videoDetails = phResponseParser.parseVideo(response.data)

                    adapter.startPreview(PHSmallVideo(videoDetails), position)

                    lastClickedPosition = position
                })
            }
        }))
        view.recycler_view.adapter = adapter

        val layoutManager = GridLayoutManager(context, 2)
        view.recycler_view.layoutManager = layoutManager
        endlessRecyclerViewScrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadPage(page, false)
            }
        }
        view.recycler_view.addOnScrollListener(endlessRecyclerViewScrollListener)

        view.swipe_refresh_layout.isRefreshing = true
        loadPage(1, true)

        rootView = view
        return view
    }

    internal fun loadPage(page: Int, shouldClear: Boolean) {
        webService.getAllVideos(null, page, EndlessRecyclerViewScrollListener.PAGE_SIZE)
                .enqueue(object : WebServiceCallback() {
                    override fun onResponse(response: ServerResponse) {
                        view?.swipe_refresh_layout?.isRefreshing = false
                        if (!response.status) {
                            adapter.clear()
                            endlessRecyclerViewScrollListener.resetState()
                            return
                        }

                        val videos = serverResponseParser.parseVideosList(response).docs
                        if (shouldClear) {
                            adapter.clear()
                            endlessRecyclerViewScrollListener.resetState()
                        }

                        videos.forEach { v -> v.webm = "" }
                        adapter.addItems(videos)
                    }
                })
    }

    override fun addFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit()
    }
}
