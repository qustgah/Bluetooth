package com.gah.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.io.File;
import java.util.List;

/**
 * Created by GaoAhui on 2016/7/5.
 */
public class Utils {
    public static List<ResolveInfo> getAppInfoByType(Context context){
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.setDataAndType(Uri.fromFile(new File("")), "audio/*");
        return pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
    }
}
