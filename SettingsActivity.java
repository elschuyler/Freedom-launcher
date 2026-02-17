package com.vibeforge.launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class SettingsActivity extends Activity {

    private SharedPreferences prefs;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("vibeforge", MODE_PRIVATE);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(Color.parseColor("#0f0f0f"));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 40);

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(0, 30, 0, 30);

        Button back = new Button(this);
        back.setText("←");
        back.setTextColor(Color.WHITE);
        back.setTextSize(20);
        back.setBackgroundColor(Color.TRANSPARENT);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { finish(); }
        });

        TextView title = new TextView(this);
        title.setText("Settings");
        title.setTextColor(Color.WHITE);
        title.setTextSize(26);
        title.setPadding(20, 0, 0, 0);

        header.addView(back);
        header.addView(title);
        layout.addView(header);

        // APPEARANCE SECTION
        layout.addView(sectionHeader("🎨 Appearance"));

        // Theme
        layout.addView(settingRow("Theme", prefs.getString("theme", "Dark"), new View.OnClickListener() {
            public void onClick(View v) {
                String[] themes = {"Dark", "Light", "AMOLED", "Ocean", "Sunset"};
                new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Choose Theme")
                    .setItems(themes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface d, int which) {
                            prefs.edit().putString("theme", themes[which]).apply();
                            Toast.makeText(SettingsActivity.this, "Theme: " + themes[which], Toast.LENGTH_SHORT).show();
                        }
                    }).show();
            }
        }));

        // Grid size
        layout.addView(settingRow("Grid Size", prefs.getInt("grid_cols", 4) + " columns", new View.OnClickListener() {
            public void onClick(View v) {
                String[] sizes = {"3 columns", "4 columns", "5 columns", "6 columns"};
                new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Grid Size")
                    .setItems(sizes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface d, int which) {
                            int cols = which + 3;
                            prefs.edit().putInt("grid_cols", cols).apply();
                            Toast.makeText(SettingsActivity.this, "Grid: " + cols + " columns", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
            }
        }));

        // Icon size
        layout.addView(settingRow("Icon Size", prefs.getString("icon_size", "Medium"), new View.OnClickListener() {
            public void onClick(View v) {
                String[] sizes = {"Small", "Medium", "Large"};
                new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Icon Size")
                    .setItems(sizes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface d, int which) {
                            prefs.edit().putString("icon_size", sizes[which]).apply();
                            Toast.makeText(SettingsActivity.this, "Icon size: " + sizes[which], Toast.LENGTH_SHORT).show();
                        }
                    }).show();
            }
        }));

        // Show clock toggle
        layout.addView(toggleRow("Show Clock", prefs.getBoolean("show_clock", true), "show_clock"));

        // Show date toggle
        layout.addView(toggleRow("Show Date", prefs.getBoolean("show_date", true), "show_date"));

        // PLUGINS SECTION
        layout.addView(sectionHeader("🔌 Plugins"));
        layout.addView(settingRow("Manage Plugins", "View & install plugins", new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, PluginsActivity.class));
            }
        }));

        // GESTURES SECTION
        layout.addView(sectionHeader("👆 Gestures"));
        layout.addView(settingRow("Swipe Up", prefs.getString("swipe_up", "App Drawer"), new View.OnClickListener() {
            public void onClick(View v) {
                String[] actions = {"App Drawer", "Search", "Nothing"};
                new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Swipe Up Action")
                    .setItems(actions, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface d, int which) {
                            prefs.edit().putString("swipe_up", actions[which]).apply();
                        }
                    }).show();
            }
        }));

        layout.addView(settingRow("Double Tap", prefs.getString("double_tap", "Lock Screen"), new View.OnClickListener() {
            public void onClick(View v) {
                String[] actions = {"Lock Screen", "Nothing", "Settings"};
                new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Double Tap Action")
                    .setItems(actions, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface d, int which) {
                            prefs.edit().putString("double_tap", actions[which]).apply();
                        }
                    }).show();
            }
        }));

        // BACKUP SECTION
        layout.addView(sectionHeader("💾 Backup & Restore"));
        layout.addView(settingRow("Backup Settings", "Save your settings", new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, BackupActivity.class));
            }
        }));

        // ABOUT SECTION
        layout.addView(sectionHeader("ℹ️ About"));
        layout.addView(settingRow("Version", "VibeForge v1.0", null));
        layout.addView(settingRow("Developer", "Built with ❤️ using Termux", null));

        scroll.addView(layout);
        setContentView(scroll);
    }

    private TextView sectionHeader(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.parseColor("#7986CB"));
        tv.setTextSize(14);
        tv.setPadding(10, 30, 10, 10);
        return tv;
    }

    private LinearLayout settingRow(String title, String subtitle, final View.OnClickListener listener) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(20, 20, 20, 20);
        row.setBackgroundColor(Color.parseColor("#1a1a2e"));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 2, 0, 2);
        row.setLayoutParams(params);

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextColor(Color.WHITE);
        titleView.setTextSize(16);

        TextView subtitleView = new TextView(this);
        subtitleView.setText(subtitle);
        subtitleView.setTextColor(Color.GRAY);
        subtitleView.setTextSize(13);

        row.addView(titleView);
        row.addView(subtitleView);

        if (listener != null) {
            row.setOnClickListener(listener);
        }

        return row;
    }

    private LinearLayout toggleRow(String title, boolean currentValue, final String prefKey) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(20, 20, 20, 20);
        row.setBackgroundColor(Color.parseColor("#1a1a2e"));
        row.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 2, 0, 2);
        row.setLayoutParams(params);

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextColor(Color.WHITE);
        titleView.setTextSize(16);
        titleView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        Switch toggle = new Switch(this);
        toggle.setChecked(currentValue);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean(prefKey, isChecked).apply();
            }
        });

        row.addView(titleView);
        row.addView(toggle);
        return row;
    }
}
