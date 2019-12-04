package app.hotx.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.view.CollapsibleActionView;
import app.hotx.R;

/**
 * Created by Grigory Azaryan on 9/30/18.
 */

public class ToolbarSearchView extends RelativeLayout implements CollapsibleActionView {
    //    private TextView searchBadge;
//    private ImageView actionBtn;
    private SearchListener searchListener;
    private String currentSearch;

    public ToolbarSearchView(Context context) {
        this(context, null);
    }

    public ToolbarSearchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.toolbar_current_search_view, this);
//        searchBadge = findViewById(R.id.search_badge);
//        actionBtn = findViewById(R.id.search_action_btn);
//
//        actionBtn.setOnClickListener(v -> {
////            String text = searchBadge.getText().toString().trim();
//            String text = "";
//
//            if (searchListener != null) {
//                searchListener.onSearch(text);
//            }
//            searchBadge.setText(text);
//            updateActionBtn();
//        });


    }

    public void setSearchBadge(String text) {
//        searchBadge.setText(text);
//        updateActionBtn();
        removeAllViews();

        currentSearch = text == null ? "" : text.trim();
        if (currentSearch.length() > 0) {
            View item = createTagItem(currentSearch);
            item.setOnClickListener(v -> {
                removeAllViews();
                if (searchListener != null) {
                    searchListener.onCleared();
                }
            });
            addView(item);
        }/* else {
            if (searchListener != null) {
                searchListener.onSearch(currentSearch);
            }
        }*/
    }

    public String getCurrentSearch() {
        return currentSearch;
    }

    public void updateActionBtn() {
//        String text = searchBadge.getText().toString();
//        if (text.trim().length() == 0) {
////            actionBtn.setImageResource(R.drawable.ic_search);
//            actionBtn.setVisibility(GONE);
//        } else {
//            actionBtn.setImageResource(R.drawable.ic_close);
//            actionBtn.setVisibility(VISIBLE);
//        }
    }

    private View createTagItem(String tag) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.toolbar_search_item, null);
        TextView textView = view.findViewById(R.id.search_item_text);
        textView.setText(tag);
        view.setTag(tag);
        return view;
    }

    @Override
    public void onActionViewExpanded() {

    }

    @Override
    public void onActionViewCollapsed() {

    }

    public void setSearchListener(SearchListener searchListener) {
        this.searchListener = searchListener;
    }

    public interface SearchListener {
        void onNewSearch(String text);
        void onCleared();
    }
}
