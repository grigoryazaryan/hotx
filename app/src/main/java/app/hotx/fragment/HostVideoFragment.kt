package app.hotx.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import app.hotx.R
import app.hotx.helper.Const
import app.hotx.log.Analytics

class HostVideoFragment : Fragment() {

    private val fm
        get() = childFragmentManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_video_host, container, false)
    }


    fun openNewVideo(viewkey: String, origin: String) {
        val bundle = Bundle()
        bundle.putString(Const.KEY_VIDEO_VKEY, viewkey)
        bundle.putString(Analytics.Param.ORIGIN, origin)
        fm.beginTransaction()
                .replace(R.id.video_host_container, VideoFragment.newInstance(bundle))
//                .also {
//                    it.takeIf {
//                        fm.backStackEntryCount == 0
//                    }?.addToBackStack(null)
//                }
                .addToBackStack(null)
                .commit()
    }

    fun removeAllFragments() {
        fm.popBackStackImmediate(null, POP_BACK_STACK_INCLUSIVE)
    }

    fun getFragmentsCount(): Int {
        return fm.backStackEntryCount
    }
}
