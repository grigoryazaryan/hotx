package app.hotx.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import app.hotx.R
import app.hotx.app.App
import app.hotx.helper.AppHelper
import app.hotx.helper.Const
import javax.inject.Inject

class HostMainFragment : Fragment() {

    //    private var listener: (tag: String?) -> Unit = {}
    private val fm: FragmentManager
        get() = childFragmentManager

    @Inject
    lateinit var appHelper: AppHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        App.getAppComponent().inject(this)
//        fm.addOnBackStackChangedListener {
//            listener.invoke(getHostFragmentTag())
//        }
    }

//    fun setBackStackChangedListener(listener: (tag: String?) -> Unit) {
//        this.listener = listener
//    }

    private fun getHostFragmentTag(): String? {
        return fm.primaryNavigationFragment!!.tag
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_host_main, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance() =
                HostMainFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }

//    fun setPrimaryFragment() {
//        navigationItemSelected(R.id.action_search)
//        listener.invoke(Const.TAG_FRAGMENT_SEARCH)
//    }

    fun openNewFragment(fragment: Fragment) {
        (fm.primaryNavigationFragment as IFragmentContainer).addFragment(fragment)
    }

    fun navigationItemSelected(actionId: Int) {
        val fm = childFragmentManager
        hideAllFragments()
        when (actionId) {
            R.id.action_search -> {
                fm.findFragmentByTag(Const.TAG_FRAGMENT_SEARCH)?.let {
                    fm.beginTransaction()
                            .attach(it)
                            .setPrimaryNavigationFragment(it)
                            .commit()
                } ?: SearchFragment.newInstance(null).let {
                    fm.beginTransaction()
                            .replace(R.id.main_container, it, Const.TAG_FRAGMENT_SEARCH)
                            .setPrimaryNavigationFragment(it)
                            .commit()
                }
            }
            R.id.action_stars_all -> {
                fm.findFragmentByTag(Const.TAG_FRAGMENT_STARS_ALL)?.let {
                    fm.beginTransaction()
                            .attach(it)
                            .setPrimaryNavigationFragment(it)
                            .commit()
                } ?: StarsAllFragment.newInstance().let {
                    fm.beginTransaction()
                            .replace(R.id.main_container, it, Const.TAG_FRAGMENT_STARS_ALL)
                            .setPrimaryNavigationFragment(it)
                            .commit()
                }
            }
            R.id.action_recommended -> {
                fm.findFragmentByTag(Const.TAG_FRAGMENT_RECOMMENDED)?.let {
                    fm.beginTransaction()
                            .attach(it)
                            .setPrimaryNavigationFragment(it)
                            .commit()
                } ?: HotxFragment.newInstance().let {
                    fm.beginTransaction()
                            .replace(R.id.main_container, it, Const.TAG_FRAGMENT_RECOMMENDED)
                            .setPrimaryNavigationFragment(it)
                            .commit()
                }
            }
            R.id.action_categories -> {
                fm.findFragmentByTag(Const.TAG_FRAGMENT_CATEGORIES)?.let {
                    fm.beginTransaction()
                            .attach(it)
                            .setPrimaryNavigationFragment(it)
                            .commit()
                } ?: CategoriesFragment.newInstance().let {
                    fm.beginTransaction()
                            .replace(R.id.main_container, it, Const.TAG_FRAGMENT_CATEGORIES)
                            .setPrimaryNavigationFragment(it)
                            .commit()
                }
            }
            R.id.action_profile -> {
                fm.findFragmentByTag(Const.TAG_FRAGMENT_PROFILE)?.let {
                    fm.beginTransaction()
                            .attach(it)
                            .setPrimaryNavigationFragment(it)
                            .commit()
                } ?: ProfileFragment.newInstance().let {
                    fm.beginTransaction()
                            .replace(R.id.main_container, it, Const.TAG_FRAGMENT_PROFILE)
                            .setPrimaryNavigationFragment(it)
                            .commit()
                }
            }
        }
    }

    private fun hideAllFragments() {
        for (f in fm.fragments) {
            fm.beginTransaction().detach(f).commit()
        }
    }

    interface IFragmentContainer {
        fun addFragment(fragment: Fragment)
    }
}
