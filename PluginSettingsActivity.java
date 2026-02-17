package com.vibeforge.launcher;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class PluginSettingsActivity extends Activity {

    public void onCreate(Bundle b) {
        super.onCreate(b);

        String pluginKey = getIntent().getStringExtra("plugin_key");
        String pluginName = getIntent().getStringExtra("plugin_name");
        if (pluginName == null) pluginName = "Plugin";

        final SharedPreferences prefs = getSharedPreferences("vibeforge_plugins", MODE_PRIVATE);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(Color.parseColor("#0f0f0f"));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 40);

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(0, 30, 0, 20);

        Button back = new Button(this);
        back.setText("←");
        back.setTextColor(Color.WHITE);
        back.setTextSize(20);
        back.setBackgroundColor(Color.TRANSPARENT);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { finish(); }
        });

        TextView title = new TextView(this);
        title.setText(pluginName + " Settings");
        title.setTextColor(Color.WHITE);
        title.setTextSize(22);
        title.setPadding(20, 0, 0, 0);

        header.addView(back);
        header.addView(title);
        layout.addView(header);

        // Plugin-specific settings based on key
        if ("weather".equals(pluginKey)) {
            layout.addView(settingItem("Location", "Auto-detect location"));
            layout.addView(settingItem("Units", "Celsius / Fahrenheit"));
            layout.addView(settingItem("Update interval", "Every 30 minutes"));
            layout.addView(settingItem("Show forecast", "Next 3 days"));
        } else if ("battery".equals(pluginKey)) {
            layout.addView(settingItem("Bar position", "Top of screen"));
            layout.addView(settingItem("Bar height", "4px"));
            layout.addView(settingItem("Color style", "Gradient"));
            layout.addView(settingItem("Show percentage", "Yes"));
        } else if ("music".equals(pluginKey)) {
            layout.addView(settingItem("Position", "Bottom of screen"));
            layout.addView(settingItem("Show album art", "Yes"));
            layout.addView(settingItem("Style", "Compact"));
        } else if ("notes".equals(pluginKey)) {
            layout.addView(settingItem("Position", "Top right corner"));
            layout.addView(settingItem("Background color", "Yellow"));
            layout.addView(settingItem("Font size", "14sp"));
        } else if ("calendar".equals(pluginKey)) {
            layout.addView(settingItem("Events to show", "3"));
            layout.addView(settingItem("Show time", "Yes"));
            layout.addView(settingItem("Calendar account", "All calendars"));
        } else if ("sysmon".equals(pluginKey)) {
            layout.addView(settingItem("Show CPU", "Yes"));
            layout.addView(settingItem("Show RAM", "Yes"));
            layout.addView(settingItem("Show Storage", "Yes"));
            layout.addView(settingItem("Update interval", "Every 5 seconds"));
        } else {
            TextView coming = new TextView(this);
            coming.setText("Plugin settings coming soon!");
            coming.setTextColor(Color.GRAY);
            coming.setTextSize(16);
            coming.setGravity(Gravity.CENTER);
            coming.setPadding(20, 60, 20, 20);
            layout.addView(coming);
        }

        scroll.addView(layout);
        setContentView(scroll);
    }

    private LinearLayout settingItem(String title, String value) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setBackgroundColor(Color.parseColor("#1a1a2e"));
        row.setPadding(20, 20, 20, 20);
        row.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 2, 0, 2);
        row.setLayoutParams(params);

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextColor(Color.WHITE);
        titleView.setTextSize(15);
        titleView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView valueView = new TextView(this);
        valueView.setText(value);
        valueView.setTextColor(Color.GRAY);
        valueView.setTextSize(13);

        row.addView(titleView);
        row.addView(valueView);
        return row;
    }
}
