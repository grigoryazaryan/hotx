package app.hotx.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import app.hotx.R;
import app.hotx.app.App;
import app.hotx.helper.Const;

public class FiltersDialogBuilder {

    private Context activity;
    private FiltersSelectedListener filtersSelectedListener;
    private Map<String, String> filtersPreset;

//    @Inject
//    Categories categories;

    public FiltersDialogBuilder(Context activity) {
        App.getAppComponent().inject(this);
        this.activity = activity;
    }

    public AlertDialog create() {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_filters, null, false);
        Spinner productionSpinner = view.findViewById(R.id.production_spinner);
        String[] productionKeys = new String[]{null, Const.PRODUCTION_PROFESSIONAL, Const.PRODUCTION_HOMEMADE};
        String[] productionNames = new String[]{activity.getString(R.string.all), activity.getString(R.string.professional),
                activity.getString(R.string.homemade)};
        productionSpinner.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, productionNames));

        Spinner qualitySpinner = view.findViewById(R.id.quality_spinner);
        String[] qualityNames = new String[]{activity.getString(R.string.all), activity.getString(R.string.hd_only)};
        String[] qualityKeys = new String[]{null, "1"};
        qualitySpinner.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, qualityNames));

        Spinner durationSpinner = view.findViewById(R.id.duration_spinner);
        String[] durationNames = new String[]{activity.getString(R.string.all), "10+ min", "20+ min", "30+ min"};
        String[] durationKeys = new String[]{null, "10", "20", "30"};
        durationSpinner.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, durationNames));

        if (filtersPreset != null && !filtersPreset.isEmpty()) {
            if (filtersPreset.containsKey(Const.KEY_PRODUCTION))
                productionSpinner.setSelection(Arrays.asList(productionKeys).indexOf(filtersPreset.get(Const.KEY_PRODUCTION)));
            if (filtersPreset.containsKey(Const.KEY_QUALITY))
                qualitySpinner.setSelection(Arrays.asList(qualityKeys).indexOf(filtersPreset.get(Const.KEY_QUALITY)));
            if (filtersPreset.containsKey(Const.KEY_DURATION))
                durationSpinner.setSelection(Arrays.asList(durationKeys).indexOf(filtersPreset.get(Const.KEY_DURATION)));
        }
        return new AlertDialog.Builder(activity)
                .setTitle(R.string.filters)
                .setView(view)
                .setPositiveButton(R.string.apply, (dialog, which) -> {
                    if (filtersSelectedListener != null) {
                        Map<String, String> filters = new HashMap<>();
                        if (productionSpinner.getSelectedItemPosition() != 0)
                            filters.put(Const.KEY_PRODUCTION, productionKeys[productionSpinner.getSelectedItemPosition()]);
                        if (qualitySpinner.getSelectedItemPosition() != 0)
                            filters.put(Const.KEY_QUALITY, qualityKeys[qualitySpinner.getSelectedItemPosition()]);
                        if (durationSpinner.getSelectedItemPosition() != 0)
                            filters.put(Const.KEY_DURATION, durationKeys[durationSpinner.getSelectedItemPosition()]);
                        filtersSelectedListener.onFiltersSelected(filters);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    public FiltersDialogBuilder setSelectedFilters(Map<String, String> selectedFilters) {
        this.filtersPreset = selectedFilters;
        return this;
    }

    public FiltersDialogBuilder setFiltersSelectedListener(FiltersSelectedListener filtersSelectedListener) {
        this.filtersSelectedListener = filtersSelectedListener;
        return this;
    }

    public interface FiltersSelectedListener {
        void onFiltersSelected(Map<String, String> filters);
    }

}
