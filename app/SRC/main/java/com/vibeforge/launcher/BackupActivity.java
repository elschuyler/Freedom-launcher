package com.vibeforge.launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class BackupActivity extends Activity {

    private LinearLayout backupList;
    private static final String BACKUP_DIR = "/storage/emulated/0/VibeForge/Backups/";

    public void onCreate(Bundle b) {
        super.onCreate(b);

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
        title.setText("Backup & Restore");
        title.setTextColor(Color.WHITE);
        title.setTextSize(24);
        title.setPadding(20, 0, 0, 0);

        header.addView(back);
        header.addView(title);
        layout.addView(header);

        // Info
        TextView info = new TextView(this);
        info.setText("Backups are saved to:\n" + BACKUP_DIR);
        info.setTextColor(Color.GRAY);
        info.setTextSize(12);
        info.setPadding(10, 0, 10, 20);
        layout.addView(info);

        // Create backup button
        Button createBtn = new Button(this);
        createBtn.setText("💾 Create Backup Now");
        createBtn.setTextColor(Color.WHITE);
        createBtn.setTextSize(16);
        createBtn.setBackgroundColor(Color.parseColor("#1B5E20"));
        createBtn.setPadding(20, 25, 20, 25);
        LinearLayout.LayoutParams createParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        createParams.setMargins(0, 0, 0, 20);
        createBtn.setLayoutParams(createParams);
        createBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { createBackup(); }
        });
        layout.addView(createBtn);

        // Existing backups label
        TextView backupsLabel = new TextView(this);
        backupsLabel.setText("📂 Existing Backups");
        backupsLabel.setTextColor(Color.parseColor("#7986CB"));
        backupsLabel.setTextSize(16);
        backupsLabel.setPadding(10, 10, 10, 10);
        layout.addView(backupsLabel);

        // Backup list
        backupList = new LinearLayout(this);
        backupList.setOrientation(LinearLayout.VERTICAL);
        layout.addView(backupList);

        loadBackups();

        scroll.addView(layout);
        setContentView(scroll);
    }

    private void createBackup() {
        try {
            File dir = new File(BACKUP_DIR);
            if (!dir.exists()) dir.mkdirs();

            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
                .format(new Date());
            String filename = "backup_" + timestamp + ".vf";
            File backupFile = new File(dir, filename);

            // Collect all settings
            SharedPreferences prefs = getSharedPreferences("vibeforge", MODE_PRIVATE);
            SharedPreferences pluginPrefs = getSharedPreferences("vibeforge_plugins", MODE_PRIVATE);

            StringBuilder sb = new StringBuilder();
            sb.append("# VibeForge Backup\n");
            sb.append("# Created: ").append(timestamp).append("\n\n");

            sb.append("[SETTINGS]\n");
            for (Map.Entry<String, ?> entry : prefs.getAll().entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
            }

            sb.append("\n[PLUGINS]\n");
            for (Map.Entry<String, ?> entry : pluginPrefs.getAll().entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
            }

            FileWriter fw = new FileWriter(backupFile);
            fw.write(sb.toString());
            fw.close();

            Toast.makeText(this, "✅ Backup created: " + filename, Toast.LENGTH_LONG).show();
            loadBackups();

        } catch (Exception e) {
            Toast.makeText(this, "❌ Backup failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadBackups() {
        backupList.removeAllViews();
        File dir = new File(BACKUP_DIR);

        if (!dir.exists() || dir.listFiles() == null || dir.listFiles().length == 0) {
            TextView empty = new TextView(this);
            empty.setText("No backups found.\nCreate your first backup above!");
            empty.setTextColor(Color.GRAY);
            empty.setTextSize(14);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(20, 40, 20, 20);
            backupList.addView(empty);
            return;
        }

        File[] files = dir.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File a, File b) {
                return Long.compare(b.lastModified(), a.lastModified());
            }
        });

        for (final File file : files) {
            if (!file.getName().endsWith(".vf")) continue;

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setBackgroundColor(Color.parseColor("#1a1a2e"));
            row.setPadding(20, 15, 10, 15);
            row.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, 2, 0, 2);
            row.setLayoutParams(rowParams);

            LinearLayout info = new LinearLayout(this);
            info.setOrientation(LinearLayout.VERTICAL);
            info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView nameView = new TextView(this);
            nameView.setText(file.getName());
            nameView.setTextColor(Color.WHITE);
            nameView.setTextSize(14);

            TextView sizeView = new TextView(this);
            sizeView.setText(file.length() + " bytes • " +
                new SimpleDateFormat("MMM dd, yyyy", Locale.US).format(new Date(file.lastModified())));
            sizeView.setTextColor(Color.GRAY);
            sizeView.setTextSize(11);

            info.addView(nameView);
            info.addView(sizeView);

            Button restoreBtn = new Button(this);
            restoreBtn.setText("Restore");
            restoreBtn.setTextSize(12);
            restoreBtn.setTextColor(Color.WHITE);
            restoreBtn.setBackgroundColor(Color.parseColor("#1565C0"));
            restoreBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new AlertDialog.Builder(BackupActivity.this)
                        .setTitle("Restore Backup?")
                        .setMessage("This will replace your current settings with: " + file.getName())
                        .setPositiveButton("Restore", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int w) {
                                restoreBackup(file);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                }
            });

            Button deleteBtn = new Button(this);
            deleteBtn.setText("🗑");
            deleteBtn.setTextSize(14);
            deleteBtn.setBackgroundColor(Color.TRANSPARENT);
            deleteBtn.setTextColor(Color.RED);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new AlertDialog.Builder(BackupActivity.this)
                        .setTitle("Delete Backup?")
                        .setMessage("Delete " + file.getName() + "?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int w) {
                                file.delete();
                                loadBackups();
                                Toast.makeText(BackupActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                }
            });

            row.addView(info);
            row.addView(restoreBtn);
            row.addView(deleteBtn);
            backupList.addView(row);
        }
    }

    private void restoreBackup(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            SharedPreferences.Editor prefsEditor = getSharedPreferences("vibeforge", MODE_PRIVATE).edit();
            SharedPreferences.Editor pluginsEditor = getSharedPreferences("vibeforge_plugins", MODE_PRIVATE).edit();

            SharedPreferences.Editor currentEditor = null;
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.isEmpty()) continue;
                if (line.equals("[SETTINGS]")) { currentEditor = prefsEditor; continue; }
                if (line.equals("[PLUGINS]")) { currentEditor = pluginsEditor; continue; }

                if (currentEditor != null && line.contains("=")) {
                    String key = line.substring(0, line.indexOf("="));
                    String value = line.substring(line.indexOf("=") + 1);

                    if (value.equals("true") || value.equals("false")) {
                        currentEditor.putBoolean(key, Boolean.parseBoolean(value));
                    } else {
                        currentEditor.putString(key, value);
                    }
                }
            }

            reader.close();
            prefsEditor.apply();
            pluginsEditor.apply();

            Toast.makeText(this, "✅ Backup restored!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "❌ Restore failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
