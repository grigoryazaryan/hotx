package app.hotx.helper;

import android.content.Context;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.hotx.R;
import app.hotx.model.Category;
import app.hotx.networking.PHResponseParser;
import app.hotx.networking.PHWebService;
import app.hotx.networking.PHWebServiceCallback;
import io.objectbox.BoxStore;
import solid.collectors.ToList;
import solid.stream.Stream;

@Singleton
public class Categories {

    private PHWebService webService;
    private PHResponseParser responseParser;
    private Context context;
    private BoxStore boxStore;
    private Utils utils;

    private List<Category> categories;
    private List<String> notSupportedCategories;

    private List<CategoriesLoadedListener> categoriesLoadedListeners = new ArrayList<>();

    @Inject
    public Categories(PHWebService webService, PHResponseParser responseParser, Context context, BoxStore boxStore, Utils utils) {
        this.webService = webService;
        this.responseParser = responseParser;
        this.context = context;
        this.boxStore = boxStore;
        this.utils = utils;

        categories = new ArrayList<>(boxStore.boxFor(Category.class).getAll());
        notSupportedCategories = new ArrayList<>();
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<String> getCategoryIds() {
        return Stream.stream(categories).map(Category::getId).collect(ToList.toList());
    }

    public List<String> getCategoryNames() {
        return Stream.stream(categories).map(Category::getName).collect(ToList.toList());
    }

    public Category getCategoryById(String id) {
        if (id == null)
            return categories.get(0);
        return Stream.stream(categories)
                .filter(cat -> id.equals(cat.getId())).first().get();
    }

    public String getCategoryNameById(String id) {
        return getCategoryById(id).getName();
    }

    public List<Pair<String, String>> asPairsList() {
        return Stream.stream(categories).map(cat -> new Pair<>(cat.getId(), cat.getName())).collect(ToList.toList());
    }

    public void addCategoriesLoadedListener(CategoriesLoadedListener listener) {
        categoriesLoadedListeners.add(listener);
    }

    public void load(CategoriesLoadedListener callback) {
        webService.getCategories(Const.PHAppKey, utils.getDeviceID()).enqueue(new PHWebServiceCallback(response -> {
            if (response.isSuccess()) {
                categories.clear();
                Category category = new Category();
                category.setId(null);
                category.setName(context.getString(R.string.all));
                categories.add(0, category);
                categories.addAll(responseParser.parseCategories(response.getData()));
                boxStore.boxFor(Category.class).removeAll();
                boxStore.boxFor(Category.class).put(categories);

//                for (CategoriesLoadedListener listener : categoriesLoadedListeners)
//                    listener.onCategoriesLoaded(this);
            }
            callback.onCategoriesLoaded(categories);
        }));
    }

    public interface CategoriesLoadedListener {
        void onCategoriesLoaded(List<Category> categories);
    }

    public List<String> getNotSupportedCategories() {
        return notSupportedCategories;
    }

    public void setNotSupportedCategories(List<String> notSupportedCategories) {
        this.notSupportedCategories = notSupportedCategories;
    }

    public boolean isCategorySupported(String categoryId) {
        for (String nscat : notSupportedCategories) {
            if (nscat.equals(categoryId)) return false;
        }
        return true;
    }
}
