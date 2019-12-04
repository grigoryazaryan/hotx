package app.hotx.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.hotx.R
import app.hotx.adapter.MainVideoListRecyclerAdapter
import app.hotx.app.App
import app.hotx.eventbus.Event
import app.hotx.eventbus.RxBus
import app.hotx.helper.*
import app.hotx.helper.Toolbar
import app.hotx.log.Analytics
import app.hotx.model.Pornstar
import app.hotx.networking.PHResponseParser
import app.hotx.networking.PHWebService
import app.hotx.networking.PHWebServiceCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.objectbox.BoxStore
import kotlinx.android.synthetic.main.fragment_star.view.*
import java.util.*
import javax.inject.Inject

class StarFragment : Fragment() {

    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var recyclerAdapter: MainVideoListRecyclerAdapter
    private lateinit var endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener
    private var rootView: View? = null

    private var orderSpinner: Spinner? = null
    private var starCoverPhoto: ImageView? = null
    private var subscribers: TextView? = null

    private var searchStar: String? = null
    private var order: String? = null

    private var star: Pornstar? = null

    @Inject
    lateinit var categories: Categories
    @Inject
    lateinit var phWebService: PHWebService
    @Inject
    lateinit var phResponseParser: PHResponseParser
    @Inject
    lateinit var utils: Utils
    @Inject
    lateinit var boxStore: BoxStore

    // Required empty public constructor
    init {
        App.getAppComponent().inject(this)
    }

    companion object {
        @JvmStatic
        fun newInstance(arguments: Bundle?) =
                StarFragment().apply {
                    this.arguments = arguments
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)

        searchStar = arguments?.getString(Const.KEY_STAR)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView != null) return rootView
        val view = inflater.inflate(R.layout.fragment_star, container, false)

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        recyclerView = view.findViewById(R.id.recycler_view)
        orderSpinner = view.findViewById(R.id.order_spinner)
        starCoverPhoto = view.star_photo
        subscribers = view.subscribers

        swipeRefreshLayout!!.setProgressViewOffset(false, 0, 200)
        swipeRefreshLayout!!.setOnRefreshListener { loadPage(1, true) }

        val orderKeys = arrayOf(Const.ORDER_MOST_RECENT, Const.ORDER_TOP_RATED, Const.ORDER_MOST_VIEWED, Const.ORDER_NEWEST, Const.ORDER_LONGEST)
        val orderNames = arrayOf(getString(R.string.recently_featured), getString(R.string.top_rated), getString(R.string.most_viewed), getString(R.string.newest), getString(R.string.longest))
        orderSpinner!!.adapter = ArrayAdapter(activity!!, R.layout.simple_drop_down_item, orderNames)
        orderSpinner!!.post {
            orderSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    order = orderKeys[position]
                    loadPage(1, true)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        }

        recyclerAdapter = MainVideoListRecyclerAdapter(context!!, ArrayList())

        setupRecycler()

        loadPage(1, true)

        rootView = view
        return view
    }

    private fun setupRecycler() {
        recyclerView!!.addOnItemTouchListener(RecyclerTouchListener(context, recyclerView, object : RecyclerTouchListener.ClickListener {
            override fun onClick(view: View, position: Int) {
                val video = recyclerAdapter.getItem(position)
                RxBus.publish(Event.VideoItemClick(video, Analytics.Value.STAR))
            }

            override fun onLongClick(view: View, position: Int) {
                val video = recyclerAdapter.getItem(position)
                recyclerAdapter.startPreview(video, position)
            }
        }))
        recyclerView!!.adapter = recyclerAdapter

        val layoutManager = GridLayoutManager(context, 2)
        recyclerView!!.layoutManager = layoutManager
        endlessRecyclerViewScrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadPage(page, false)
            }
        }
        recyclerView!!.addOnScrollListener(endlessRecyclerViewScrollListener)
    }

    internal fun loadPage(page: Int, shouldClear: Boolean) {
        val slug = searchStar!!.replace(" ", "-")
        phWebService.getPornstar(Const.PHAppKey, utils.deviceID, EndlessRecyclerViewScrollListener.PAGE_SIZE,
                (page - 1) * EndlessRecyclerViewScrollListener.PAGE_SIZE, order, slug)
                .enqueue(PHWebServiceCallback { response ->
                    swipeRefreshLayout!!.isRefreshing = false
                    if (!response.isSuccess) {
                        recyclerAdapter.clear()
                        endlessRecyclerViewScrollListener.resetState()
                        return@PHWebServiceCallback
                    }

                    val videos = phResponseParser.parseVideos(response.data)
                    if (star == null) {
                        star = phResponseParser.parsePornstar(response.data)
                        setStarData(star!!)
                    }

                    if (shouldClear) {
                        recyclerAdapter.clear()
                        endlessRecyclerViewScrollListener.resetState()
                    }

                    val notPremiumVideos = videos.filter { v -> !v.isPremium }
                    recyclerAdapter.addItems(notPremiumVideos)
                })
    }

    private fun setStarData(star: Pornstar) {
        view?.let {
            Glide.with(context!!)
                    .load(if (!star.cover.isEmpty()) star.cover else star.thumb)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(it.star_photo)

            Toolbar.Builder().withLayout(it.toolbar)
                    .setTitle(getString(R.string.star) + ": " + star.name)
                    .setNavigationButton(R.drawable.ic_back_arrow) { activity!!.onBackPressed() }
                    .create()

            it.videos_number.text = star.numberOfVideos.toString()
            it.views.text = star.views
            it.subscribers.text = star.subscribers
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
//        menu!!.clear()
//        super.onCreateOptionsMenu(menu, inflater)
//    }

}
