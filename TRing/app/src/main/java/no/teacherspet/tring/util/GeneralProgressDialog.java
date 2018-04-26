package no.teacherspet.tring.util;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/**
 * Created by Eirik on 26-Apr-18.
 */

public class GeneralProgressDialog {

    ProgressBar progressBar;
    Context context;
    Activity activity;
    ViewGroup layout;

    public GeneralProgressDialog(Context context, Activity activity,ViewGroup layout) {
        this.context = context;
        this.activity = activity;
        this.layout = layout;
        progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar, params);
        hide();
    }

    public void show(){
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hide(){
        progressBar.setVisibility(View.GONE);
        setTouchable(true);
    }

    public void setTouchable(boolean bool){
        if(bool){
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }else{
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}
