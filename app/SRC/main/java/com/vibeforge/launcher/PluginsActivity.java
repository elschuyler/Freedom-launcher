package com.vibeforge.launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class PluginsActivity extends Activity {

    private SharedPreferences prefs;
    private LinearLayout pluginList;

    // Available plugins
    private String[][] availablePlugins = {
        {"Weather Widget", "Show weather on home screen", "weather"},
        {"Battery Bar", "Battery indicator at top", "battery"},
        {"Step Counter", "Show daily steps", "steps"},
        {"Music Controls", "Media controls on home screen", "music"},
        {"Quick Notes", "Sticky note on home screen", "notes"},
        {"Calendar Widget", "Show upcoming events", "calendar"},
        {"System Monitor", "CPU and RAM usage", "sysmon"},
        {"Gesture Shortcuts", "Custom gesture actions", "gestures"}
    };

    public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("vibeforge_plugins", MODE_PRIVATE);

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
        title.setText("Plugins");
        title.setTextColor(Color.WHITE);
        title.setTextSize(26);
        title.setPadding(20, 0, 0, 0);

        header.addView(back);
        header.addView(title);
        layout.addView(header);

        // Description
        TextView desc = new TextView(this);
        desc.setText("Extend VibeForge with powerful plugins. Toggle to enable or disable.");
        desc.setTextColor(Color.GRAY);
        desc.setTextSize(13);
        desc.setPadding(10, 0, 10, 20);
        layout.addView(desc);

        // Plugin list
        pluginList = new LinearLayout(this);
        pluginList.setOrientation(LinearLayout.VERTICAL);

        for (String[] plugin : availablePlugins) {
            layout.addView(createPluginCard(plugin[0], plugin[1], plugin[2]));
        }

        // Get more plugins button
        Button getMore = new Button(this);
        getMore.setText("🔍 Browse More Plugins");
        getMore.setTextColor(Color.WHITE);
        getMore.setBackgroundColor(Color.parseColor("#3949AB"));
        getMore.setPadding(20, 20, 20, 20);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(0, 30, 0, 0);
        getMore.setLayoutParams(btnParams);
        getMore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(PluginsActivity.this,
                    "Plugin store coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

        layout.addView(pluginList);
        layout.addView(getMore);

        scroll.addView(layout);
        setContentView(scroll);
    }

    private LinearLayout createPluginCard(final String name, final String desc, final String key) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setBackgroundColor(Color.parseColor("#1a1a2e"));
        card.setPadding(20, 20, 20, 20);
        card.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 2, 0, 2);
        card.setLayoutParams(params);

        // Text info
        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView nameView = new TextView(this);
        nameView.setText(name);
        nameView.setTextColor(Color.WHITE);
        nameView.setTextSize(16);

        TextView descView = new TextView(this);
        descView.setText(desc);
        descView.setTextColor(Color.GRAY);
        descView.setTextSize(12);

        info.addView(nameView);
        info.addView(descView);

        // Toggle
        boolean enabled = prefs.getBoolean(key, false);
        Switch toggle = new Switch(this);
        toggle.setChecked(enabled);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
                prefs.edit().putBoolean(key, isChecked).apply();
                String msg = name + (isChecked ? " enabled" : " disabled");
                Toast.makeText(PluginsActivity.this, msg, Toast.LENGTH_SHORT).show();

                if (isChecked) {
                    // Show plugin settings option
                    new AlertDialog.Builder(PluginsActivity.this)
                        .setTitle(name + " enabled!")
                        .setMessage("Would you like to configure this plugin?")
                        .setPositiveButton("Configure", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int w) {
                                android.content.Intent intent = new android.content.Intent(
                                    PluginsActivity.this, PluginSettingsActivity.class);
                                intent.putExtra("plugin_key", key);
                                intent.putExtra("plugin_name", name);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Later", null)
                        .show();
                }
            }
        });

        card.addView(info);
        card.addView(toggle);
        return card;
    }
}
