package com.sobot.workorder.weight.popvindow;

import android.graphics.drawable.Drawable;

/**
 * 功能描述：弹窗内部子类项（绘制标题和图标）
 */
public class SobotActionItem {

	// 定义图片对象
	public Drawable mDrawable;
	// 定义文本对象
	public String mTitle;
	// 是否选中
	public boolean isChecked;

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
	public SobotActionItem(String title,Drawable drawable) {
		this.mDrawable = drawable;
		this.mTitle = title;
	}
	public SobotActionItem(String title,Drawable drawable,  boolean isChecked) {
		this.mDrawable = drawable;
		this.mTitle = title;
		this.isChecked = isChecked;
	}

	public SobotActionItem( String title,Drawable drawable, boolean isChecked, String dicId) {
		this.mTitle = title;
		this.mDrawable = drawable;
		this.isChecked = isChecked;
	}



	@Override
	public String toString() {
		return "ActionItem{" +
				"mDrawable=" + mDrawable +
				", mTitle=" + mTitle +
				", isChecked=" + isChecked +
				'}';
	}
}