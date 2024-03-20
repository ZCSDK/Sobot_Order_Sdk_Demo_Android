package com.sobot.workorder.weight.dialog;

import android.graphics.drawable.Drawable;

/**
 * 功能描述：弹窗内部子类项（绘制标题和图标）
 */
public class SobotActionItem<T> {

    // 定义图片对象
    public Drawable mDrawable;
    // 定义文本对象
    public String mTitle;
    // 是否选中
    public boolean isChecked;

    public String type;

    public String dicId;

    public T obj;

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public SobotActionItem(String title) {
        this.mTitle = title;
        this.mDrawable = null;
    }

    public SobotActionItem(String title, Drawable drawable) {
        this.mDrawable = drawable;
        this.mTitle = title;
    }

    public SobotActionItem(String title, String type, Drawable drawable) {
        this.mDrawable = drawable;
        this.mTitle = title;
        this.type = type;
    }

    public SobotActionItem(String title, Drawable drawable, boolean isChecked) {
        this.mDrawable = drawable;
        this.mTitle = title;
        this.isChecked = isChecked;
    }

    public SobotActionItem(String title, boolean isChecked) {
        this.mTitle = title;
        this.isChecked = isChecked;
    }

    public SobotActionItem(String title, boolean isChecked, T obj) {
        this.mTitle = title;
        this.isChecked = isChecked;
        this.obj = obj;
    }


    public SobotActionItem(String title, Drawable drawable, boolean isChecked, String dicId) {
        this.mTitle = title;
        this.mDrawable = drawable;
        this.isChecked = isChecked;
        this.dicId = dicId;
    }

    public Drawable getmDrawable() {
        return mDrawable;
    }

    public void setmDrawable(Drawable mDrawable) {
        this.mDrawable = mDrawable;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getDicId() {
        return dicId;
    }

    public void setDicId(String dicId) {
        this.dicId = dicId;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SobotActionItem{" +
                "mDrawable=" + mDrawable +
                ", mTitle='" + mTitle + '\'' +
                ", isChecked=" + isChecked +
                ", dicId='" + dicId + '\'' +
                '}';
    }
}