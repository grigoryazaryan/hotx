package app.hotx.adapter;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import javax.inject.Inject;

import app.hotx.R;
import app.hotx.app.App;
import app.hotx.fragment.VideoInfoFragment;
import app.hotx.fragment.VideoRelatedFragment;
import app.hotx.model.PHVideo;
import app.hotx.model.VideoObject;

public class VideoFragmentPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles[];
    private PHVideo video;

    @Inject
    Context context;

    public VideoFragmentPagerAdapter(FragmentManager fm, PHVideo video) {
        super(fm);
        App.getAppComponent().inject(this);
        this.video = video;
        tabTitles = new String[]{context.getString(R.string.related), context.getString(R.string.info)};
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0)
            return VideoRelatedFragment.newInstance(video);
        else
            return VideoInfoFragment.newInstance(video);
    }

    //https://medium.com/inloopx/adventures-with-fragmentstatepageradapter-4f56a643f8e0
    @Override
    public long getItemId(int position) {
        return video.hashCode() * (position + 1) * 10;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
