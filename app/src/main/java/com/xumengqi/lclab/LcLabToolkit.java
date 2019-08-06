package com.xumengqi.lclab;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
class LcLabToolkit {
    private static User user;

    static void showToastHint(Context context, String string, int idOfDrawable) {
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER,0, 0);
        View view = LayoutInflater.from(context).inflate(R.layout.toast, null);
        ImageView ivToast = view.findViewById(R.id.iv_toast);
        ivToast.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), idOfDrawable));
        TextView tvToast = view.findViewById(R.id.tv_toast);
        tvToast.setText(string);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
    static Boolean isConnectingForNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    static void setUser(User userNew) {
        user = userNew;
    }
    static User getUser() {
        return user;
    }
}
