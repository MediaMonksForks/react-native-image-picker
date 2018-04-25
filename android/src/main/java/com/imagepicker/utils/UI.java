package com.imagepicker.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.react.bridge.ReadableMap;
import com.imagepicker.ImagePickerModule;
import com.imagepicker.R;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author Alexander Ustinov
 */
public class UI {
    public static @NonNull
    AlertDialog chooseDialog(@Nullable final ImagePickerModule module,
                             @NonNull final ReadableMap options,
                             @Nullable final OnAction callback) {
        final Context context = module.getActivity();
        final float density = context.getResources().getDisplayMetrics().density;

        if (context == null) {
            return null;
        }
        final WeakReference<ImagePickerModule> reference = new WeakReference<>(module);

        final ButtonsHelper buttons = ButtonsHelper.newInstance(options);
        final List<String> titles = buttons.getTitles();
        final List<String> actions = buttons.getActions();

        TextView title = new TextView(context);
        Typeface typeface = null;
        if (ReadableMapUtils.hasAndNotEmptyString(options, "titleFont")) {
            typeface = Typeface.createFromAsset(context.getResources().getAssets(), "fonts/" + options.getString("titleFont") + ".ttf");
            if (typeface != null) {
                title.setTypeface(typeface);
            }
        }
        if (options.hasKey("titleFontSize")) {
            title.setTextSize(options.getInt("titleFontSize"));
        }
        if (options.hasKey("titleColor")) {
            title.setTextColor(options.getInt("titleColor"));
        }
        title.setText(options.getString("title"));
        title.setHeight((int) (70 * density));
        title.setGravity(Gravity.CENTER);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DataPickerTheme);
        builder.setCustomTitle(title);

        View view = module.getActivity().getLayoutInflater().inflate(R.layout.dialog_buttons, null);
        builder.setView(view);

        builder.setNegativeButton(buttons.btnCancel.title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callback.onCancel(reference.get());
                dialogInterface.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();

        ListView list = (ListView) view.findViewById(R.id.button_list);
        if (list != null) {
            ButtonAdapter adapter = new ButtonAdapter(context, R.layout.list_item, titles, options);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> list, View v, int pos, long id) {
                    final String action = actions.get(pos);

                    switch (action) {
                        case "photo":
                            callback.onTakePhoto(reference.get());
                            break;

                        case "library":
                            callback.onUseLibrary(reference.get());
                            break;

                        case "cancel":
                            callback.onCancel(reference.get());
                            break;

                        default:
                            callback.onCustomButton(reference.get(), action);
                    }
                }
            });

            if (options.hasKey("dividerColor")) {
                list.setDivider(new ColorDrawable(options.getInt("dividerColor")));
                list.setDividerHeight(1);
            }
        }

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(@NonNull final DialogInterface dialog) {
                callback.onCancel(reference.get());
                dialog.dismiss();
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Window view = ((AlertDialog) dialog).getWindow();
                if (view != null && options.hasKey("backgroundColor")) {
                    Drawable dr = ContextCompat.getDrawable(context, R.drawable.dialog_background_margin);
                    dr.setColorFilter(new PorterDuffColorFilter(options.getInt("backgroundColor"), PorterDuff.Mode.MULTIPLY));
                    view.setBackgroundDrawable(dr);
                }

                Typeface typeface = null;
                if (ReadableMapUtils.hasAndNotEmptyString(options, "cancelButtonFont")) {
                    typeface = Typeface.createFromAsset(context.getResources().getAssets(), "fonts/" + options.getString("cancelButtonFont") + ".ttf");
                }

                Button negativeButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                if (negativeButton != null) {
                    negativeButton.setTransformationMethod(null);

                    if (typeface != null) {
                        negativeButton.setTypeface(typeface);
                    }
                    if (options.hasKey("cancelButtonFontSize")) {
                        negativeButton.setTextSize(options.getInt("cancelButtonFontSize"));
                    }
                    if (options.hasKey("cancelButtonFontColor")) {
                        negativeButton.setTextColor(options.getInt("cancelButtonFontColor"));
                    }
                }
            }
        });

        return dialog;
    }

    public interface OnAction {
        void onTakePhoto(@Nullable ImagePickerModule module);

        void onUseLibrary(@Nullable ImagePickerModule module);

        void onCancel(@Nullable ImagePickerModule module);

        void onCustomButton(@Nullable ImagePickerModule module, String action);
    }
}
