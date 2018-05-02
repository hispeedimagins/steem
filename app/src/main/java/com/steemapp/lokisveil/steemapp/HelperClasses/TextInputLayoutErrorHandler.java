package com.steemapp.lokisveil.steemapp.HelperClasses;

import android.support.design.widget.TextInputLayout;

/**
 * Created by boot on 3/4/2018.
 */

public class TextInputLayoutErrorHandler {
    public TextInputLayout layout;
    public TextInputLayoutErrorHandler(TextInputLayout layout){
        this.layout = layout;
    }

    public void addError(String error){
        layout.setError(error);
    }

    public void clearError(){
        layout.setError(null);
    }

    public void setHint(String hint){
        layout.setHint(hint);
    }

    public void addMaxLimit(int charnum){
        layout.setCounterEnabled(true);
        layout.setCounterMaxLength(charnum);
    }


}
