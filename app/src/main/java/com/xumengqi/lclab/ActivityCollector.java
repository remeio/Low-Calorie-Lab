package com.xumengqi.lclab;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
class ActivityCollector {
    /** 活动集中管理器 */
    private static List<Activity> activities = new ArrayList<>();
    
    static void addActivity(Activity activity) {
        activities.add(activity);
    }

    static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    /** 结束当前界面，与跳转页面不同 */
    static void finishActivity(Activity activity) {
        activity.finish();
        ActivityCollector.removeActivity(activity);
    }

    /** 结束所有活动，自动执行销毁 */
    static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        activities.clear();
    }
}
