package app.hotx.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import app.hotx.R;
import app.hotx.model.Category;

/**
 * Created by Grigory Azaryan on 10/7/18.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    final String TAG = "CategoriesAdapter";

    private RecyclerView recyclerView;
    private Context context;
    private List<Category> data = new ArrayList<>();

    public CategoriesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Category category = data.get(position);

        viewHolder.title.setText(category.getName());

        Glide.with(context)
                .load(category.getImageUrl())
//                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .into(viewHolder.preview);
    }


    public void clear() {
        int count = data.size();
        data.clear();
        notifyItemRangeRemoved(0, count);
    }

    public void addItems(List<Category> items) {
        int positionStart = data.size();
        data.addAll(items);
        notifyItemRangeInserted(positionStart, items.size());
    }

    public Category getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        View root;
        TextView title;
        ImageView preview;

        ViewHolder(View view) {
            super(view);
            preview = view.findViewById(R.id.preview);
            title = view.findViewById(R.id.title);

            root = view;
        }
    }
}