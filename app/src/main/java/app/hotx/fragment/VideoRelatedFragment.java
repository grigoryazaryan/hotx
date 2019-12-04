package app.hotx.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.hotx.R;
import app.hotx.adapter.MainVideoListRecyclerAdapter;
import app.hotx.app.App;
import app.hotx.eventbus.Event;
import app.hotx.eventbus.RxBus;
import app.hotx.helper.AppHelper;
import app.hotx.helper.Const;
import app.hotx.helper.EndlessRecyclerViewScrollListener;
import app.hotx.helper.RecyclerTouchListener;
import app.hotx.helper.Utils;
import app.hotx.log.Analytics;
import app.hotx.model.PHSmallVideo;
import app.hotx.model.PHVideo;
import app.hotx.networking.PHResponseParser;
import app.hotx.networking.PHWebService;
import app.hotx.networking.PHWebServiceCallback;
import solid.collectors.ToList;
import solid.stream.Stream;

public class VideoRelatedFragment extends Fragment {
    private PHVideo video;
    private RecyclerView recyclerView;
    private MainVideoListRecyclerAdapter recyclerAdapter;
    private View rootView;

    @Inject
    PHWebService phWebService;
    @Inject
    PHResponseParser phResponseParser;
    @Inject
    AppHelper appHelper;
    @Inject
    Utils utils;

    public VideoRelatedFragment() {
        // Required empty public constructor
        App.getAppComponent().inject(this);
    }

    public static VideoRelatedFragment newInstance(PHVideo videoObject) {
        VideoRelatedFragment videoFragment = new VideoRelatedFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.KEY_VIDEO_OBJECT, videoObject);
        videoFragment.setArguments(bundle);
        return videoFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getSerializable(Const.KEY_VIDEO_OBJECT) != null) {
            video = (PHVideo) getArguments().getSerializable(Const.KEY_VIDEO_OBJECT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) return rootView;
        rootView = inflater.inflate(R.layout.fragment_related_videos, container, false);

        recyclerView = rootView.findViewById(R.id.related_videos);
        recyclerAdapter = new MainVideoListRecyclerAdapter(getContext(), new ArrayList<>());
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener
                .ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                PHSmallVideo video = recyclerAdapter.getItem(position);
                RxBus.INSTANCE.publish(new Event.VideoItemClick(video, Analytics.Value.VIDEO));
            }

            @Override
            public void onLongClick(View view, int position) {
                PHSmallVideo video = recyclerAdapter.getItem(position);
                recyclerAdapter.startPreview(video, position);
            }
        }));
        recyclerView.setAdapter(recyclerAdapter);

        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadPage(page);
            }
        });

        loadPage(1);

        return rootView;
    }

    private void loadPage(int page) {
        phWebService.getRelatedVideos(Const.PHAppKey, utils.getDeviceID(), video.getVkey(),
                EndlessRecyclerViewScrollListener.PAGE_SIZE, (page - 1) * EndlessRecyclerViewScrollListener.PAGE_SIZE)
                .enqueue(new PHWebServiceCallback(response -> {
                    if (!response.isSuccess()) {
                        return;
                    }

                    List<PHSmallVideo> videos = phResponseParser.parseRelatedVideos(response.getData());
                    List<PHSmallVideo> notPremiumVideos = Stream.stream(videos).filter(v -> !v.isPremium()).collect(ToList.toList());
                    recyclerAdapter.addItems(notPremiumVideos);
                }));
    }

}
