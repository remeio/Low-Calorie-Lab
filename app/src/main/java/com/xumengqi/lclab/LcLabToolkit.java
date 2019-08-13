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
    /** 低卡记录界面所获取的用户 */
    private static User user;
    /** 用于低卡商城是否更新的标识 */
    private static boolean readyToUpdate = false;
    /** 用于低卡记录是否更新的标识 */
    private static boolean readyToUpdateRecord = true;

    /**
     * 便于显示统一的Toast
     * @param context 上下文
     * @param string 文本内容
     * @param idOfDrawable 图标
     */
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

    /**
     * 判断网络是否可用
     * @param context 上下文
     * @return 网络可用与否
     */
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

    static void setReadyToUpdate(Boolean readyToUpdateNew) {
        readyToUpdate = readyToUpdateNew;
    }

    static boolean isReadyToUpdate() {
        return readyToUpdate;
    }

    static void setReadyToUpdateRecord(Boolean readyToUpdateRecordNew) {
        readyToUpdateRecord = readyToUpdateRecordNew;
    }

    static boolean isReadyToUpdateRecord() {
        return readyToUpdateRecord;
    }


    static boolean isCacheNotRam() {
        /* 用内存：会OOM */
        /* 用缓存：即返回空，再用glide加载图片 */
        return true;
    }
}