package com.sobot.workorder.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.sobot.album.AlbumFile;
import com.sobot.album.SobotAlbum;
import com.sobot.workorder.R;
import com.sobot.workorder.adapter.base.SobotBaseHolder;
import com.sobot.workorder.adapter.base.SobotBaseRecyclerViewAdapter;

import java.util.List;

/**
 * 工单创建界面 图片选择器 适配器
 */
public class SobotWOCreatePicAdapter extends SobotBaseRecyclerViewAdapter<AlbumFile> {

    private static final int viewType_normalPic = 1;
    private static final int viewType_addPic = 2;

    private OnDeleteImgListener onDeleteImgListener;
    private OnClickImgListener onClickImgListener;

    public SobotWOCreatePicAdapter(Context context) {
        super(context);
    }

    @Override
    public int getLayout(int viewType) {
        switch (viewType) {
            default:
                return R.layout.sobot_adapter_order_pic_item;
        }
    }

    @Override
    public SobotBaseHolder getHolder(ViewGroup parent, int viewType, View view) {
        switch (viewType) {
            default:
                return new CreateWorkOrderPicHolder(parent, view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getMediaType() == AlbumFile.TYPE_ADD) {
            return viewType_addPic;
        } else {
            return viewType_normalPic;
        }
    }

    /**
     * 上传成功，添加到adapter中
     * 判断是否是超过15个，如果超过15个，删除最后一个
     * @param position
     * @param data
     */
    public void addData(int position, AlbumFile data) {
        if (list.size() >= 15) {
            int lastIndex = list.size() - 1;
            AlbumFile lastBean = list.get(lastIndex);
            if (lastBean != null && lastBean.getMediaType() == AlbumFile.TYPE_ADD) {
                list.remove(lastIndex);
            }
        }
        list.add(position, data);
        notifyDataSetChanged();
    }

    /**
     * 初始化数据，编辑工单，回复时，加载已上传的图片
     * @param datas
     */
    public void addDatas(List<AlbumFile> datas) {
        list.clear();
        list.addAll(datas);
        if (list.size() < 15) {
            AlbumFile addFile = new AlbumFile();
            addFile.setMediaType(AlbumFile.TYPE_ADD);
            list.add(addFile);
        }
        this.notifyDataSetChanged();
    }

    /**
     * 删除后更新
     * @param datas
     */
    public void updateDatas(List<AlbumFile> datas) {
        list.clear();
        list.addAll(datas);
        if (list.size() < 15) {
            AlbumFile addFile = new AlbumFile();
            addFile.setMediaType(AlbumFile.TYPE_ADD);
            list.add(addFile);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
//        int size = list.size();
//        if(size < 5){
//            return size + 1;
//        }else{
//            return super.getItemCount();
//        }
//        if (list.size() < 6) {
            return list.size();
//        } else {
//            return 5;
//        }
//        return list.size();
    }

    class CreateWorkOrderPicHolder extends SobotBaseHolder {

        public ImageView iv_work_order_pic;
        public ImageView iv_work_order_play;
        public ImageView iv_work_order_pic_add;
        public RecyclerView parent;
        private ImageView iv_work_order_pic_delete;

        public CreateWorkOrderPicHolder(ViewGroup parent, View itemView) {
            super(itemView);
            this.parent = (RecyclerView) parent;
        }

        @Override
        public void initView() {
            iv_work_order_pic = (ImageView) itemView.findViewById(R.id.iv_work_order_pic);
            iv_work_order_pic_add = (ImageView) itemView.findViewById(R.id.iv_work_order_pic_add);
            iv_work_order_pic_delete = (ImageView) itemView.findViewById(R.id.iv_work_order_pic_delete);
            iv_work_order_play = (ImageView) itemView.findViewById(R.id.iv_work_order_play);
        }

        @Override
        public void setData(SobotBaseRecyclerViewAdapter adapter, int position) {
            if (adapter.getItemViewType(position) == viewType_addPic) {
                itemView.setTag(R.drawable.sobot_icon_avatar_app_online, true);
                iv_work_order_pic.setVisibility(View.GONE);
                iv_work_order_pic_add.setVisibility(View.VISIBLE);
                iv_work_order_pic_delete.setVisibility(View.GONE);
            } else {
                iv_work_order_pic.setVisibility(View.VISIBLE);
                iv_work_order_pic_delete.setVisibility(View.VISIBLE);
                iv_work_order_pic_add.setVisibility(View.GONE);
                itemView.setTag(R.drawable.sobot_icon_avatar_app_online, false);
                final AlbumFile bean = list.get(position);
                SobotAlbum.getAlbumConfig().
                        getAlbumLoader().
                        load(iv_work_order_pic, bean);
                if(bean.getMediaType()== AlbumFile.TYPE_VIDEO){
                    iv_work_order_play.setVisibility(View.VISIBLE);
                }else{
                    iv_work_order_play.setVisibility(View.GONE);
                }
//                SobotBitmapUtil.display(context, !TextUtils.isEmpty(bean.getLocalPath()) ? bean.getLocalPath() : bean.getFileUrl(), iv_work_order_pic, R.drawable
//                        .sobot_icon_custom_select_pic_default, R.drawable.sobot_icon_custom_select_pic_error);
                iv_work_order_pic_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onDeleteImgListener != null && bean != null) {
                            onDeleteImgListener.onDeleteImgClick(iv_work_order_pic_delete, bean);
                        }
                    }
                });
                iv_work_order_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onClickImgListener != null && bean != null) {
                            onClickImgListener.onClickImgClick(bean);
                        }
                    }
                });
            }

        }
    }

    public interface OnDeleteImgListener {
        void onDeleteImgClick(View view, AlbumFile bean);
    }

    public OnDeleteImgListener getOnDeleteImgListener() {
        return onDeleteImgListener;
    }

    public void setOnDeleteImgListener(OnDeleteImgListener onDeleteImgListener) {
        this.onDeleteImgListener = onDeleteImgListener;
    }

    public interface OnClickImgListener {
        void onClickImgClick(AlbumFile bean);
    }

    public OnClickImgListener getOnClickImgListener() {
        return onClickImgListener;
    }

    public void setOnClickImgListener(OnClickImgListener onClickImgListener) {
        this.onClickImgListener = onClickImgListener;
    }
}
