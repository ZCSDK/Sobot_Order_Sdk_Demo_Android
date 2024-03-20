package com.sobot.workorder.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sobot.utils.SobotLogUtils;
import com.sobot.utils.SobotNetUtils;
import com.sobot.utils.SobotStringUtils;
import com.sobot.workorder.R;
import com.sobot.workorder.base.SobotWOBaseFragment;

import org.jetbrains.annotations.NotNull;


/**
 * webview
 *
 * @author guoqf
 */
public class SobotWOWebFragment extends SobotWOBaseFragment implements View.OnClickListener {

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private RelativeLayout sobot_rl_net_error;

    private Button sobot_btn_reconnect;
    private TextView sobot_txt_loading;
    private TextView sobot_textReConnect;

    private String mUrl = "";


    public void setUrl(String url) {
        this.mUrl = url;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.sobot_fragment_webview, container, false);
        initView(root);
        return root;
    }
    private void initView(View rootView) {
        if (rootView == null) {
            return;
        }
        mWebView = (WebView) rootView.findViewById(R.id.sobot_mWebView);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.sobot_loadProgress);
        sobot_rl_net_error = (RelativeLayout) rootView.findViewById(R.id.sobot_rl_net_error);
        sobot_btn_reconnect = (Button) rootView.findViewById(R.id.sobot_btn_reconnect);
        sobot_btn_reconnect.setText(R.string.sobot_reconnect);
        sobot_btn_reconnect.setOnClickListener(this);
        sobot_textReConnect = (TextView) rootView.findViewById(R.id.sobot_textReConnect);
        sobot_textReConnect.setText(R.string.sobot_wo_net_error_string);
        sobot_txt_loading = (TextView) rootView.findViewById(R.id.sobot_txt_loading);
        initWebView();
        initData();
    }

    private void initData() {
        if (!SobotStringUtils.isEmpty(mUrl)) {
            //加载url
            mWebView.loadUrl(mUrl);
        }
        resetViewDisplay();
    }

    @Override
    public void onClick(View view) {
        if (view == sobot_btn_reconnect) {
            if (!TextUtils.isEmpty(mUrl)) {
                initData();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(needRefresh){
            initData();
            needRefresh = false;
        }
    }

    @Override
    public void setNeedRefresh(boolean needRefresh) {
        super.setNeedRefresh(needRefresh);
        if(needRefresh){
            if(getUserVisibleHint()){
                initData();
                setNeedRefresh(false);
            }
        }
    }

    /**
     * 根据有无网络显示不同的View
     */
    private void resetViewDisplay() {
        if (SobotNetUtils.isConnected(getContext())) {
            mWebView.setVisibility(View.VISIBLE);
            sobot_rl_net_error.setVisibility(View.GONE);
        } else {
            mWebView.setVisibility(View.GONE);
            sobot_rl_net_error.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("NewApi")
    private void initWebView() {
        if (Build.VERSION.SDK_INT >= 11) {
            try {
                mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
            } catch (Exception e) {
                //ignor
            }
        }
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                //检测到下载文件就打开系统浏览器
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri content = Uri.parse(url);
                intent.setData(content);
                startActivity(intent);
            }
        });
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        mWebView.getSettings().setDefaultFontSize(16);
        mWebView.getSettings().setTextZoom(100);
        mWebView.getSettings().setAllowFileAccess(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // 设置可以使用localStorage
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.getSettings().setSavePassword(false);
//        mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString() + " sobot");

        //关于webview的http和https的混合请求的，从Android5.0开始，WebView默认不支持同时加载Https和Http混合模式。
        // 在API>=21的版本上面默认是关闭的，在21以下就是默认开启的，直接导致了在高版本上面http请求不能正确跳转。
        if (Build.VERSION.SDK_INT >= 21) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        //Android 4.4 以下的系统中存在一共三个有远程代码执行漏洞的隐藏接口
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        mWebView.removeJavascriptInterface("accessibility");
        mWebView.removeJavascriptInterface("accessibilityTraversal");

        //设置图片自适应屏幕
        mWebView.getSettings().setUseWideViewPort(true); //将图片调整到适合webview的大小
        mWebView.getSettings().setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        // 应用可以有数据库
        mWebView.getSettings().setDatabaseEnabled(true);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //注释的地方是打开其它应用，比如qq
                /*if (url.startsWith("http") || url.startsWith("https")) {
                    return false;
                } else {
                    Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(in);
                    return true;
                }*/
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                SobotLogUtils.i("网页--title---：" + title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress > 0 && newProgress < 100) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                } else if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                chooseAlbumPic();
                return true;
            }

        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mWebView != null) {
            mWebView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mWebView != null) {
            mWebView.removeAllViews();
            final ViewGroup viewGroup = (ViewGroup) mWebView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mWebView);
            }
            mWebView.destroy();
        }
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        //被摧毁前缓存一些数据
        outState.putString("url", mUrl);
        super.onSaveInstanceState(outState);
    }

    //选择照片，上传照片
    private static final int REQUEST_CODE_ALBUM = 0x0111;
    private static final int RESULT_OK = -1;
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;


    /**
     * 选择相册照片
     */
    private void chooseAlbumPic() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
//        i.setType("image/*");
        i.setType("video/*;image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), REQUEST_CODE_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ALBUM) {
            if (uploadMessage == null && uploadMessageAboveL == null) {
                return;
            }
            if (resultCode != RESULT_OK) {
                //一定要返回null,否则<input file> 就是没有反应
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }
                if (uploadMessageAboveL != null) {
                    uploadMessageAboveL.onReceiveValue(null);
                    uploadMessageAboveL = null;

                }
            }

            if (resultCode == RESULT_OK) {
                Uri imageUri = null;
                switch (requestCode) {
                    case REQUEST_CODE_ALBUM:

                        if (data != null) {
                            imageUri = data.getData();
                        }
                        break;
                }

                //上传文件
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(imageUri);
                    uploadMessage = null;
                }
                if (uploadMessageAboveL != null) {
                    uploadMessageAboveL.onReceiveValue(new Uri[]{imageUri});
                    uploadMessageAboveL = null;
                }
            }
        }
    }
}