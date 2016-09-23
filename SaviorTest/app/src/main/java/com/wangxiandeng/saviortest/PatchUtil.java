package com.wangxiandeng.saviortest;

import android.os.Environment;

import java.io.File;

/**
 * Created by xingzhu on 2016/9/23.
 */

public class PatchUtil {
    public final static String PATCH_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator+"patch_dex.jar";
}
