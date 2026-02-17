package com.vibeforge.launcher;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {

    private GridView appGrid;
    private EditText searchBar;
    private List<AppInfo> allApps = new ArrayList<AppInfo>();
    private List<AppInfo> filteredApps = new ArrayList<AppInfo>();
    private AppAdapter adapter;
    private TextView clockView;
    private TextView dateView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Root layout
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.parseColor("#CC000000"));

        // Try to set wallpaper
        try {
            WallpaperManager wm = WallpaperManager.getInstance(this);
            Drawable wallpaper = wm.getDrawable();
            root.setBackground(wallpaper);
        } catch (Exception e) {
            root.setBackgroundColor(Color.parseColor("#1a1a2e"));
        }

        // Top bar
        LinearLayout topBar = new LinearLayout(this);
        topBar.setId(View.generateViewId());
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setBackgroundColor(Color.parseColor("#AA000000"));
        topBar.setPadding(20, 40, 20, 10);
        topBar.setGravity(Gravity.CENTER_VERTICAL);

        // Clock
        LinearLayout clockLayout = new LinearLayout(this);
        clockLayout.setOrientation(LinearLayout.VERTICAL);

        clockView = new TextView(this);
        clockView.setText("00:00");
        clockView.setTextColor(Color.WHITE);
        clockView.setTextSize(28);

        dateView = new TextView(this);
        dateView.setText("Loading...");
        dateView.setTextColor(Color.LTGRAY);
        dateView.setTextSize(13);

        clockLayout.addView(clockView);
        clockLayout.addView(dateView);

        // Settings button
        Button settingsBtn = new Button(this);
        settingsBtn.setText("⚙");
        settingsBtn.setTextSize(22);
        settingsBtn.setTextColor(Color.WHITE);
        settingsBtn.setBackgroundColor(Color.TRANSPARENT);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        topBar.addView(clockLayout, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        topBar.addView(settingsBtn);

        RelativeLayout.LayoutParams topParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT);
        topParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        root.addView(topBar, topParams);

        // Search bar
        searchBar = new EditText(this);
        searchBar.setId(View.generateViewId());
        searchBar.setHint("Search apps...");
        searchBar.setHintTextColor(Color.GRAY);
        searchBar.setTextColor(Color.WHITE);
        searchBar.setBackgroundColor(Color.parseColor("#AA333333"));
        searchBar.setPadding(30, 20, 30, 20);
        searchBar.setTextSize(16);
        searchBar.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterApps(s.toString());
            }
            public void afterTextChanged(Editable s) {}
        });

        RelativeLayout.LayoutParams searchParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT);
        searchParams.addRule(RelativeLayout.BELOW, topBar.getId());
        searchParams.setMargins(20, 10, 20, 10);
        root.addView(searchBar, searchParams);

        // App grid
        appGrid = new GridView(this);
        appGrid.setId(View.generateViewId());
        appGrid.setNumColumns(4);
        appGrid.setVerticalSpacing(20);
        appGrid.setHorizontalSpacing(10);
        appGrid.setPadding(15, 15, 15, 150);
        appGrid.setBackgroundColor(Color.TRANSPARENT);

        RelativeLayout.LayoutParams gridParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT);
        gridParams.addRule(RelativeLayout.BELOW, searchBar.getId());
        root.addView(appGrid, gridParams);

        setContentView(root);

        loadApps();
        startClock();
    }

    private void filterApps(String query) {
        filteredApps.clear();
        if (query.isEmpty()) {
            filteredApps.addAll(allApps);
        } else {
            String lower = query.toLowerCase();
            for (AppInfo app : allApps) {
                if (app.label.toLowerCase().contains(lower)) {
                    filteredApps.add(app);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadApps() {
        allApps.clear();
        PackageManager pm = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);

        for (ResolveInfo info : list) {
            AppInfo app = new AppInfo();
            app.label = info.loadLabel(pm).toString();
            app.icon = info.loadIcon(pm);
            app.packageName = info.activityInfo.packageName;
            app.intent = pm.getLaunchIntentForPackage(app.packageName);
            if (app.intent != null) allApps.add(app);
        }

        Collections.sort(allApps, new Comparator<AppInfo>() {
            public int compare(AppInfo a, AppInfo b) {
                return a.label.compareToIgnoreCase(b.label);
            }
        });

        filteredApps.addAll(allApps);
        adapter = new AppAdapter();
        appGrid.setAdapter(adapter);
        appGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                try {
                    startActivity(filteredApps.get(pos).intent);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Cannot open app", Toast.LENGTH_SHORT).show();
                }
            }
        });

        appGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
                AppInfo app = filteredApps.get(pos);
                showAppOptions(app);
                return true;
            }
        });
    }

    private void showAppOptions(final AppInfo app) {
        String[] options = {"Open", "App Info", "Uninstall"};
        new android.app.AlertDialog.Builder(this)
            .setTitle(app.label)
            .setItems(options, new android.content.DialogInterface.OnClickListener() {
                public void onClick(android.content.DialogInterface dialog, int which) {
                    if (which == 0) {
                        startActivity(app.intent);
                    } else if (which == 1) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(android.net.Uri.parse("package:" + app.packageName));
                        startActivity(intent);
                    } else if (which == 2) {
                        Intent intent = new Intent(Intent.ACTION_DELETE);
                        intent.setData(android.net.Uri.parse("package:" + app.packageName));
                        startActivity(intent);
                    }
                }
            })
            .show();
    }

    private void startClock() {
        final android.os.Handler handler = new android.os.Handler();
        handler.post(new Runnable() {
            public void run() {
                java.util.Calendar cal = java.util.Calendar.getInstance();
                int hour = cal.get(java.util.Calendar.HOUR_OF_DAY);
                int min = cal.get(java.util.Calendar.MINUTE);
                clockView.setText(String.format("%02d:%02d", hour, min));

                String[] days = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
                String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
                String day = days[cal.get(java.util.Calendar.DAY_OF_WEEK) - 1];
                String month = months[cal.get(java.util.Calendar.MONTH)];
                int date = cal.get(java.util.Calendar.DAY_OF_MONTH);
                dateView.setText(day + ", " + month + " " + date);

                handler.postDelayed(this, 1000);
            }
        });
    }

    public void onResume() {
        super.onResume();
        loadApps();
    }

    class AppInfo {
        String label;
        Drawable icon;
        Intent intent;
        String packageName;
    }

    class AppAdapter extends BaseAdapter {
        public int getCount() { return filteredApps.size(); }
        public Object getItem(int pos) { return filteredApps.get(pos); }
        public long getItemId(int pos) { return pos; }

        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout item = new LinearLayout(MainActivity.this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(Gravity.CENTER);
            item.setPadding(8, 12, 8, 12);

            ImageView icon = new ImageView(MainActivity.this);
            icon.setImageDrawable(filteredApps.get(position).icon);
            icon.setLayoutParams(new LinearLayout.LayoutParams(130, 130));
            icon.setScaleType(ImageView.ScaleType.FIT_CENTER);

            TextView label = new TextView(MainActivity.this);
            label.setText(filteredApps.get(position).label);
            label.setTextColor(Color.WHITE);
            label.setTextSize(11);
            label.setMaxLines(2);
            label.setEllipsize(android.text.TextUtils.TruncateAt.END);
            label.setGravity(Gravity.CENTER);
            label.setPadding(0, 6, 0, 0);

            item.addView(icon);
            item.addView(label);
            return item;
        }
    }
}
