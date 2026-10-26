package com.efes.overlay;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OverlayService extends Service {
    static final String ACTION_SHOW="com.efes.overlay.SHOW", ACTION_HIDE="com.efes.overlay.HIDE";
    WindowManager wm; View bubble, panel; WindowManager.LayoutParams bubbleParams;
    @Override public void onCreate(){super.onCreate(); wm=(WindowManager)getSystemService(WINDOW_SERVICE);}
    @Override public int onStartCommand(Intent i,int flags,int id){
        String a=i==null?ACTION_SHOW:i.getAction();
        if(ACTION_HIDE.equals(a)) removeViews(); else if(Settings.canDrawOverlays(this)) showViews();
        return START_NOT_STICKY;
    }
    GradientDrawable shape(int color,int stroke){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(28);d.setStroke(3,stroke);return d;}
    TextView text(String s,int size){TextView t=new TextView(this);t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(size);t.setGravity(Gravity.CENTER);t.setPadding(18,12,18,12);return t;}
    void showViews(){
        if(bubble!=null)return;
        bubbleParams=new WindowManager.LayoutParams(-2,-2,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);
        bubbleParams.gravity=Gravity.TOP|Gravity.START; bubbleParams.x=20;bubbleParams.y=180;
        TextView b=text("EFES",16);b.setBackground(shape(0xFF202020,0xFFFF3030));bubble=b;
        bubble.setOnClickListener(v->togglePanel());
        bubble.setOnTouchListener(new View.OnTouchListener(){int sx,sy;float dx,dy;boolean moved;
            public boolean onTouch(View v,MotionEvent e){
                if(e.getAction()==MotionEvent.ACTION_DOWN){sx=bubbleParams.x;sy=bubbleParams.y;dx=e.getRawX();dy=e.getRawY();moved=false;return false;}
                if(e.getAction()==MotionEvent.ACTION_MOVE){int x=(int)(e.getRawX()-dx),y=(int)(e.getRawY()-dy);if(Math.abs(x)>8||Math.abs(y)>8)moved=true;if(moved){bubbleParams.x=sx+x;bubbleParams.y=sy+y;wm.updateViewLayout(bubble,bubbleParams);}return moved;}return false;
            }});
        wm.addView(bubble,bubbleParams);
    }
    void togglePanel(){
        if(panel!=null){wm.removeView(panel);panel=null;return;}
        LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(12,10,12,10);box.setBackground(shape(0xEE171717,0xFFFF3030));
        TextView h=text("EFES MENÜ",18);h.setTextColor(0xFFFF4444);box.addView(h);
        box.addView(text("Yardımcı arayüz aktif",14));box.addView(text("Oyuna komut göndermez.",12));
        TextView close=text("KAPAT",14);close.setBackground(shape(0xFF3A1111,0xFFFF3030));close.setOnClickListener(v->togglePanel());box.addView(close);
        WindowManager.LayoutParams p=new WindowManager.LayoutParams(-2,-2,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);
        p.gravity=Gravity.TOP|Gravity.START;p.x=bubbleParams.x;p.y=bubbleParams.y+90;panel=box;wm.addView(panel,p);
    }
    void removeViews(){if(panel!=null){wm.removeView(panel);panel=null;}if(bubble!=null){wm.removeView(bubble);bubble=null;}}
    @Override public void onDestroy(){removeViews();super.onDestroy();}
    @Override public IBinder onBind(Intent i){return null;}
}
