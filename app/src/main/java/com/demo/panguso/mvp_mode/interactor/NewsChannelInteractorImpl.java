package com.demo.panguso.mvp_mode.interactor;

import android.util.Log;

import com.demo.panguso.mvp_mode.component.db.NewsChannelTableManager;
import com.demo.panguso.mvp_mode.response.RequestCallBack;

import java.util.List;

import greendao.NewsChannelTable;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ${yangfang} on 2016/9/19.
 */
public class NewsChannelInteractorImpl implements NewsChannelInteractor<List<NewsChannelTable>> {
    @Override
    public Subscription loadNewsChannels(final RequestCallBack<List<NewsChannelTable>> callBack) {
        Log.e("NewsorImpl","999999");
        return Observable.create(new Observable.OnSubscribe<List<NewsChannelTable>>() {
            @Override
            public void call(Subscriber<? super List<NewsChannelTable>> subscriber) {
                NewsChannelTableManager.initDB();
                subscriber.onNext(NewsChannelTableManager.loadNewsChannels());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<NewsChannelTable>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError("加载失败");
                    }

                    @Override
                    public void onNext(List<NewsChannelTable> newsChannelTables) {
                        callBack.success(newsChannelTables);
                    }
                });

    }
}
