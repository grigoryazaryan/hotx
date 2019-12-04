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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.hotx.R
import app.hotx.adapter.StarsAllAdapter
import app.hotx.app.App
import app.hotx.dialog.PhFiltersDialogBuilder
import app.hotx.eventbus.Event
import app.hotx.eventbus.RxBus
import app.hotx.helper.*
import app.hotx.log.Analytics
import app.hotx.model.AdditionalFilterEntry
import app.hotx.model.OrderFilterEntry
import app.hotx.networking.PHResponseParser
import app.hotx.networking.PHWebService
import app.hotx.networking.PHWebServiceCallback
import kotlinx.android.synthetic.main.fragment_stars_all.view.*
import javax.inject.Inject

class StarsAllFragment : Fragment(), HostMainFragment.IFragmentContainer {

    private lateinit var endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var adapter: StarsAllAdapter

    private var rootView: View? = null

    private var additionalFilters: Map<String, String> = emptyMap()
    private var orderFilterQuery: Map<String, String> = mutableMapOf()

    private var search: String = ""


    @Inject
    lateinit var utils: Utils
    @Inject
    lateinit var appHelper: AppHelper
    @Inject
    lateinit var phWebService: PHWebService
    @Inject
    lateinit var phResponseParser: PHResponseParser
    @Inject
    lateinit var analytics: Analytics

    companion object {
        @JvmStatic
        fun newInstance(arguments: Bundle? = null) =
                StarsAllFragment().apply {
                    this.arguments = arguments
                    App.getAppComponent().inject(this)
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        data?.also { AppHelper.log(this, utils.toString(it)) }
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView?.let { return it }
        val view = inflater.inflate(R.layout.fragment_stars_all, container, false)

        swipeRefreshLayout = view.swipe_refresh_layout
        swipeRefreshLayout.setProgressViewOffset(false, 0, 200)
        view.swipe_refresh_layout.setOnRefreshListener { loadPage(0) }

        adapter = StarsAllAdapter(context!!)
        view.recycler_view.addOnItemTouchListener(RecyclerTouchListener(context, view.recycler_view, object : RecyclerTouchListener.ClickListener {
            override fun onClick(view: View, position: Int) {
                val item = adapter.getItem(position)
                RxBus.publish(Event.StarItemClick(item))
            }

            override fun onLongClick(view: View, position: Int) {
            }
        }))
        view.recycler_view.adapter = adapter

        val layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 2)
        view.recycler_view.layoutManager = layoutManager
        endlessRecyclerViewScrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: androidx.recyclerview.widget.RecyclerView) {
                loadPage(page)
            }
        }
        view.recycler_view.addOnScrollListener(endlessRecyclerViewScrollListener)

        val searchView: SearchView = view.findViewById(R.id.toolbar_search_view)
        val autocompletePopup = ListPopupWindow(view.context)
        val autocompleteAdapter = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_dropdown_item, mutableListOf())

        autocompletePopup.isModal = false
        autocompletePopup.anchorView = searchView
        autocompletePopup.setAdapter(autocompleteAdapter)

        autocompletePopup.setOnItemClickListener { _, _, position, _ ->
            val star = autocompleteAdapter.getItem(position)
            autocompletePopup.dismiss()
            searchView.onActionViewCollapsed()
            RxBus.publish(Event.SearchStar(star!!))
        }
