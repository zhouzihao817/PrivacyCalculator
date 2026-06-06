package com.privacy.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.content.*;
import android.content.pm.*;
import android.provider.MediaStore;
import android.net.Uri;
import java.util.*;
import java.io.*;

public class VaultActivity extends Activity {
    
    private LinearLayout tabContainer;
    private static final int PICK_FILE = 1;
    private static final int PICK_IMAGE = 2;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault);
        
        tabContainer = findViewById(R.id.tabContainer);
        
        // tab buttons
        findViewById(R.id.tabPhotos).setOnClickListener(v -> showPhotos());
        findViewById(R.id.tabFiles).setOnClickListener(v -> showFiles());
        findViewById(R.id.tabApps).setOnClickListener(v -> showApps());
        findViewById(R.id.tabNotes).setOnClickListener(v -> showNotes());
        findViewById(R.id.tabBookmarks).setOnClickListener(v -> showBookmarks());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        // default show photos
        showPhotos();
    }
    
    private void showPhotos() {
        tabContainer.removeAllViews();
        
        Button btnAdd = new Button(this);
        btnAdd.setText("+ Add Photo");
        btnAdd.setBackgroundColor(0xFF333333);
        btnAdd.setTextColor(0xFFFFFFFF);
        btnAdd.setPadding(40, 30, 40, 30);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });
        tabContainer.addView(btnAdd);
        
        TextView tv = new TextView(this);
        tv.setText("Photo Vault (Private)");
        tv.setTextSize(16);
        tv.setTextColor(0xFFFFFFFF);
        tv.setPadding(0, 40, 0, 20);
        tabContainer.addView(tv);
    }
    
    private void showFiles() {
        tabContainer.removeAllViews();
        
        Button btnAdd = new Button(this);
        btnAdd.setText("+ Add File");
        btnAdd.setBackgroundColor(0xFF333333);
        btnAdd.setTextColor(0xFFFFFFFF);
        btnAdd.setPadding(40, 30, 40, 30);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, PICK_FILE);
        });
        tabContainer.addView(btnAdd);
        
        TextView tv = new TextView(this);
        tv.setText("File Vault (Private)");
        tv.setTextSize(16);
        tv.setTextColor(0xFFFFFFFF);
        tv.setPadding(0, 40, 0, 20);
        tabContainer.addView(tv);
    }
    
    private void showApps() {
        tabContainer.removeAllViews();
        
        TextView tv = new TextView(this);
        tv.setText("Installed Apps (Click to Launch)");
        tv.setTextSize(16);
        tv.setTextColor(0xFFFFFFFF);
        tv.setPadding(0, 0, 0, 40);
        tabContainer.addView(tv);
        
        // list installed apps
        PackageManager pm = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);
        
        for (ResolveInfo app : apps) {
            Button btn = new Button(this);
            btn.setText(app.loadLabel(pm));
            btn.setBackgroundColor(0xFF333333);
            btn.setTextColor(0xFFFFFFFF);
            btn.setPadding(40, 20, 40, 20);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 10, 0, 10);
            btn.setLayoutParams(params);
            btn.setOnClickListener(v -> {
                Intent launchIntent = pm.getLaunchIntentForPackage(app.activityInfo.packageName);
                if (launchIntent != null) {
                    startActivity(launchIntent);
                }
            });
            tabContainer.addView(btn);
        }
    }
    
    private void showNotes() {
        tabContainer.removeAllViews();
        
        EditText etNote = new EditText(this);
        etNote.setHint("Enter private note...");
        etNote.setTextColor(0xFFFFFFFF);
        etNote.setHintTextColor(0xFF888888);
        etNote.setBackgroundColor(0xFF222222);
        etNote.setPadding(40, 30, 40, 30);
        tabContainer.addView(etNote);
        
        Button btnSave = new Button(this);
        btnSave.setText("Save Note");
        btnSave.setBackgroundColor(0xFF333333);
        btnSave.setTextColor(0xFFFFFFFF);
        btnSave.setPadding(40, 20, 40, 20);
        btnSave.setOnClickListener(v -> {
            try {
                String fileName = "note_" + System.currentTimeMillis() + ".txt";
                FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
                fos.write(etNote.getText().toString().getBytes());
                fos.close();
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        tabContainer.addView(btnSave);
    }
    
    private void showBookmarks() {
        tabContainer.removeAllViews();
        
        EditText etUrl = new EditText(this);
        etUrl.setHint("Enter URL (e.g. github.com)");
        etUrl.setTextColor(0xFFFFFFFF);
        etUrl.setHintTextColor(0xFF888888);
        etUrl.setBackgroundColor(0xFF222222);
        etUrl.setPadding(40, 30, 40, 30);
        tabContainer.addView(etUrl);
        
        Button btnOpen = new Button(this);
        btnOpen.setText("Open Web");
        btnOpen.setBackgroundColor(0xFF333333);
        btnOpen.setTextColor(0xFFFFFFFF);
        btnOpen.setPadding(40, 20, 40, 20);
        btnOpen.setOnClickListener(v -> {
            String url = etUrl.getText().toString();
            if (!url.startsWith("http")) {
                url = "https://" + url;
            }
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        });
        tabContainer.addView(btnOpen);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream is = getContentResolver().openInputStream(uri);
                String fileName = "file_" + System.currentTimeMillis();
                FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                is.close();
                Toast.makeText(this, "File saved to vault", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
