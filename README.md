# Android集成文档
相关限制及注意事项  
1、Android SDK 新版支持 api17 以上版本，支持竖屏和横屏。

2、开发工具AS建议升级到3.0以上版本

3、Android SDK 目前仅仅支持超链接标签，其他html标签和属性均不识别

4、Android SDK 需要申请存储、拍照危险权限，否则部分功能无法使用

![图片](https://img.sobot.com/mobile/sdk/images/sdk-order-home-01.png)

智齿客服为企业提供了一整套完善的智能客服解决方案。智齿工单SDK 为其提供所有工单支持，仅需提供使用的账户，就能完成一系列的工单操作。

智齿客服SDK具有以下特性

* 提供登录-使用-退出完整流程。
* 分2种登录模块，外部登录同步token和常规用户名、密码登录。
* 支持工单的查询、操作、新建等基础功能；


## 1 文档介绍

### 1.1 集成流程示意图
![图片](https://img.sobot.com/mobile/sdk/images/sdk-order-001.png)
### 1.2.文件说明
**SDK包含源码包（sobotordersdk）、Demo(Sobot_Order_Sdk_Demo_Android)、和Doc相关说明文档。**

| 文件名   | 说明   | 
|:----|:----|
| SobotOrderApi   | 该文件提供接入功能   | 

## 2 集成方式

### 2.1 依赖集成

[最新版本地址](https://central.sonatype.com/artifact/com.sobot.zcsdk/ordersdk)

```js
api 'com.sobot.zcsdk:ordersdk:+'
```

在build.gradle中如下所示：

```js
dependencies {
    api 'com.sobot.zcsdk:ordersdk:+'
    api 'com.squareup.okhttp3:okhttp:4.4.0'
    api 'androidx.appcompat:appcompat:1.0.0'
    api 'com.google.android.material:material:1.4.+'
    api 'androidx.recyclerview:recyclerview:1.0.0'
    //目前支持常见的3种图片加载库，必须在下面的图片加载库中选择一个添加依赖
    //implementation 'com.nostra13.universalimageloader:universal-image-loader:1.9.5'
    //implementation 'com.squareup.picasso:picasso:2.8'
    api 'com.github.bumptech.glide:glide:4.16.0'
    //在使用4.9.0以上版本的glide时，需额外添加依赖，之前的版本不需要
    api 'com.sobot.chat:sobotsupport-glidev4:3.1'
}
```
【注意】由于glide v3版本和v4版本的接口完全不同，因此我们为了方便您的使用，采用了特殊的集成方式使sdk可以支持任 意版本的glide。正常情况下，您使用glide 时，直接添加glide依赖和ordersdk的依赖，sdk即 可正常使用。如果报错，那么把glide 升级到4.4.0版本以上即可。


**在使用4.9.0以上版本的glide时，需额外升级依赖**

api 'com.sobot.chat:sobotsupport-glidev4:3.1'

### 2.2 导入集成

下载链接：[Android_Order_SDK](https://shimo.im/docs/NJkbELdRZ8HwpwqR)

导入Module

解压下载的智齿Android_Order_SDK_XXX.zip文件，将 sobotordersdk 模块直接引入到您的项目中，然后 Build-->clean projecty一下，之后在build.gradle添加项目依赖

完成上述步骤之后build.gradle中如下所示：

```js
dependencies {
    api project(":sobotordersdk")
    api 'com.squareup.okhttp3:okhttp:4.4.0'
    api 'androidx.appcompat:appcompat:1.0.0'
    api 'com.google.android.material:material:1.4.+'
    api 'androidx.recyclerview:recyclerview:1.0.0'
    //目前支持常见的3种图片加载库，必须在下面的图片加载库中选择一个添加依赖
    //implementation 'com.nostra13.universalimageloader:universal-image-loader:1.9.5'
    //implementation 'com.squareup.picasso:picasso:2.8'
    api 'com.github.bumptech.glide:glide:4.16.0'
    //在使用4.9.0以上版本的glide时，需额外添加依赖，之前的版本不需要
    api 'com.sobot.chat:sobotsupport-glidev4:3.1'
}
```
【注意】由于glide v3版本和v4版本的接口完全不同，因此我们为了方便您的使用，采用了特殊的集成方式使sdk可以支持任 意版本的glide。正常情况下，您使用glide 时，直接添加glide依赖和ordersdk的依赖，sdk即 可正常使用。如果报错，那么把glide 升级到4.4.0版本以上即可。

**在使用4.9.0以上版本的glide时，需额外升级依赖**

api 'com.sobot.chat:sobotsupport-glidev4:3.1'

## 3 功能说明

## 3.1 域名和初始化设置
域名说明：

*默认SaaS平台域名为：https://api-c.sobot.com

*如果您是腾讯云服务，请设置为：https://api-c.soboten.com

*如果您是本地化部署，请使用自己的部署的服务域名

注意：启动智齿工单SDK之前，必须调用初始化接口initWithHost，否则将无法启动SDK】

```js

    /**
     * 初始化方法,需要在宿主应用application.onCreate函数中调用
     *
     * @param host  可以为空，默认阿里云服务；如果需要，请设置自己的域名
     */
    SobotOrderApi.initWithHost(Application application, String host);
```


## 3.2 启动智齿页面

### 3.2.1 启动工单首页

方式1：直接使用我们提供的方法，自动完成登录并跳转页面。
接口：

```js
/**
  * 通过邮箱密码方式启动工单系统，进入首页
  * @param context
  * @param loginPwd 客服邮箱
  * @param loginPwd 登录密码
  * @param block 回调结果 可为空
  */
SobotOrderApi.startWithAcount(Context context, String loginUser, String loginPwd, SobotResultBlock block)；


 /**
  * 通过设置token方式启动工单系统，进入首页
  * @param context
  * @param loginToken 登录客服token
  * @param block 回调结果 可为空
  */
SobotOrderApi.startWithToken(final Context context, String loginToken, SobotResultBlock block)；
```


方式2：通过嵌入fragment的方式，自己完成登录并跳转页面。需要先完成登录，然后再执行跳转。

第一步、先登录

```js  
//登录方式一

/**
 * 通过邮箱密码方式登录工单系统
 * @param context
 * @param loginPwd       客服邮箱
 * @param loginPwd       登录密码
 * @param isGoToActivity 是否进入工单中心界面 true 进入 false 只登录不进入
 * @param block 回调结果 可为空                       
 */
SobotOrderApi.loginUser(Context context, String loginUser, String loginPwd,false, SobotResultBlock block)；


//登录方式二

 /**
   * 通过设置token方式登录工单系统
   * @param context
   * @param loginToken     登录客服token
   * @param isGoToActivity 是否进入工单中心界面 true 进入 false 只登录不进入
   * @param block 回调结果 可为空
   */
SobotOrderApi.loginUser(Context context, String loginToken, false, SobotResultBlock block)
```

第二步、再进入fragment

参考代码
```js  
SobotWOclassificationFragment classificationFragment;


classificationFragment = (SobotWOclassificationFragment) getSupportFragmentManager()
                .findFragmentById(getResId("order_fragment"));
        if (classificationFragment == null) {
            classificationFragment = SobotWOclassificationFragment.newInstance();

            addFragmentToActivity(getSupportFragmentManager(),
                    classificationFragment, getResId("order_fragment"));
        }
```



### 3.2.2 启动工单详情
【注：执行次操作之前，需要保证已完成登录】

接口

```js 
/**
 * 进入工单详情页
 * 
 * @param context
 * @param orderId 工单id
 * @param block 回调结果 可为空
 */
SobotOrderApi.openOrderDetail(Context context, String orderId, SobotResultBlock block)；       
```
### 3.2.3 启动创建工单页面
【注：执行次操作之前，需要保证已完成登录】

接口

```js 
 /**
  * 打开创建工单页面
  *
  * @param context
  * @param param  可为空，也可添加参数 userName 用户昵称 userId 用户id
  * @param block 回调结果
  */
SobotOrderApi.openCreateWorkOrder(final Context context, Map<String, Object> param, SobotResultBlock block)
```

示例代码

```js 
Map<String, Object> param = new HashMap<>();
if (TextUtils.isEmpty(userid.getText().toString())){
      if(!TextUtils.isEmpty(nick.getText().toString())){
           SobotToastUtil.showCustomToast(getSobotActivity(),"如果输入昵称，但是用户id为空，对应客户是也是空的，无效的");
      }else{
        if(SobotStringUtils.isEmpty(nick.getText().toString())){
            SobotToastUtil.showCustomToast(getContext(),"用户昵称不能为空");
                 return;
           }
           param.put("userName", nick.getText().toString());
           param.put("userId", userid.getText().toString());
		   }
}
SobotOrderApi.openCreateWorkOrder(getSobotActivity(), param, null);
```

### 3.3 登录
```js  
//登录方式一

/**
 * 通过邮箱密码方式登录工单系统
 * @param context
 * @param loginPwd       客服邮箱
 * @param loginPwd       登录密码
 * @param isGoToActivity 是否进入工单中心界面 true 进入 false 只登录不进入
 * @param block 回调结果 可为空                       
 */
SobotOrderApi.loginUser(Context context, String loginUser, String loginPwd,false, SobotResultBlock block)；


//登录方式二

 /**
   * 通过设置token方式登录工单系统
   * @param context
   * @param loginToken     登录客服token
   * @param isGoToActivity 是否进入工单中心界面 true 进入 false 只登录不进入
   * @param block 回调结果 可为空
   */
SobotOrderApi.loginUser(Context context, String loginToken, false, SobotResultBlock block)
```

## 3.4 退出登录
```js
 /**
  * 退出工单系统
  * @param context
  * @param loginUser 登录账号
  * @param block  回调结果 可为空
  */
SobotOrderApi.out(Context context, String loginUser, SobotResultBlock block) 
```

## 3.5 设置调试模式，开启日志
```js
/**
 * 日志显示设置
 *
 * @param isShowLog true 显示日志信息 默认false不显示
 */
SobotOrderApi.setShowDebug(Boolean isShowLog)
```

## 4 自定义UI设置

### 4.1 ui样式通过同名资源替换
在客户app中colors.xml中添加同名颜色可覆盖智齿sdk中的颜色样式；也可通过在主项目中同一位置放一个同名的图片资源去替换智齿sdk界面中的图片；也可通过在主项目中同一位置放一个同名的文字资源去替换智齿sdk界面中的文字；

以下是常用的颜色配置，更多颜色、图片资源可到手动集成模块中下载源码包中去查找；
```js
    <!--工单 主题色 默认绿色 -->
    <color name="sobot_wo_theme_color">#09AEB0</color>
    <!-- 头部背景 -->
    <color name="sobot_color_title_bar_bg">@color/sobot_wo_theme_color</color>
    <!-- 状态栏的颜色设置 -->
    <color name="sobot_status_bar_color">@color/sobot_color_title_bar_bg</color>
    <!-- 头部中间昵称颜色 -->
    <color name="sobot_color_title_bar_title">@color/sobot_wo_white_color</color>
    <!-- 头部两侧菜单字体颜色 -->
    <color name="sobot_color_title_bar_menu_text">@color/sobot_wo_white_color</color>
    <!-- 一级文字 颜色 -->
    <color name="sobot_wo_wenzi_gray1">#3D4966</color>
    <!-- 二级文字 颜色 -->
    <color name="sobot_wo_wenzi_gray2">#8B98AD</color>
    <!-- 三级文字 颜色 -->
    <color name="sobot_wo_wenzi_gray3">#BDC3D1</color>
    <!-- 四级文字 颜色 -->
    <color name="sobot_wo_wenzi_gray4">#CCCCCC</color>
    <!--界面 通用背景颜色 -->
    <color name="sobot_wo_bg_color">#EFF3FA</color>
    <!-- 二级背景色 -->
    <color name="sobot_wo_second_bg_color">#FFFFFF</color>
    <!-- 页面背景  三级颜色 -->
    <color name="sobot_wo_third_bg_color">#EFF3FA</color>
    <!-- 透明的背景色-->
    <color name="sobot_wo_app_bg_transparent">@color/sobot_wo_transparent</color>
    <!-- 搜索框背景颜色-->
    <color name="sobot_wo_search_bg_color">#F6F8FC</color>
    <!-- 超链接颜色-->
    <color name="sobot_wo_link">@color/sobot_wo_blue_color</color>

```

## 5 智齿SDK功能使用体验Demo
[点击下载 Demo 源码](https://github.com/ZCSDK/Sobot_Order_Sdk_Demo_Android)

[点击下载体验安装包](https://img.sobot.com/mobile/ordersdk/sobot_order_sdk_demo_android.apk)


## 6 常见问题
常见问题解答：请[点击链接](https://www.sobot.com/chat/pc/v2/index.html?sysnum=a76f3cef7d1043c69dd592c3e43f8242#0) 进入智能机器人输入您的问题

## 7 更新说明

[《智齿客服工单SDK 版本更新说明》](https://shimo.im/docs/NJkbELdRZ8HwpwqR)


## 8 智齿科技SDK收集使用个人信息说明
[《智齿科技SDK收集使用个人信息说明》](https://www.sobot.com/docs/clause/sdk-clause.html)