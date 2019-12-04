package app.hotx.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.hotx.R
import app.hotx.adapter.MainVideoListRecyclerAdapter
import app.hotx.app.App
import app.hotx.dialog.FiltersDialogBuilder
import app.hotx.eventbus.Event
import app.hotx.eventbus.RxBus
import app.hotx.helper.*
import app.hotx.log.Analytics
import app.hotx.log.ViewScreenSession
import app.hotx.networking.PHResponseParser
import app.hotx.networking.PHWebService
import app.hotx.networking.PHWebServiceCallback
import io.objectbox.BoxStore
import kotlinx.android.synthetic.main.fragment_search.view.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


/**
 * Created by Grigory Azaryan on 11/21/18.
 */

class SearchFragment : Fragment(), HostMainFragment.IFragmentContainer {

    private var session: ViewScreenSession = ViewScreenSession(Analytics.Value.SEARCH)
    private var rootView: View? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: MainVideoListRecyclerAdapter
    private lateinit var endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener
    private lateinit var searchView: SearchView

    private var additionalFilters: Map<String, String> = HashMap()

    private var searchTag: String? = null
    private var searchText: String? = null
    private var searchCategoryId: String? = null
    private var filter: String? = ""
    private var order: String? = Const.ORDER_HOTTEST

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
    @Inject
    lateinit var appHelper: AppHelper
    @Inject
    lateinit var analytics: Analytics

