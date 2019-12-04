package app.hotx.helper;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;

import java.util.List;
import java.util.Map;

import app.hotx.R;
import solid.collectors.ToList;
import solid.stream.Stream;

public class DropdownOptionsSelector {

    private ItemSelectedListener itemSelectedListener;
    private Map<String, String> options;
    private ListPopupWindow listPopupWindow;
    private int selectedItemPosition = -1;
    private View selectedView;
    private ListAdapter adapter;


//    public DropdownOptionsSelector setOptions(Map<String, String> options) {
//        this.options = options;
//        return this;
//    }

    public DropdownOptionsSelector setAdapter(ListAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public DropdownOptionsSelector anchor(View view) {

        listPopupWindow = new ListPopupWindow(view.getContext());

        listPopupWindow.setAnchorView(view);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setContentWidth(view.getResources().getDimensionPixelSize(R.dimen.dropdown_options_selector_content_width));

        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener((parent, v, position, id) -> {
            setSelection(position);
            listPopupWindow.dismiss();
        });
        return this;
    }

    public void expand() {
        listPopupWindow.show();
    }

    public void setSelection(int position) {
        if (selectedItemPosition != position) {
            if (selectedItemPosition != -1 && itemSelectedListener != null) {
                itemSelectedListener.onItemSelected(position, (String) adapter.getItem(position));
            }
//        View v = adapter.getView(position, null, null);
            selectedItemPosition = position;
//        if (selectedView != null) selectedView.setSelected(false);
//        v.setSelected(true);
//        selectedView = v;
        }
    }

    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    public DropdownOptionsSelector setItemSelectedListener(ItemSelectedListener itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
        return this;
    }

    public interface ItemSelectedListener {
        void onItemSelected(int position, String selected);
    }

    public static ArrayAdapter<String> createSimpleAdapter(Context context, List<Pair<String, String>> options) {
        List<String> qNames = Stream.stream(options).map(pair -> pair.second).collect(ToList.toList());
        return new ArrayAdapter<>(context, R.layout.simple_drop_down_item, qNames);
    }
}
