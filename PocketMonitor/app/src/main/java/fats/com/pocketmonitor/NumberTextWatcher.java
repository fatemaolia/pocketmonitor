package fats.com.pocketmonitor;

import android.text.Editable;
import android.text.TextWatcher;

import java.text.NumberFormat;

/**
 *  This class allows us to format the EditText so that it accepts currency inputs
 */

public class NumberTextWatcher implements TextWatcher {

    boolean mEditing;

    public NumberTextWatcher() {
        mEditing = false;
    }

    public synchronized void afterTextChanged(Editable s) {
        if(!mEditing) {
            mEditing = true;

            String digits = s.toString().replaceAll("\\D", "");
            NumberFormat nf = NumberFormat.getCurrencyInstance();
            try{
                String formatted = nf.format(Double.parseDouble(digits)/100);
                s.replace(0, s.length(), formatted);
            } catch (NumberFormatException nfe) {
                s.clear();
            }

            mEditing = false;
        }
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    public void onTextChanged(CharSequence s, int start, int before, int count) { }
}