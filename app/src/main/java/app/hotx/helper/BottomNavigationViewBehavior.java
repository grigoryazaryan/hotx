package app.hotx.helper;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import android.view.View;

public class BottomNavigationViewBehavior extends CoordinatorLayout.Behavior<BottomNavigationView> {
final String TAG = "BottomNavigateBehavior";

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, BottomNavigationView child, @NonNull View directTargetChild, @NonNull
            View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull BottomNavigationView child, @NonNull View target, int dxConsumed, int
            dyConsumed, int dxUnconsumed, int dyUnconsumed, @ViewCompat.NestedScrollType int type) {
        child.setTranslationY(Math.max(0f, Math.min(child.getHeight(), child.getTranslationY() + dyConsumed)));
    }
}
