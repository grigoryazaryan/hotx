package app.hotx.helper;

import android.content.Context;
import android.webkit.WebView;

public class HtmlViewer {
    private final static String TAG = "HtmlViewer";

    private static HtmlViewer instance;

    private WebView webview;
    private HtmlLoadedListener htmlLoadedListener;

    public static HtmlViewer getInstance(Context context) {
        if (instance == null)
            instance = new HtmlViewer(context);
        return instance;
    }

    private HtmlViewer(Context context) {
//        this.webview = new WebView(context);
////        webview.getSettings().setJavaScriptEnabled(true);
////        webview.getSettings().setDomStorageEnabled(true);
////        webview.getSettings().setAppCacheEnabled(true);
////        webview.getSettings().setAppCachePath(context.getFilesDir().getAbsolutePath() + "/cache");
////        webview.getSettings().setDatabaseEnabled(true);
//        webview.setWebChromeClient(new WebChromeClient() {
//            public void onProgressChanged(WebView view, int progress) {
//                // Activities and WebViews measure progress with different scales.
//                // The progress meter will automatically disappear when we reach 100%
////                activity.setProgress(progress * 1000);
//            }
//        });
//        webview.setWebViewClient(new WebViewClient() {
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                Log.v(TAG, description);
//                htmlLoadedListener.onError(description);
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                webview.loadUrl("javascript:window.googlejs.onLoaded" +
//                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
//            }
//        });
//        webview.addJavascriptInterface(new JavaScriptInterface() {
//            @JavascriptInterface
//            public void onLoaded(String html) {
//                htmlLoadedListener.onHtmlLoaded(html);
//            }
//        }, "googlejs");
    }

    public void loadUrl(String url, HtmlLoadedListener htmlLoadedListener) {
//        this.htmlLoadedListener = htmlLoadedListener;
//        webview.loadUrl(url);
//
    }
//
//    private interface JavaScriptInterface {
//        @JavascriptInterface
//        void onLoaded(String html);
//    }
//
    public interface HtmlLoadedListener {
        void onHtmlLoaded(String html);

        default void onError(String error) {
        }
    }
}
