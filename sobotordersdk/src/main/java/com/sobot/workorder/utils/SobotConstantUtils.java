package com.sobot.workorder.utils;

/**
 * @author: Sobot
 * 2022/3/25
 */
public interface SobotConstantUtils {

    /**
     * 传给工单详情页面的工单id
     */
    String SOBOT_WO_DETAIL_INFO_TICKETID = "SOBOT_WORK_ORDER_DETAIL_INFO_TICKETID";
    /**
     * 传给工单详情页面的客户id
     */
    String SOBOT_WORK_ORDER_DETAIL_INFO_CUSTOMERID = "SOBOT_WORK_ORDER_DETAIL_INFO_CUSTOMERID";
    /**
     * 传给工单详情页面的是否显示接单
     */
    String SOBOT_WO_DETAIL_INFO_ISSHOWRECEIPT = "SOBOT_WORK_ORDER_DETAIL_INFO_ISSHOWRECEIPT";

    //---------工单创建编辑回复页面合成一个页面

    /**
     * 工单页面显示类型
     */
    String sobot_wo_display_type = "sobot_work_order_display_type";
    /**
     * 表示是回复页面
     */
    int sobot_wo_display_type_reply = 1;

    /**
     * 表示是编辑页面
     */
    int sobot_wo_display_type_edit = 2;

    /**
     * 表示是编辑页面
     */
    int sobot_wo_display_type_new = 0;

    /**
     * 工单list页面显示数据
     */
    String sobto_work_order_data = "work_order_data";

    /**
     * 工单list页面显示类型：受理客服组
     */
    int sobot_wo_list_display_type_service_group = 300;

    /**
     * 工单list页面显示类型：受理客服
     */
    int sobot_wo_list_display_type_service = 301;

    /**
     * 工单list页面显示类型：抄送客服
     */
    int sobot_wo_list_display_type_service_copy = 303;

    /**
     * 工单list页面显示类型：工单类型
     */
    int sobot_wo_list_display_type_category = 304;


    /**
     * 工单创建界面：组合字段
     */
    int sobot_wo_create_type_group_field = 310;

    int sobot_wo_create_type_category_small = 311;

    //关单必填字段(工单状态为 已解决  已关闭 提交时检测)
    int sobot_wo_close_wo_field_required = 312;


    /**
     * 创建工单页面：用户中心
     */
    int sobot_wo_list_display_type_user_center = 307;

    /**
     * 删除图片成功
     */
    int sobot_wo_deleteAppFileSuccess = 302;

    /**
     * 删除图片成功
     */
    int sobot_wo_deleteAppFileSuccess_reply = 306;

    /**
     * 工单模版
     */
    int sobot_wo_list_ticket_template = 331;

    /**
     * 选择条目后返回数据的Key
     */
    String sobot_wo_list_selected_item = "work_order_list_selected_item";

    /**
     * 打开选择界面时传递当前选择项目的  key
     */
    String sobot_wo_list_data_selected_value = "work_order_list_data_selected_value";

    /**
     * 图片预览传过去的数据集合key
     */
    String sobot_wo_pic_list = "work_order_pic_list";

    /**
     * 图片预览传过去的当前选择图片位置 key
     */
    String sobot_wo_pic_list_current_item = "work_order_pic_list_current_item";

    /**
     * 传给工单详情页面的工单id
     */
    String sobot_wo_detail_info_ticketId = "work_order_detail_info_ticketId";
    /**
     * 传给工单详情页面的是否显示接单
     */
    String sobot_wo_detail_info_isShowReceipt = "work_order_detail_info_isShowReceipt";

    /**
     * 表示工单页面回复成功后需不需要更新数据
     */
    int sobot_wo_reply_response_need_update = 305;


    int SOBOT_WO_CUSTOMER_FIELD_SINGLE_LINE_TYPE = 1;//单行文本
    int SOBOT_WO_CUSTOMER_FIELD_MORE_LINE_TYPE = 2;//多行文本
    int SOBOT_WO_CUSTOMER_FIELD_DATE_TYPE = 3;//日期
    int SOBOT_WO_CUSTOMER_FIELD_TIME_TYPE = 4;//时间
    int SOBOT_WO_CUSTOMER_FIELD_NUMBER_TYPE = 5;//数值
    int SOBOT_WO_CUSTOMER_FIELD_SPINNER_TYPE = 6;//下拉列表
    int SOBOT_WO_CUSTOMER_FIELD_CHECKBOX_TYPE = 7;//复选框
    int SOBOT_WO_CUSTOMER_FIELD_RADIO_TYPE = 8;//单选框
    int SOBOT_WO_CUSTOMER_FIELD_CASCADE_TYPE = 9;//级联字段
    int SOBOT_WO_CUSTOMER_FIELD_GROUP_TYPE = 10;//组合自定义字段

    int SOBOT_WO_ORDER_CATEGORY_NODEFLAG_NO = 0;//此工单分类没有子节点
    int SOBOT_WO_ORDER_CATEGORY_NODEFLAG_YES = 1;//此工单分类有子节点

     int SOBOT_NEED_REFRESH = 1010;

    String SOBOT_CREATE_WORKORDER_USER = "sobot_create_workorder_user";//在客户中心 新增一个客户,成功以后

    String SOBOT_BROADCAST_WORK_ORDER_HAVE_MSG = "WORK_ORDER_HAVE_MSG";//收到工单通知以后，发送的广播

    //工单详情页请求搜索/分类列表的下一页
    String SOBOT_BROADCAST_TICKET_LOADMORE_REQUEST = "SOBOT_BROADCAST_TICKET_LOADMORE_REQUEST";//详情页面请求下一页
    String SOBOT_BROADCAST_TICKET_LOADMORE_RESULT = "SOBOT_BROADCAST_TICKET_LOADMORE_RESULT";//请求成功，添加数据
    String SOBOT_BROADCAST_TICKET_NOMORE = "SOBOT_BROADCAST_TICKET_NOMORE";//请求到最后一页，不能请求，
    String SOBOT_REFRESH_DATA ="SOBOT_REFRESH_DATA";
//    String SOBOT_REFRESH_DATA_LIST ="SOBOT_REFRESH_DATA_LIST";
}
