package com.imagepicker.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.react.bridge.ReadableMap;
import com.imagepicker.R;

import java.util.List;

/**
 * Created by rolanddenhertog on 24/04/2018.
 */

public class ButtonAdapter extends ArrayAdapter<String> {

    private List<String> _titles;
    private ReadableMap _options;

    public ButtonAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ButtonAdapter(Context context, int resource, List<String> titles, ReadableMap options) {
        super(context, resource, titles);

        _titles = titles;
        _options = options;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item, null);
        }

        String p = getItem(position);

        if (p != null) {
            TextView text = (TextView) v.findViewById(R.id.text);
            Typeface typeface = null;
            if (ReadableMapUtils.hasAndNotEmptyString(_options, "buttonFont")) {
                typeface = Typeface.createFromAsset(getContext().getResources().getAssets(), "fonts/" + _options.getString("buttonFont") + ".ttf");
                if (typeface != null) {
                    text.setTypeface(typeface);
                }
                if (_options.hasKey("buttonFontSize")) {
                    text.setTextSize(_options.getInt("buttonFontSize"));
                }
                if (_options.hasKey("buttonFontColor")) {
                    text.setTextColor(_options.getInt("buttonFontColor"));
                }

            }
            text.setText(_titles.get(position));
        }

        return v;
    }

}
