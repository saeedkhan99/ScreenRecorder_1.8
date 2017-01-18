package com.ami.customui;

import android.content.Context;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.AttributeSet;

/**
 * Created by hi on 5/11/16.
 */
public class ListPreferenceCustom extends ListPreference {
    public ListPreferenceCustom(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public ListPreferenceCustom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ListPreferenceCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ListPreferenceCustom(Context context) {
        super(context);
        init();
    }
    private void init(){
        setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference arg0, Object arg1) {
                arg0.setSummary(getEntry());
                return true;
            }
        });
    }
    @Override
    public CharSequence getSummary() {
        return super.getEntry();
    }
}
