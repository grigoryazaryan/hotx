package app.hotx.helper;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;

import java.util.Map;

import app.hotx.R;

public class VideoQualitySelector {
    private ItemSelectedListener itemSelectedListener;
    private Map<String, String> options;
    private String[] qNames;
    private int selectedItemPosition = -1;
    private Context context;

    public VideoQualitySelector(Context context, Map<String, String> options) {
        this.context = context;
        this.options = options;

        qNames = new String[options.size()];
        options.keySet().toArray(qNames);
    }

    public VideoQualitySelector setSelectedItem(int position) {
        this.selectedItemPosition = position;
        return this;
    }

    public VideoQualitySelector setSelectedItem(String quality) {
        for (int i = 0; i < qNames.length; i++) {
            if (qNames[i].equals(quality)) {
                this.selectedItemPosition = i;
                break;
            }
        }
        return this;
    }

    public VideoQualitySelector setItemSelectedListener(ItemSelectedListener itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
        return this;
    }

    public void show() {

        new AlertDialog.Builder(context)
                .setTitle(R.string.quality)
                .setSingleChoiceItems(qNames, selectedItemPosition, (dialogInterface, i) -> {
                    if (selectedItemPosition != i) {
                        selectedItemPosition = i;
                        if (itemSelectedListener != null)
                            itemSelectedListener.onItemSelected(i, qNames[i]);

                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();
    }

    public interface ItemSelectedListener {
        void onItemSelected(int position, String selected);
    }
}
