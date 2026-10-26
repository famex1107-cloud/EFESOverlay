package com.efes.overlay;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    static final int REQUEST_OVERLAY = 1001;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL); layout.setPadding(32,48,32,32); layout.setBackgroundColor(0xFF151515);
        TextView title = new TextView(this); title.setText("EFES YARDIMCI MENÜ"); title.setTextColor(0xFFFF3333); title.setTextSize(24); title.setPadding(0,0,0,20); layout.addView(title);
        TextView info = new TextView(this); info.setText("Bu uygulama yalnızca yardımcı arayüz gösterir. Oyunu kontrol etmez veya otomatik farm yapmaz."); info.setTextColor(0xFFFFFFFF); info.setTextSize(16); info.setPadding(0,0,0,24); layout.addView(info);
        Button show = new Button(this); show.setText("Yüzen menüyü göster"); layout.addView(show);
        Button hide = new Button(this); hide.setText("Yüzen menüyü gizle"); layout.addView(hide);
        setContentView(layout);
        show.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), REQUEST_OVERLAY);
            } else startService(new Intent(this, OverlayService.class).setAction(OverlayService.ACTION_SHOW));
        });
        hide.setOnClickListener(v -> startService(new Intent(this, OverlayService.class).setAction(OverlayService.ACTION_HIDE)));
    }
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_OVERLAY && Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(this))
            startService(new Intent(this, OverlayService.class).setAction(OverlayService.ACTION_SHOW));
    }
}
