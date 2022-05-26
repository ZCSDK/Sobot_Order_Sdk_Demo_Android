package com.sobot.workorder.weight.tab;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import android.view.ViewGroup;

import com.sobot.workorder.base.SobotBaseFragment;

import java.util.List;

public class SobotViewPagerAdapter extends SobotTabFragmentPagerAdapter {
    private String[] tabs;
    private List<SobotBaseFragment> pagers;
    private Context context;

    public SobotViewPagerAdapter(Context context, FragmentManager fm, String[] tabs,
                                 List<SobotBaseFragment> pagers) {
        super(fm);
        this.tabs = tabs;
        this.pagers = pagers;
        this.context = context;
    }

    /**
     * 返回每一页需要的fragment
     */
    @Override
    public SobotBaseFragment getItem(int position) {
        return pagers.get(position);
    }

    @Override
    public int getCount() {
        return pagers.size();
    }

    /**
     * 返回每一页对应的title
     */
    @Override
    public CharSequence getPageTitle(int position) {
        if (tabs != null && position < tabs.length) {
            return tabs[position];
        }
        return "";
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
    }
}