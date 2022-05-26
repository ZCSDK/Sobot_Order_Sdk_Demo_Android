package com.sobot.workorder.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.common.utils.SobotResourceUtils;
import com.sobot.utils.SobotRegularUtils;
import com.sobot.utils.SobotSharedPreferencesUtil;
import com.sobot.workorder.activity.SobotPhotoActivity;
import com.sobot.workorder.adapter.SobotWorkOrderUtilsAdapter;
import com.sobot.workorder.weight.SobotListViewForScrollView;
import com.sobot.workorderlibrary.api.apiutils.SobotWOConstant;
import com.sobot.workorderlibrary.api.model.SobotFileItemModel;
import com.sobot.workorderlibrary.api.model.SobotServiceVoModel;
import com.sobot.workorderlibrary.api.model.SobotTicketResultListModel;
import com.sobot.workorderlibrary.api.model.SobotTmpItemModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Sobot
 * 2022/3/25
 */
public class SobotWorkOrderUtils {

    //显示文件
    public static void updateFileList(Context context, List<SobotFileItemModel> fileList, LinearLayout llFileList) {
        if (context == null || fileList == null || fileList.size() == 0) {
            llFileList.setVisibility(View.GONE);
            return;
        }
        final Context appContext = context.getApplicationContext();
        llFileList.setVisibility(View.VISIBLE);
        for (int i = 0; i < fileList.size(); i++) {
            final SobotFileItemModel file = fileList.get(i);
            String fileType = file.getFileType();
            String fileUrl = file.getFileUrl();
            String fileName = file.getFileName();
            View view = View.inflate(appContext, SobotResourceUtils.getResLayoutId(context,"sobot_detaile_item_file_layout"), null);
            TextView tv_file1_content = (TextView) view.findViewById(SobotResourceUtils.getResId(context,"tv_file1_content"));
            TextView tv_file1_click_btn = (TextView) view.findViewById(SobotResourceUtils.getResId(context,"tv_file1_click_btn"));
            tv_file1_content.setText(fileName);
            if (fileList.size() - 1 == i) {
                //最后一个
                View v_dividerline = (View) view.findViewById(SobotResourceUtils.getResId(context,"v_dividerline"));
                v_dividerline.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(fileType) && (fileType.equals("png") || fileType.equals("jpg") || fileType.equals("jpeg"))) {
                tv_file1_click_btn.setText("预览");
                Drawable drawable = context.getResources().getDrawable(SobotResourceUtils.getDrawableId(context,"sobot_icon_pic"));
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_file1_content.setCompoundDrawables(drawable, null, null, null);
                tv_file1_click_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(appContext, SobotPhotoActivity.class);
                        intent.putExtra("imageUrL", file.getFileUrl());
                        intent.putExtra("titleName", file.getFileName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        appContext.startActivity(intent);
                    }
                });
            } else {
                tv_file1_click_btn.setText("下载");
                final String mUrl = fileUrl;
                String  drawableName = "sobot_icon_pic";
                if (!TextUtils.isEmpty(fileType)) {
                    if (fileType.equals("doc") || fileType.equals("docx")) {
                        drawableName = "sobot_icon_word";
                    }
                    if (fileType.equals("pdf")) {
                        drawableName = "sobot_icon_pdf";
                    }
                    if (fileType.equals("xls") || fileType.endsWith("xlsx")||fileType.endsWith("csv")) {
                        drawableName = "sobot_icon_excel";
                    }
                    if (fileType.equals("zip") || fileType.endsWith("rar")) {
                        drawableName = "sobot_icon_zip";
                    }
                    if (fileType.equals("ppt") || fileType.endsWith("pptx")) {
                        drawableName = "sobot_icon_ppt";
                    }
                }
                Drawable drawable = context.getResources().getDrawable((SobotResourceUtils.getDrawableId(context,drawableName)));
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_file1_content.setCompoundDrawables(drawable, null, null, null);
                tv_file1_click_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 外部浏览器
                        try {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content = Uri.parse(mUrl);
                            intent.setData(content);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            appContext.startActivity(intent);
                        } catch (android.content.ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            llFileList.addView(view);
        }
    }
    //更新自定义字段
    //工单详情页调用  在WorkDetailDealFragment中调用
    //@param isInTemplate 是否属于当前模板
    @SuppressLint("SetTextI18n")
    public static void updateWorkOderResultListView(final Context context, List<SobotTicketResultListModel> resultList, LinearLayout ll_container, boolean isInTemplate) {
        if (ll_container == null) {
            return;
        }
        ll_container.removeAllViews();
        if (resultList != null && resultList.size() > 0) {
            ll_container.setVisibility(View.VISIBLE);
            for (int i = 0; i < resultList.size(); i++) {
                View v = View.inflate(context, SobotResourceUtils.getResLayoutId(context,"sobot_item_detail_his_child"), null);
                LinearLayout group_cumtomer_field = v.findViewById(SobotResourceUtils.getResId(context,"group_cumtomer_field"));
                LinearLayout normal_cumtomer_field = v.findViewById(SobotResourceUtils.getResId(context,"normal_cumtomer_field"));

                group_cumtomer_field.setVisibility(View.GONE);
                normal_cumtomer_field.setVisibility(View.GONE);

                SobotListViewForScrollView listView = v.findViewById(SobotResourceUtils.getResId(context,"result_list"));
                TextView tv_key = v.findViewById(SobotResourceUtils.getResId(context,"tv_key"));
                TextView tv_val = v.findViewById(SobotResourceUtils.getResId(context,"tv_val"));
                TextView tv_key1 = v.findViewById(SobotResourceUtils.getResId(context,"tv_key1"));

                if (!isInTemplate) {
                    //回复内容不属于当前模板时 需要把内容的颜色置灰
                    int color = Color.parseColor("#BDC3D1");
                    tv_key.setTextColor(color);
                    tv_val.setTextColor(color);
                    tv_key1.setTextColor(color);
                }
                v.findViewById(SobotResourceUtils.getResId(context,"v_line")).setVisibility(View.GONE);
                if (resultList.get(i).getFieldType() == 6 || resultList.get(i).getFieldType() == 7 || resultList.get(i).getFieldType() == 8|| resultList.get(i).getFieldType() == 9) {
                    tv_key.setText(resultList.get(i).getTitle());
                    normal_cumtomer_field.setVisibility(View.VISIBLE);
                    tv_val.setText(TextUtils.isEmpty(resultList.get(i).getText()) ? "--" : resultList.get(i).getText());
                } else if (resultList.get(i).getFieldType() == 10) {
                    tv_key1.setText(resultList.get(i).getTitle());
                    group_cumtomer_field.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(resultList.get(i).getValue())) {
                        List<SobotTmpItemModel> listModel = new ArrayList<>();
                        String[] strLists;
                        if (resultList.get(i).getValue().contains("|")) {
                            strLists = resultList.get(i).getValue().replace("|", "#").split("#");
                            if (strLists.length > 0) {
                                SobotTmpItemModel model;
                                for (String a : strLists) {
                                    if (!TextUtils.isEmpty(a)) {
                                        String[] tmpList = a.split(";");
                                        if (tmpList.length > 0) {
                                            for (int j = 0; j < tmpList.length; j++) {
                                                String aTmpList = tmpList[j];
                                                String[] tmpStr = aTmpList.split("：");
                                                if (tmpStr.length > 0) {
                                                    model = new SobotTmpItemModel();
                                                    if (a.length() != 2) {
                                                        if (j == tmpList.length - 1) {
                                                            model.setShowLine(true);
                                                        }
                                                    }
                                                    if (tmpStr.length == 1) {
                                                        model.setTitle(tmpStr[0]);
                                                        listModel.add(model);
                                                    } else if (tmpStr.length == 2) {
                                                        model.setTitle(tmpStr[0]);
                                                        model.setContent(tmpStr[1]);
                                                        listModel.add(model);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (!TextUtils.isEmpty(resultList.get(i).getSummary()) && resultList.get(i).getNumericalFlag() == 1) {
                                    model = new SobotTmpItemModel();
                                    model.setShowSummary(true);
                                    model.setTitle(resultList.get(i).getSummary());
                                    listModel.add(model);
                                }

                                if (listModel.size() > 0) {
                                    SobotWorkOrderUtilsAdapter adapter = new SobotWorkOrderUtilsAdapter(context, listModel);
                                    listView.setAdapter(adapter);
                                }
                            }
                        } else {
                            String[] tmpList = resultList.get(i).getValue().split(";");
                            if (tmpList.length > 0) {
                                SobotTmpItemModel model;
                                for (String aTmpList : tmpList) {
                                    String[] tmpStr = aTmpList.split("：");
                                    if (tmpStr.length > 0) {
                                        if (tmpStr.length == 1) {
                                            model = new SobotTmpItemModel();
                                            model.setTitle(tmpStr[0]);
                                            listModel.add(model);
                                        } else if (tmpStr.length == 2) {
                                            model = new SobotTmpItemModel();
                                            model.setTitle(tmpStr[0]);
                                            model.setContent(tmpStr[1]);
                                            listModel.add(model);
                                        }
                                    }
                                }

                                if (!TextUtils.isEmpty(resultList.get(i).getSummary()) && resultList.get(i).getNumericalFlag() == 1) {
                                    model = new SobotTmpItemModel();
                                    model.setShowSummary(true);
                                    model.setTitle(resultList.get(i).getSummary());
                                    listModel.add(model);
                                }

                                if (listModel.size() > 0) {
                                    SobotWorkOrderUtilsAdapter adapter = new SobotWorkOrderUtilsAdapter(context, listModel);
                                    listView.setAdapter(adapter);
                                }
                            }
                        }
                    }
                } else {
                    normal_cumtomer_field.setVisibility(View.VISIBLE);
                    tv_key.setText(resultList.get(i).getTitle());
                    tv_val.setText(TextUtils.isEmpty(resultList.get(i).getValue()) ? "--" : resultList.get(i).getValue());
                    final String value = resultList.get(i).getValue();
                    if (!TextUtils.isEmpty(value) && SobotRegularUtils.isMobileSimple(value)) {
                        tv_val.setTextColor(context.getResources().getColor(SobotResourceUtils.getResColorId(context,"sobot_wo_theme_color")));
                        tv_val.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!TextUtils.isEmpty(value)) {
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.VIEW");
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setData(Uri.parse("tel:" + value));// mobile为你要拨打的电话号码，模拟器中为模拟器编号也可
                                    context.startActivity(intent);
                                }
                            }
                        });
                    }
                }

                ll_container.addView(v);
            }
        } else {
            ll_container.setVisibility(View.GONE);
        }
    }
    //更新自定义字段
    //此方法是工单历史中调用
    public static void updateResultListView(Context context, List<SobotTicketResultListModel> resultList, LinearLayout ll_container) {
        if (ll_container == null) {
            return;
        }
        ll_container.removeAllViews();
        if (resultList != null && resultList.size() > 0) {
            ll_container.setVisibility(View.VISIBLE);
            for (int i = 0; i < resultList.size(); i++) {
                SobotTicketResultListModel data = resultList.get(i);
                //去掉语音功能 2022.04.28
//                if ("语音文件".equals(data.getText()) && !TextUtils.isEmpty(data.getValue())
//                        && data.getValue().startsWith("http")) {
//                    String voicePath = data.getValue();
//                    View voiceView = View.inflate(context.getApplicationContext(), SobotResourceUtils.getResLayoutId(context,"sobot_item_detail_his_child_voice"), null);
//                    final MusicPlayView mpv_play = voiceView.findViewById(SobotResourceUtils.getResId(context,"mpv_play"));
//                    // 语音文件播放
//                    mpv_play.setDataSource(voicePath);
//                    mpv_play.resume();
//                    ll_container.addView(voiceView);
//                } else {
                    View v = View.inflate(context, SobotResourceUtils.getResLayoutId(context,"sobot_item_detail_his_child"), null);
                    LinearLayout group_cumtomer_field = v.findViewById(SobotResourceUtils.getResId(context,"group_cumtomer_field"));
                    TextView tv_key = v.findViewById(SobotResourceUtils.getResId(context,"tv_key"));
                    TextView tv_val = v.findViewById(SobotResourceUtils.getResId(context,"tv_val"));
                    SobotListViewForScrollView listView = v.findViewById(SobotResourceUtils.getResId(context,"result_list"));
                    group_cumtomer_field.setVisibility(View.GONE);
                    if (data.getFieldType() == 10) {
                        group_cumtomer_field.setVisibility(View.VISIBLE);
                        tv_key.setText(data.getTitle());
                        if (!TextUtils.isEmpty(data.getValue())) {
                            String[] strLists;
                            if (data.getValue().contains("|")) {
                                strLists = data.getValue().replace("|", "#").split("#");
                                if (strLists.length > 0) {
                                    List<SobotTmpItemModel> listModel = new ArrayList<>();
                                    SobotTmpItemModel model;
                                    for (String a : strLists) {
                                        if (!TextUtils.isEmpty(a)) {
                                            String[] tmpList = a.split(";");
                                            if (tmpList.length > 0) {
                                                for (int j = 0; j < tmpList.length; j++) {
                                                    String aTmpList = tmpList[j];
                                                    String[] tmpStr = aTmpList.split("：");
                                                    if (tmpStr.length > 0) {
                                                        model = new SobotTmpItemModel();
                                                        if (a.length() != 2) {
                                                            if (j == tmpList.length - 1) {
                                                                model.setShowLine(true);
                                                            }
                                                        }
                                                        if (tmpStr.length == 1) {
                                                            model.setTitle(tmpStr[0]);
                                                            listModel.add(model);
                                                        } else if (tmpStr.length == 2) {
                                                            model.setTitle(tmpStr[0]);
                                                            model.setContent(tmpStr[1]);
                                                            listModel.add(model);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (!TextUtils.isEmpty(data.getSummary())) {
                                        model = new SobotTmpItemModel();
                                        model.setShowSummary(true);
                                        model.setTitle(data.getSummary());
                                        listModel.add(model);
                                    }

                                    if (listModel.size() > 0) {
                                        SobotWorkOrderUtilsAdapter adapter = new SobotWorkOrderUtilsAdapter(context, listModel);
                                        listView.setAdapter(adapter);
                                    }
                                }
                            } else {
                                String[] tmpList = data.getValue().split(";");
                                if (tmpList.length > 0) {
                                    List<SobotTmpItemModel> listModel = new ArrayList<>();
                                    SobotTmpItemModel model;
                                    for (String aTmpList : tmpList) {
                                        String[] tmpStr = aTmpList.split("：");
                                        if (tmpStr.length == 1) {
                                            model = new SobotTmpItemModel();
                                            model.setTitle(tmpStr[0]);
                                            listModel.add(model);
                                        } else if (tmpStr.length == 2) {
                                            model = new SobotTmpItemModel();
                                            model.setTitle(tmpStr[0]);
                                            model.setContent(tmpStr[1]);
                                            listModel.add(model);
                                        }
                                    }

                                    if (!TextUtils.isEmpty(data.getSummary())) {
                                        model = new SobotTmpItemModel();
                                        model.setShowSummary(true);
                                        model.setTitle(data.getSummary());
                                        listModel.add(model);
                                    }

                                    if (listModel.size() > 0) {
                                        SobotWorkOrderUtilsAdapter adapter = new SobotWorkOrderUtilsAdapter(context, listModel);
                                        listView.setAdapter(adapter);
                                    }
                                }
                            }
                        }
                    } else {
                        tv_key.setText(data.getText());
                        String tmpStr = data.getValue();
                        tv_val.setText(Html.fromHtml((TextUtils.isEmpty(tmpStr) ? "" : tmpStr.replace(";", "\n")).replace("<p>", "").replace
                                ("</p>", "").replaceAll("<img[^>]*>", "[图片]")));
                    }
                    ll_container.addView(v);
//                }
            }
        } else {
            ll_container.setVisibility(View.GONE);
        }
    }
    //获取到未读工单的key
    public static String getWorkOrderUnReadKey(Context context) {
        return "WORK_ORDER_UNREAD_MSG"+getServiceId(context);
    }

    /**
     * 获取登录用户的id
     * @param context
     * @return
     */
    public static String getServiceId(Context context){
        SobotServiceVoModel sobotServiceVoModel = SobotSharedPreferencesUtil.getInstance(context).get(SobotWOConstant.SOBOT_LOGIN_USER_INFO, SobotServiceVoModel.class);
        if(sobotServiceVoModel !=null){
            return sobotServiceVoModel.getServiceId();
        }else{
            return "";
        }
    }

    /**
     * 获取登录用户的名字
     * @param context
     * @return
     */
    public static String getServiceName(Context context){
        SobotServiceVoModel sobotServiceVoModel = SobotSharedPreferencesUtil.getInstance(context).get(SobotWOConstant.SOBOT_LOGIN_USER_INFO, SobotServiceVoModel.class);
        if(sobotServiceVoModel !=null){
            return sobotServiceVoModel.getServiceName();
        }else{
            return "";
        }
    }

}
