package app.hotx.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import app.hotx.R
import app.hotx.app.App
import app.hotx.helper.Utils
import app.hotx.model.AdditionalFilterEntry
import kotlinx.android.synthetic.main.filter_dialog_option_layout.view.*

/**
 * Created by Grigory Azaryan on 11/6/18.
 */
class PhFiltersDialogBuilder {
    private val activity: Context
    private var filtersSelectedListener: (selectedOptions: Map<String, String>) -> Unit = {}
    private var options: Map<String, AdditionalFilterEntry> = emptyMap()
    private var filtersPreset: Map<String, String>? = null

    constructor (activity: Context) {
        App.getAppComponent().inject(this)
        this.activity = activity
    }

    fun Int.dp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
    fun create(): AlertDialog {
        val layout = LinearLayout(activity)
        layout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        layout.orientation = LinearLayout.VERTICAL
        val d = Utils.dpToPx(15)
        layout.setPadding(d, d, d, d)

        for (option in options) {
            val view = LayoutInflater.from(activity).inflate(R.layout.filter_dialog_option_layout, null)

            view.option_title.text = option.value.title
            view.option_spinner.adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, option.value.values)
            filtersPreset?.get(option.key)?.also {
                view.option_spinner.setSelection(option.value.values.indexOf(it))
            }
            view.option_spinner.tag = option.key // remember option key in View tag


            layout.addView(view)
        }

        return AlertDialog.Builder(activity)
                .setTitle(R.string.filters)
                .setView(layout)
                .setPositiveButton(R.string.apply) { _, _ ->
                    filtersSelectedListener((0..(layout.childCount - 1)).fold(emptyMap()) { map, i ->
                        layout.getChildAt(i).let {
                            if (it.option_spinner.selectedItemPosition > 0)
                                map.plus(it.option_spinner.tag.toString() to it.option_spinner.selectedItem.toString())
                            else
                                map
                        }
                    })
                }
                .setNegativeButton(R.string.cancel, null)
                .create()
    }

    fun setSelectedFilters(selectedFilters: Map<String, String>?): PhFiltersDialogBuilder {
        this.filtersPreset = selectedFilters
        return this
    }

    fun setFiltersSelectedListener(filtersSelectedListener: (Map<String, String>) -> Unit): PhFiltersDialogBuilder {
        this.filtersSelectedListener = filtersSelectedListener
        return this
    }

    fun setOptions(options: Map<String, AdditionalFilterEntry>): PhFiltersDialogBuilder {
        this.options = options
        return this
    }

}