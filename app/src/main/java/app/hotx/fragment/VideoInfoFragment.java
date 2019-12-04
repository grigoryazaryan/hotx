package app.hotx.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import app.hotx.R;
import app.hotx.activity.MainActivity;
import app.hotx.app.App;
import app.hotx.eventbus.Event;
import app.hotx.eventbus.RxBus;
import app.hotx.helper.AppHelper;
import app.hotx.helper.Categories;
import app.hotx.helper.Const;
import app.hotx.helper.Utils;
import app.hotx.model.PHVideo;
import app.hotx.networking.ServerResponseParser;
import app.hotx.networking.WebService;

public class VideoInfoFragment extends Fragment {
    private PHVideo video;
    private FlexboxLayout videoTags, videoPornstars, videoCategories;
    private View labelPornstars, labelCategories;
    private TextView addedOn, approvedOn;
    private MainActivity activity;

    @Inject
    WebService webService;
    @Inject
    ServerResponseParser serverResponseParser;
    @Inject
    AppHelper appHelper;
    @Inject
    Utils utils;
    @Inject
    Categories categories;


    public VideoInfoFragment() {
        // Required empty public constructor
        App.getAppComponent().inject(this);
    }

    public static VideoInfoFragment newInstance(PHVideo videoObject) {
        VideoInfoFragment videoFragment = new VideoInfoFragment();
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
        return inflater.inflate(R.layout.fragment_video_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        videoTags = view.findViewById(R.id.video_tags);
        labelPornstars = view.findViewById(R.id.label_pornstars);
        videoPornstars = view.findViewById(R.id.video_pornstars);
        labelCategories = view.findViewById(R.id.label_categories);
        videoCategories = view.findViewById(R.id.video_categories);
        addedOn = view.findViewById(R.id.video_added_on);
        approvedOn = view.findViewById(R.id.video_approved_on);

        setVideoPornstars(video);
        setVideoCategories(video);
        setVideoTags(video);

        addedOn.setText(new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(video.getAddedOn() * 1000));
        approvedOn.setText(new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(video.getApprovedOn() * 1000));
        view.findViewById(R.id.video_approved_on_container).setVisibility(video.getApprovedOn() != 0 ? View.VISIBLE : View.GONE);
    }

    private void setVideoCategories(PHVideo video) {
        if (video.getCategoriesRaw() == null || video.getCategoriesRaw().split(",").length == 0) {
            labelCategories.setVisibility(View.GONE);
            videoCategories.setVisibility(View.GONE);
            return;
        }
        for (String catId : video.getCategoriesRaw().split(",")) {
            View view = createTagItem(categories.getCategoryNameById(catId));
            view.setOnClickListener(v -> {
                RxBus.INSTANCE.publish(new Event.SearchCategory(catId));
            });
            videoCategories.addView(view);
        }
    }

    private void setVideoTags(PHVideo video) {
        View.OnClickListener clickListener = v -> {
            RxBus.INSTANCE.publish(new Event.SearchTag(String.valueOf(v.getTag())));
        };

        for (String tag : video.getTags().split(",")) {
            View view = createTagItem(tag);
            view.setOnClickListener(clickListener);
            videoTags.addView(view);
        }
    }

    private void setVideoPornstars(PHVideo video) {
        if (video.getPornstars().isEmpty()) {
            labelPornstars.setVisibility(View.GONE);
            videoPornstars.setVisibility(View.GONE);
            return;
        }
        View.OnClickListener clickListener = v -> {
            RxBus.INSTANCE.publish(new Event.StarTagClick(String.valueOf(v.getTag())));
        };
        for (String tag : video.getPornstars().split(",")) {
            View view = createTagItem(tag);
            view.setOnClickListener(clickListener);
            videoPornstars.addView(view);
        }
    }

    private View createTagItem(String tag) {
        View view = LayoutInflater.from(activity).inflate(R.layout.tag_item, null);
        TextView textView = view.findViewById(R.id.tag_item_text);
        textView.setText(tag);
        view.setTag(tag);
        textView.setBackground(appHelper.createClickableViewBackground(R.drawable.tag_item_background));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity)
            activity = ((MainActivity) context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }
}