//        searchView.setOnCloseListener {
//            search = ""
//            loadPage(0)
//            false
//        }
//        searchView.setOnSearchClickListener {
//            search = searchView.query.toString()
//            loadPage(0)
//        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    search = newText
                    rootView?.swipe_refresh_layout?.isRefreshing = true
                    loadPage(0)
                } else {
                    phWebService.searchAutocomplete(Const.PHAppKey, utils.deviceID, newText, Const.SEARCH_PORNSTAR)
                            .enqueue(PHWebServiceCallback { response ->
                                if (!response.isSuccess) return@PHWebServiceCallback

                                val suggestions = phResponseParser.parseAutocompletePornstars(response.data)
                                autocompleteAdapter.clear()
                                autocompleteAdapter.addAll(suggestions.map { it.name })
                                autocompletePopup.takeUnless { it.isShowing }?.show()
                            })
                }
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                search = query
                autocompletePopup.dismiss()
                appHelper.hideKeyboard(activity)
                rootView?.swipe_refresh_layout?.isRefreshing = true
                loadPage(0)
                analytics.logSearch(Analytics.Value.TYPE_SEARCH_TEXT, query)
                return false
            }

        })

        Toolbar.Builder().withLayout(view.toolbar)
                .setTitle(getString(R.string.pornstars))
                .setNavigationButton(R.drawable.ic_logo_round)
                .setSearchView(searchView)
                .create()

        view.swipe_refresh_layout.isRefreshing = true
        loadPage(0)

        rootView = view
        return rootView
    }

    private fun setFiltersData(selectedOrder: OrderFilterEntry) {
        rootView?.filter_spinner?.apply {
            var data = emptyList<String>()
            if (selectedOrder.filterValues.isNotEmpty()) data = selectedOrder.filterValues.map { it.second }
            adapter = ArrayAdapter(activity!!, R.layout.simple_drop_down_item, data)
            visibility = if (data.isEmpty()) View.INVISIBLE else View.VISIBLE
        }
    }

    private fun setupOrdersFilters(ordersFilters: List<OrderFilterEntry>) {
        rootView?.takeIf { it.order_spinner.tag == null }?.also {
            it.order_spinner.adapter = ArrayAdapter(activity!!, R.layout.simple_drop_down_item, ordersFilters.map { o -> o.orderTitle })
            var selectedOrder: OrderFilterEntry = ordersFilters[0]
            setFiltersData(selectedOrder)
            it.post {
                it.order_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        selectedOrder = ordersFilters[position]
                        orderFilterQuery = mapOf("order" to selectedOrder.orderKey)

                        setFiltersData(selectedOrder)

                        if (selectedOrder.filterValues.isEmpty()) {
                            rootView?.swipe_refresh_layout?.isRefreshing = true
                            loadPage(0)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {

                    }
                }

                it.filter_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        orderFilterQuery = mapOf("order" to selectedOrder.orderKey, selectedOrder.filterKey to selectedOrder.filterValues[position].first)
                        rootView?.swipe_refresh_layout?.isRefreshing = true
                        loadPage(0)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {

                    }
                }
            }
            it.order_spinner.tag = true // indicate spinner initialized
        }
    }

    internal fun loadPage(page: Int) {
        val shouldClear = page < 1
        val p = if (page > 0) page else 1
        phWebService.getPornstars(Const.PHAppKey, utils.deviceID, EndlessRecyclerViewScrollListener.PAGE_SIZE,
                (p - 1) * EndlessRecyclerViewScrollListener.PAGE_SIZE, orderFilterQuery, additionalFilters, search)
                .enqueue(PHWebServiceCallback { response ->
                    swipeRefreshLayout.isRefreshing = false
                    if (!response.isSuccess) {
                        adapter.clear()
                        endlessRecyclerViewScrollListener.resetState()
                        return@PHWebServiceCallback
                    }

                    val stars = phResponseParser.parsePornstars(response.data)
                    val additionalFilters = phResponseParser.parsePornstarsAdditionalFilters(response.data)
                    val ordersFilters = phResponseParser.parseOrder(response.data)

                    if (shouldClear) {
                        adapter.clear()
                        endlessRecyclerViewScrollListener.resetState()
                    }

                    adapter.addItems(stars)
                    setupOrdersFilters(ordersFilters)
                    setupAdditionalFilters(additionalFilters)
                })
    }

    private fun setupAdditionalFilters(options: Map<String, AdditionalFilterEntry>) {
        rootView?.filters?.takeIf { it.tag == null }?.apply {
            setOnClickListener {
                PhFiltersDialogBuilder(activity!!)
                        .setOptions(options)
                        .setSelectedFilters(additionalFilters)
                        .setFiltersSelectedListener { map ->
                            setImageResource(if (map.isNotEmpty()) R.drawable.ic_filter_selected else R.drawable.ic_filter)
                            additionalFilters = map
                            rootView?.swipe_refresh_layout?.isRefreshing = true
                            loadPage(0)
                        }
                        .create().show()
            }
            tag = true // indicate dialog initialized
        }
    }

    override fun addFragment(fragment: Fragment) {
        appHelper.hideKeyboard(activity)
        childFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit()
    }
}