package com.hellobaytree.graftrs.shared.utils;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import com.hellobaytree.graftrs.BuildConfig;

import java.util.List;

public class TextTools {

    public static void log(String tag, String text) {
        boolean isInDebugMode = BuildConfig.DEBUG;
        if (!isInDebugMode) return;

        if (TextUtils.isEmpty(text)) {
            Log.d(tag, String.valueOf(text));
            return;
        }
        int maxLogSize = 1000;
        for (int i = 0; i <= text.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > text.length() ? text.length() : end;
            Log.d(tag, text.substring(start, end));
        }
    }

    public static boolean trimIsEmpty(String value) {
        return (value == null || value.trim().isEmpty());
    }

    public static boolean isNumber(String value) {
        return value != null && !value.replaceAll("\\D", "").isEmpty();
    }

    public static void resetInputLayout(TextInputLayout textInputLayout) {
        textInputLayout.setError(null);
        textInputLayout.setErrorEnabled(false);
    }

    public static String getPath(Context ctx, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = ctx.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(columnIndex);
        cursor.close();
        return s;
    }

    public static String toBulletList(List<String> listItems, boolean vertical) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            for (String string : listItems) {
                stringBuilder.append(Html.fromHtml("<span style=\"color:blue\"> &#8226; </span>") + string + (vertical ? "\n" : " "));
            }
        } catch (Exception e) {
        }
        return stringBuilder.toString();
    }

    public static String toCommaList(List<String> listItems, boolean vertical) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            for (String string : listItems) {
                stringBuilder.append(string + ", " + (vertical ? "\n" : ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }


    public static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public static boolean contains(String value1, CharSequence input) {
        return contains(value1, input.toString());
    }

    public static boolean contains(String value1, String value2) {
        return !(value1 == null || value2 == null)
                && value1.trim().toLowerCase().contains(value2.trim().toLowerCase());
    }
}
