package com.b_lam.resplash.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.b_lam.resplash.R;
import com.b_lam.resplash.fragments.AutoWallpaperFragment;
import com.b_lam.resplash.util.LocaleUtils;
import com.b_lam.resplash.util.ThemeUtils;
import com.b_lam.resplash.util.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AutoWallpaperActivity extends AppCompatActivity implements AutoWallpaperFragment.OnAutoWallpaperFragmentListener {

    private final static String TAG = "AutoWallpaperActivity";

    @BindView(R.id.auto_wallpaper_coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.auto_wallpaper_fab) FloatingActionButton floatingActionButton;
    @BindView(R.id.toolbar_auto_wallpaper) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        switch (ThemeUtils.getTheme(this)) {
            case ThemeUtils.Theme.LIGHT:
                setTheme(R.style.PreferenceThemeLight);
                break;
            case ThemeUtils.Theme.DARK:
                setTheme(R.style.PreferenceThemeDark);
                break;
            case ThemeUtils.Theme.BLACK:
                setTheme(R.style.PreferenceThemeBlack);
                break;
        }

        super.onCreate(savedInstanceState);

        LocaleUtils.loadLocale(this);

        ThemeUtils.setRecentAppsHeaderColor(this);

        setContentView(R.layout.activity_auto_wallpaper);

        ButterKnife.bind(this);

        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material, getTheme());
        upArrow.setColorFilter(ThemeUtils.getThemeAttrColor(this, R.attr.menuIconColor), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.auto_wallpaper_title);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.auto_wallpaper_fragment_container, new AutoWallpaperFragment())
                .commit();

        boolean enabled = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("auto_wallpaper", false);
        setFloatingActionButtonVisibility(enabled);

        floatingActionButton.setOnClickListener(view -> setNewWallpaper());
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        if (fragment instanceof AutoWallpaperFragment) {
            AutoWallpaperFragment autoWallpaperFragment = (AutoWallpaperFragment) fragment;
            autoWallpaperFragment.setOnAutoWallpaperFragmentListener(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAutoWallpaperEnableClicked(boolean enabled) {
        setFloatingActionButtonVisibility(enabled);
    }

    private void setNewWallpaper() {
        AutoWallpaperFragment autoWallpaperFragment = (AutoWallpaperFragment)
                getSupportFragmentManager().findFragmentById(R.id.auto_wallpaper_fragment_container);

        if (autoWallpaperFragment != null) {
            autoWallpaperFragment.scheduleAutoWallpaperJob(PreferenceManager.getDefaultSharedPreferences(this));
            showSnackbar();
        }
    }

    private void setFloatingActionButtonVisibility(boolean visible) {
        if (visible) {
            floatingActionButton.show();
        } else {
            floatingActionButton.hide();
        }
    }

    private void showSnackbar() {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, getString(R.string.setting_wallpaper), Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ThemeUtils.getThemeAttrColor(this, R.attr.colorPrimaryDark));
        snackbar.getView().setElevation(Utils.dpToPx(this, 6));
        snackbar.show();
    }
}
