package com.xumengqi.lclab;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {
    /** 活动集中管理器 */
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    /** 结束当前界面，与跳转页面不同 */
    public static void finishActivity(Activity activity) {
        activity.finish();
        ActivityCollector.removeActivity(activity);
    }

    /** 结束所有活动，自动执行销毁 */
    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        activities.clear();
    }
}