    companion object {
        @JvmStatic
        fun newInstance(arguments: Bundle? = null) =
                SearchFragment().apply {
                    this.arguments = arguments
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.getAppComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView?.let { return it }
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        searchView = view.findViewById(R.id.toolbar_search_view)

        view.swipe_refresh_layout.setProgressViewOffset(false, 0, 200)
        view.swipe_refresh_layout.setOnRefreshListener { loadPage(0) }

        val orderKeys = arrayOf(Const.ORDER_HOTTEST, Const.ORDER_MOST_RECENT, Const.ORDER_TOP_RATED, Const.ORDER_MOST_VIEWED, Const.ORDER_NEWEST, Const.ORDER_LONGEST)
        val orderNames = arrayOf(getString(R.string.hottest), getString(R.string.recently_featured), getString(R.string.top_rated), getString(R.string.most_viewed), getString(R.string.newest), getString(R.string.longest))

        val filterKeys = arrayOf(Const.FILTER_ALL, Const.FILTER_DAY, Const.FILTER_WEEK, Const.FILTER_MONTH)
        val filterNames = arrayOf(getString(R.string.all), getString(R.string.today), getString(R.string.week), getString(R.string.month))

        view.order_spinner.adapter = ArrayAdapter(context!!, R.layout.simple_drop_down_item, orderNames)
        view.order_spinner.post {
            view.order_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    order = orderKeys[position]
                    if (arrayOf(Const.ORDER_TOP_RATED, Const.ORDER_MOST_VIEWED).contains(order))
                        rootView?.filter_spinner?.visibility = View.VISIBLE
                    else {
                        rootView?.filter_spinner?.setSelection(0, false)
                        rootView?.filter_spinner?.visibility = View.INVISIBLE
                        filter = ""
                    }
                    rootView?.swipe_refresh_layout?.isRefreshing = true
                    loadPage(0)
                }

            }
        }
        view.filter_spinner.adapter = ArrayAdapter(context!!, R.layout.simple_drop_down_item, filterNames)
        view.filter_spinner.post {
            view.filter_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    filter = filterKeys[position]
                    rootView?.swipe_refresh_layout?.isRefreshing = true
                    loadPage(0)
                }
            }
        }

        view.filters.setOnClickListener {
            FiltersDialogBuilder(activity)
                    .setSelectedFilters(additionalFilters)
                    .setFiltersSelectedListener { selectedFilters ->
                        view.filters.setImageResource(if (selectedFilters.isNotEmpty()) R.drawable.ic_filter_selected else R.drawable.ic_filter)
                        additionalFilters = selectedFilters
                        view.swipe_refresh_layout?.isRefreshing = true
                        loadPage(0)
                    }
                    .create().show()
        }

        recyclerAdapter = MainVideoListRecyclerAdapter(context!!, ArrayList())

        setupRecycler()

        var title = ""
        val toolbarBuilder = Toolbar.Builder().withLayout(view.findViewById(R.id.toolbar))
                .setNavigationButton(R.drawable.ic_logo_round, null)

        if (arguments == null) arguments = Bundle()
        arguments?.apply {
            when {
                containsKey(Const.KEY_CATEGORY_ID) -> {
                    searchCategoryId = getString(Const.KEY_CATEGORY_ID)
                    title = getString(R.string.category) + ":" + categories.getCategoryNameById(searchCategoryId)
                    toolbarBuilder.setNavigationButton(R.drawable.ic_back_arrow) {
                        activity!!.onBackPressed()
                    }
                }
                containsKey(Const.KEY_TAG) -> {
                    searchTag = getString(Const.KEY_TAG, "")
                    title = getString(R.string.tag) + ":" + searchTag
                    toolbarBuilder.setNavigationButton(R.drawable.ic_back_arrow) {
                        activity!!.onBackPressed()
                    }
                }
                else -> {
                    title = getString(R.string.search) + ":"
                    toolbarBuilder.setSearchView(setupSearchView())
                }
            }

            session.origin = getString(Analytics.Param.ORIGIN)
        }

        toolbarBuilder
                .setTitle(title)
                .create()

        view.swipe_refresh_layout.isRefreshing = true
        loadPage(0)

        rootView = view
        return rootView
    }

    private fun setupSearchView(): SearchView {
        val autocompletePopup = ListPopupWindow(context!!)
        val autocompleteAdapter = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_dropdown_item, mutableListOf())

        autocompletePopup.isModal = false
        autocompletePopup.anchorView = searchView
        autocompletePopup.setAdapter(autocompleteAdapter)

        var showPopup = true
        autocompletePopup.setOnItemClickListener { _, _, position, _ ->
            val search = autocompleteAdapter.getItem(position)
            searchView.setQuery(search, true)
            showPopup = false
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            var init = false
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty() && init) {
                    searchText = ""
                    view?.swipe_refresh_layout?.isRefreshing = true
                    loadPage(0)
                    appHelper.hideKeyboard(activity)
                } else {
                    phWebService.searchAutocomplete(Const.PHAppKey, utils.deviceID, newText, Const.SEARCH_VIDEO)
                            .enqueue(PHWebServiceCallback { response ->
                                if (!response.isSuccess) return@PHWebServiceCallback

                                val suggestions = phResponseParser.parseAutocompleteSearch(response.data)
                                autocompleteAdapter.clear()
                                autocompleteAdapter.addAll(suggestions)
                                autocompletePopup.takeIf { showPopup && !it.isShowing }?.show()
                                showPopup = true
                            })
                }
                init = true
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                searchText = query
                view?.swipe_refresh_layout?.isRefreshing = true
                loadPage(0)
                autocompletePopup.dismiss()
                appHelper.hideKeyboard(activity)
                analytics.logSearch(Analytics.Value.TYPE_SEARCH_TEXT, query)
                return false
            }

        })
        return searchView
    }

    private fun setupRecycler() {
        recyclerView.addOnItemTouchListener(RecyclerTouchListener(context, recyclerView, object : RecyclerTouchListener.ClickListener {
            override fun onClick(view: View, position: Int) {
                val video = recyclerAdapter.getItem(position)
                RxBus.publish(Event.VideoItemClick(video, Analytics.Value.SEARCH))
            }

            override fun onLongClick(view: View, position: Int) {
                val video = recyclerAdapter.getItem(position)
                recyclerAdapter.startPreview(video, position)
            }
        }))
        recyclerView.adapter = recyclerAdapter

        val layoutManager = GridLayoutManager(context, 2)
        recyclerView.layoutManager = layoutManager
        endlessRecyclerViewScrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadPage(page)
            }
        }
        recyclerView.addOnScrollListener(endlessRecyclerViewScrollListener)
    }

    internal fun loadPage(page: Int) {
        val shouldClear = page < 1
        val p = if (page > 0) page else 1
        phWebService.getVideos(Const.PHAppKey, utils.deviceID, EndlessRecyclerViewScrollListener.PAGE_SIZE,
                (p - 1) * EndlessRecyclerViewScrollListener.PAGE_SIZE, searchTag ?: searchText, searchCategoryId, order, filter, additionalFilters)
                .enqueue(PHWebServiceCallback { response ->
                    view?.swipe_refresh_layout?.isRefreshing = false
                    if (!response.isSuccess) {
                        recyclerAdapter.clear()
                        endlessRecyclerViewScrollListener.resetState()
                        return@PHWebServiceCallback
                    }

                    if (shouldClear) {
                        recyclerAdapter.clear()
                        endlessRecyclerViewScrollListener.resetState()
                    }

                    val videos = phResponseParser.parseVideos(response.data)
                    val notPremiumVideos = videos.filter { v -> !v.isPremium }
                    recyclerAdapter.addItems(notPremiumVideos)
                })
    }

    override fun addFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit()
    }

    override fun onPause() {
        super.onPause()
        session.takeIf { it.origin != null }?.stop()
    }

    override fun onResume() {
        super.onResume()
        session.takeIf { it.origin != null }?.start()
    }
}