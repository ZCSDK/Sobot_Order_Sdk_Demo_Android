package com.sobot.workorder.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 自定义可适应ScrollView的ListView
 *
 * @author terry_龙
 */
public class SobotListViewForScrollView extends ListView {
    public SobotListViewForScrollView(Context context) {
        super(context);
    }
    public SobotListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public SobotListViewForScrollView(Context context, AttributeSet attrs,
                                      int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
