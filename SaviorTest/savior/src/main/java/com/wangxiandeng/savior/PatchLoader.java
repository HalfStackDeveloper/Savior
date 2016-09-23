package com.wangxiandeng.savior;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

/**
 * Created by xingzhu on 2016/9/22.
 */

public class PatchLoader {
    private Application mApplication;
    private String mPatchBoxName;

    /**
     * @param application    App的Application
     * @param patchBoxName 自定义AppPatchesBoxImpl全限定名
     */

    private static PatchLoader mInstance;

    public static PatchLoader getInstance() {
        if (mInstance == null) {
            synchronized (PatchLoader.class) {
                mInstance = new PatchLoader();
            }
        }
        return mInstance;
    }

    public void init(Application application, String patchBoxName) {
        mApplication = application;
        mPatchBoxName = patchBoxName;
    }

    /**
     * @param patchPath 补丁dex所在路径
     * @throws Exception
     */
    public void loadPatch( String patchPath) throws Exception {
        if (mApplication == null) {
            throw new Exception("Please init PatchLoad in your Application");
        }

        try {
            //加载补丁Dex文件
            DexClassLoader dexClassLoader = new DexClassLoader(patchPath, getOdexPath(), null, getClass().getClassLoader());

            //加载补丁装载类PatchBox
            Class<?> patchBoxClass = Class.forName(mPatchBoxName, true, dexClassLoader);
            IPatchBox patchBox = (IPatchBox) patchBoxClass.newInstance();

            //遍历加载补丁类
            for (String className : patchBox.getPatchClasses()) {
                Class<?> patchClass = dexClassLoader.loadClass(className);
                Object patchInstance = patchClass.newInstance();

                //反射修改bug类的mSavior字段
                int index = className.indexOf("$Patch");
                if (index == -1) {
                    Log.e("Savior:", "incorrect name for patch, please rename your patch according to the README.md");
                    return;
                }
                String bugClassName = className.substring(0, index);
                Class<?> bugClass = getClass().getClassLoader().loadClass(bugClassName);
                Field saviorField = bugClass.getDeclaredField("$savior");
                saviorField.setAccessible(true);
                saviorField.set(null, patchInstance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //dex优化后的odex存放路径
    private String getOdexPath() {
        return mApplication.getCacheDir().getPath();
    }

}
