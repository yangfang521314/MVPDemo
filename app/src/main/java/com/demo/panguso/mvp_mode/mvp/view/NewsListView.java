package com.demo.panguso.mvp_mode.mvp.view;

import com.demo.panguso.mvp_mode.common.LoadNewsType;
import com.demo.panguso.mvp_mode.mvp.bean.NewsSummary;
import com.demo.panguso.mvp_mode.mvp.view.base.BaseView;

import java.util.List;

/**
 * Created by ${yangfang} on 2016/9/9.
 */
public interface NewsListView extends BaseView{
    void setItems(List<NewsSummary> items, @LoadNewsType.checker int loadType);
}
