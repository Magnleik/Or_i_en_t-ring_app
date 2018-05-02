package no.teacherspet.tring.util;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import no.teacherspet.tring.R;

/**
 * Created by Eirik on 26-Apr-18.
 */

public class GeneralProgressDialog {

    private FragmentManager fm;
    private MyDialogFragment dialogFragment;
    private boolean blocking = false;

    public GeneralProgressDialog(Context context, Activity activity, boolean blocking) {
        Context context1 = context;
        Activity activity1 = activity;
        this.blocking = blocking;
        ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
        fm = activity.getFragmentManager();
        dialogFragment = new MyDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("blocking", blocking);
        dialogFragment.setArguments(bundle);
        //show();
    }

    public GeneralProgressDialog(Context context, Activity activity){
        this(context,activity,false);
    }

    public void show(){

        if(dialogFragment.isAdded()) {
            dialogFragment.dismiss();
        }
        dialogFragment.show(fm, "loading");
        //setTouchable(!blocking);
    }

    public void hide(){
        dialogFragment.dismiss();
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }


    public static class MyDialogFragment extends DialogFragment{

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_progressbar_dialog,container,false);
            getDialog().setCanceledOnTouchOutside(!this.getArguments().getBoolean("blocking",false));
            return rootView;
        }
    }
}
