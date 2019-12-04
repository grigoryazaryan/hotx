package app.hotx.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import app.hotx.R;
import app.hotx.adapter.CategoriesAdapter;
import app.hotx.app.App;
import app.hotx.eventbus.Event;
import app.hotx.eventbus.RxBus;
import app.hotx.helper.AppHelper;
import app.hotx.helper.Categories;
import app.hotx.helper.RecyclerTouchListener;
import app.hotx.helper.Toolbar;
import app.hotx.model.Category;
import solid.collectors.ToList;
import solid.stream.Stream;

/**
 * Created by Grigory Azaryan on 10/7/18.
 */

public class CategoriesFragment extends Fragment implements HostMainFragment.IFragmentContainer {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private CategoriesAdapter recyclerAdapter;
    private View rootView;

    @Inject
    Categories categoriesHelper;
    @Inject
    AppHelper appHelper;

    public CategoriesFragment() {
        // Required empty public constructor
        App.getAppComponent().inject(this);
    }

    public static CategoriesFragment newInstance() {
        CategoriesFragment videoFragment = new CategoriesFragment();
        Bundle bundle = new Bundle();
        videoFragment.setArguments(bundle);
        return videoFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) return rootView;
        rootView = inflater.inflate(R.layout.fragment_categories, container, false);

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerAdapter = new CategoriesAdapter(getContext());

        swipeRefreshLayout.setProgressViewOffset(false, 0, 200);
        swipeRefreshLayout.setOnRefreshListener(this::loadCategories);

        new Toolbar.Builder().withLayout(rootView.findViewById(R.id.toolbar))
                .setTitle(getString(R.string.categories))
                .setNavigationButton(R.drawable.ic_logo_round, null)
                .create();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                Category category = recyclerAdapter.getItem(position);
                if (!categoriesHelper.isCategorySupported(category.getId())) {
                    appHelper.showToast(R.string.video_type_not_supported_yet);
                    return;
                }
                RxBus.INSTANCE.publish(new Event.CategoryItemClick(category));
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
        recyclerView.setAdapter(recyclerAdapter);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout.setRefreshing(true);
        loadCategories();

        return rootView;
    }

    private void loadCategories() {
        categoriesHelper.load(categories -> {
            swipeRefreshLayout.setRefreshing(false);
            recyclerAdapter.clear();
            if (categories.size() > 0) {
                List<Category> data = new ArrayList<>(categories);
                // todo ambiguous
                data.remove(0);
                data = Stream.stream(data).filter(value -> categoriesHelper.isCategorySupported(value.getId())).collect(ToList.toList());
                recyclerAdapter.addItems(data);
            }
        });
    }

    @Override
    public void addFragment(@NotNull Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
