package com.privacy.calculator;

import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.content.*;
import android.provider.MediaStore;
import android.net.Uri;
import java.util.*;
import java.io.*;
import androidx.appcompat.app.AppCompatActivity;

public class VaultActivity extends AppCompatActivity {
    
    private LinearLayout tabContainer;
    private static final int PICK_FILE = 1;
    private static final int PICK_IMAGE = 2;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault);
        
        tabContainer = findViewById(R.id.tabContainer);
        
        // 标签按钮
        findViewById(R.id.tabPhotos).setOnClickListener(v -> showPhotos());
        findViewById(R.id.tabFiles).setOnClickListener(v -> showFiles());
        findViewById(R.id.tabApps).setOnClickListener(v -> showApps());
        findViewById(R.id.tabNotes).setOnClickListener(v -> showNotes());
        findViewById(R.id.tabBookmarks).setOnClickListener(v -> showBookmarks());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        // 默认显示照片
        showPhotos();
    }
    
    private void showPhotos() {
        tabContainer.removeAllViews();
        Button btnAdd = new Button(this);
        btnAdd.setText("+ 添加照片");
        btnAdd.setBackgroundColor(0xFF333333);
        btnAdd.setTextColor(0xFFFFFFFF);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });
        tabContainer.addView(btnAdd);
        
        TextView tv = new TextView(this);
        tv.setText("照片保险箱（私密）");
        tv.setTextSize(16);
        tv.setTextColor(0xFFFFFFFF);
        tv.setPadding(0, 20, 0, 0);
        tabContainer.addView(tv);
    }
    
    private void showFiles() {
        tabContainer.removeAllViews();
        Button btnAdd = new Button(this);
        btnAdd.setText("+ 添加文件");
        btnAdd.setBackgroundColor(0xFF333333);
        btnAdd.setTextColor(0xFFFFFFFF);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, PICK_FILE);
        });
        tabContainer.addView(btnAdd);
        
        TextView tv = new TextView(this);
        tv.setText("文件保险箱（私密）");
        tv.setTextSize(16);
        tv.setTextColor(0xFFFFFFFF);
        tv.setPadding(0, 20, 0, 0);
        tabContainer.addView(tv);
    }
    
    private void showApps() {
        tabContainer.removeAllViews();
        TextView tv = new TextView(this);
        tv.setText("已安装应用（点击启动）");
        tv.setTextSize(16);
        tv.setTextColor(0xFFFFFFFF);
        tv.setPadding(0, 0, 0, 20);
        tabContainer.addView(tv);
        
        // 列出已安装应用
        PackageManager pm = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);
        
        for (ResolveInfo app : apps) {
            Button btn = new Button(this);
            btn.setText(app.loadLabel(pm));
            btn.setBackgroundColor(0xFF333333);
            btn.setTextColor(0xFFFFFFFF);
            btn.setPadding(20, 10, 20, 10);
            btn.setOnClickListener(v -> {
                Intent launchIntent = pm.getLaunchIntentForPackage(app.activityInfo.packageName);
                if (launchIntent != null) startActivity(launchIntent);
            });
            tabContainer.addView(btn);
        }
    }
    
    private void showNotes() {
        tabContainer.removeAllViews();
        EditText etNote = new EditText(this);
        etNote.setHint("输入私密笔记...");
        etNote.setTextColor(0xFFFFFFFF);
        etNote.setHintTextColor(0xFF888888);
        etNote.setBackgroundColor(0xFF222222);
        etNote.setPadding(20, 20, 20, 20);
        tabContainer.addView(etNote);
        
        Button btnSave = new Button(this);
        btnSave.setText("保存笔记");
        btnSave.setBackgroundColor(0xFF333333);
        btnSave.setTextColor(0xFFFFFFFF);
        btnSave.setOnClickListener(v -> {
            try {
                FileOutputStream fos = openFileOutput("note_" + System.currentTimeMillis() + ".txt", MODE_PRIVATE);
                fos.write(etNote.getText().toString().getBytes());
                fos.close();
                Toast.makeText(this, "笔记已保存", Toast.LENGTH_SHORT).show();
            } catch (Exception e) { e.printStackTrace(); }
        });
        tabContainer.addView(btnSave);
    }
    
    private void showBookmarks() {
        tabContainer.removeAllViews();
        EditText etUrl = new EditText(this);
        etUrl.setHint("输入网址（如 github.com）");
        etUrl.setTextColor(0xFFFFFFFF);
        etUrl.setHintTextColor(0xFF888888);
        etUrl.setBackgroundColor(0xFF222222);
        etUrl.setPadding(20, 20, 20, 20);
        tabContainer.addView(etUrl);
        
        Button btnOpen = new Button(this);
        btnOpen.setText("打开网页");
        btnOpen.setBackgroundColor(0xFF333333);
        btnOpen.setTextColor(0xFFFFFFFF);
        btnOpen.setOnClickListener(v -> {
            String url = etUrl.getText().toString();
            if (!url.startsWith("http")) url = "https://" + url;
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
                while ((len = is.read(buffer)) > 0) fos.write(buffer, 0, len);
                fos.close();
                is.close();
                Toast.makeText(this, "文件已保存到保险箱", Toast.LENGTH_SHORT).show();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}
