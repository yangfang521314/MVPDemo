package com.demo.panguso.mvp_mode.mvp.view.base;

/**
 * Created by ${yangfang} on 2016/9/8.
 */
public interface BaseView {
    void showProgress();

    void hideProgress();

    void showMessage(String message);

    void showErrorMsg(String message);
    void onDestory();
}
