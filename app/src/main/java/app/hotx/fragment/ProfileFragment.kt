package app.hotx.fragment

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import app.hotx.BuildConfig
import app.hotx.R
import app.hotx.app.App
import app.hotx.eventbus.Event
import app.hotx.eventbus.RxBus
import app.hotx.helper.AppHelper
import app.hotx.helper.Settings
import app.hotx.helper.Toolbar
import kotlinx.android.synthetic.main.dialog_fast_forward_settings.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import javax.inject.Inject


/**
 * Created by Grigory Azaryan on 11/8/18.
 */

class ProfileFragment : Fragment(), HostMainFragment.IFragmentContainer {

    private var rootView: View? = null

    @Inject
    internal lateinit var appHelper: AppHelper
    @Inject
    internal lateinit var settings: Settings

    companion object {
        @JvmStatic
        fun newInstance(arguments: Bundle? = null) =
                ProfileFragment().apply {
                    this.arguments = arguments

                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.getAppComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView?.let { return it }
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        view.password_layout.setOnClickListener { view.password_switch.isChecked = !view.password_switch.isChecked }
        view.password_switch.isChecked = settings.isPasswordUsed()

        view.auto_fullscreen_layout.setOnClickListener { view.auto_fullscreen_switch.isChecked = !view.auto_fullscreen_switch.isChecked }
        view.auto_fullscreen_switch.isChecked = settings.isAutoFullscreen

        view.fast_forward_layout.setOnClickListener { showFastForwardDialog() }

        setupAbout(view)

        Toolbar.Builder().withLayout(view.toolbar)
                .setTitle(getString(R.string.profile))
                .setNavigationButton(R.drawable.ic_logo_round, null)
                .create()

        view.password_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                appHelper.showPinDialog(fragmentManager, { dialog, password ->
                    settings.password = password
                    dialog.dismiss()
                }, { view.password_switch.isChecked = false })
            } else {
                settings.password = null
            }
        }

        view.auto_fullscreen_switch.setOnCheckedChangeListener { _, isChecked ->
            settings.isAutoFullscreen = isChecked
            RxBus.publish(Event.FullscreenSettingsChanged(isChecked))
        }

        view.favorites_layout.setOnClickListener {
            addFragment(FavoritesFragment.getInstance(null))
        }

        rootView = view
        return rootView
    }

    private fun setupAbout(view: View) {
        view.about.text = getString(R.string.version, BuildConfig.VERSION_NAME)
        val email = "mail@www.zzz"
        val content = SpannableString(email)
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        view.mail.text = content
        view.mail.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
            }
            startActivity(emailIntent)
        }
    }

    private fun showFastForwardDialog() {
        val dialog = AlertDialog.Builder(activity!!)
                .setView(R.layout.dialog_fast_forward_settings)
                .create()

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.apply)) { _, _ ->
            settings.fastForwardSec = dialog.fast_forward.progress
            settings.fastRewindSec = dialog.fast_rewind.progress
            RxBus.publish(Event.FastForwardSettingsChanged(settings.fastForwardSec, settings.fastRewindSec))
        }

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel)) { d, _ -> d.dismiss() }

        dialog.show()

        dialog.fast_forward.incrementProgressBy(1)
        dialog.fast_rewind.incrementProgressBy(1)

        dialog.fast_forward.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                dialog.fast_forward_text.text = getString(R.string.forward) + ": " + progress + "sec"
            }
        })

        dialog.fast_rewind.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                dialog.fast_rewind_text.text = getString(R.string.rewind) + ": " + progress + "sec"
            }
        })

        dialog.fast_forward.progress = settings.fastForwardSec
        dialog.fast_rewind.progress = settings.fastRewindSec
    }

    override fun addFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit()
    }
}