package no.teacherspet.tring;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class PopupPointDesc extends Activity {

    public boolean savePoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savePoint=false;
        setContentView(R.layout.activity_popup_point_desc);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.3));
        EditText name = (EditText) findViewById(R.id.set_point_name);
        name.setText("Punkt");
        name.selectAll();
        InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(name,InputMethodManager.SHOW_IMPLICIT);
    }

    public void onOkClick(View v){
        savePoint = true;
        finish();
    }
    public void onCancelClick(View v){
        finish();
    }
}
