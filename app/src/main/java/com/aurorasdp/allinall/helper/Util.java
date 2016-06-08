package com.aurorasdp.allinall.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aurorasdp.allinall.R;
import com.mobsandgeeks.saripaar.ValidationError;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by AAshour on 5/15/2016.
 */
public class Util {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected())
            return true;
        else {
            Toast.makeText(context, R.string.no_connection_error_msg, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public static void onValidationFailed(Context context, List<ValidationError> errors) {
        for (int i = 0; i < errors.size(); i++) {
            ValidationError validationError = errors.get(i);
            String message = validationError.getCollatedErrorMessage(context);
            if (validationError.getView() instanceof EditText) {
                validationError.getView().requestFocus();
                ((EditText) validationError.getView()).setError(message);
            } else {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void requestFailed(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.error_title)
                .setMessage(context.getResources().getString(R.string.error_title))
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        }
                )
                .show();
    }

    public static String md5(String password) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return password;
    }

    public static RoundedBitmapDrawable createRoundedBitmapDrawable(Bitmap bitmap, Resources resources) {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int bitmapRadius = Math.min(bitmapWidth, bitmapHeight) / 2;
        int bitmapSquareWidth = Math.min(bitmapWidth, bitmapHeight);
        int newBitmapSquareWidth = bitmapSquareWidth;
        Bitmap roundedBitmap = Bitmap.createBitmap(newBitmapSquareWidth, newBitmapSquareWidth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundedBitmap);
        int x = bitmapSquareWidth - bitmapWidth;
        int y = bitmapSquareWidth - bitmapHeight;
        canvas.drawBitmap(bitmap, x, y, null);
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getWidth() / 2, newBitmapSquareWidth / 2, borderPaint);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, roundedBitmap);
        roundedBitmapDrawable.setCornerRadius(bitmapRadius);
        roundedBitmapDrawable.setAntiAlias(true);
        return roundedBitmapDrawable;
    }

    public static Bitmap getBitmapByUri(Context context, Uri bitmapUri) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = context.getContentResolver().openInputStream(bitmapUri);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "Can not load image", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(context, "Can not load image", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri) {
        int orientation = getOrientation(context, photoUri);
        Bitmap srcBitmap = getBitmapByUri(context, photoUri);
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);
            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }
        return srcBitmap;
    }

    public static int getOrientation(Context context, Uri photoUri) {
    /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);
        if (cursor.getCount() != 1) {
            return -1;
        }
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public static TextView getEmptyView(int text, Context context) {
        TextView emptyView = new TextView(context);
        emptyView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        emptyView.setText(text);
        emptyView.setTextSize(20);
        emptyView.setVisibility(View.GONE);
        emptyView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        return emptyView;
    }

    public static String getDateTime(String date, String time) {
        String reformatedDateString = "";
        try {
            String oldFormat = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat oldSDF = new SimpleDateFormat(oldFormat);
            int day = oldSDF.parse(date + " " + time).getDate();
            String dayNumberSuffix = getDayNumberSuffix(day);
            String newFormat = "d'" + dayNumberSuffix + "' MMM yyyy & hh:mm aa";
            SimpleDateFormat newSDF = new SimpleDateFormat(newFormat);
//            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            reformatedDateString = newSDF.format(oldSDF.parse(date + " " + time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformatedDateString;
    }

    private static String getDayNumberSuffix(int day) {
        switch (day) {
            case 1:
            case 21:
            case 31:
                return "st";

            case 2:
            case 22:
                return "nd";

            case 3:
            case 23:
                return "rd";

            default:
                return "th";
        }
    }

    public static String generateRandom() {
        Random ran = new Random();
        int top = 6;
        char data = ' ';
        String dat = "";

        for (int i = 0; i <= top; i++) {
            data = (char) (ran.nextInt(25) + 97);
            dat = data + dat;
        }
        return dat;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);

    }


}
