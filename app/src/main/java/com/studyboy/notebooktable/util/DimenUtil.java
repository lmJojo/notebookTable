package com.studyboy.notebooktable.util;

import android.content.Context;
import android.util.Log;

public class DimenUtil {

    public static float density = 1;
    public static int dip2px(Context context,float dpValue) {
        density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

//    /**
//     * 初始化调用
//     */
//    public static void initDensity(Context context) {
//
//        Log.e("ConstantUtil", "initDensity=" + density);
//    }
//    public static float getDensity() {
//        return density;
//    }
}
