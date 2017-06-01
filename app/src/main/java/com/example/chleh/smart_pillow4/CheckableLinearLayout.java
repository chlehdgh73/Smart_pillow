package com.example.chleh.smart_pillow4;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * Created by chleh on 2017-05-31.
 */

public class CheckableLinearLayout extends LinearLayout implements Checkable {

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setChecked(boolean checked) {
        CheckBox box =(CheckBox)findViewById(R.id.checkBox);
        if(box.isChecked()!= checked)
        {
            box.setChecked(checked);
        }
    }

    @Override
    public boolean isChecked() {
        CheckBox box =(CheckBox)findViewById(R.id.checkBox);
        return box.isChecked();
    }



    @Override
    public void toggle() {
        CheckBox box =(CheckBox)findViewById(R.id.checkBox);
        setChecked(box.isChecked()?false:true);

    }
}
