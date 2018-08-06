package com.allever.social.mvp.base;

import java.lang.ref.WeakReference;

public abstract class BasePresenter<V> {

    protected WeakReference<V> mViewRef;

    public V getView(){
        return mViewRef.get();
    }

    public void attachView(V view){
        mViewRef = new WeakReference<>(view);
    }

    public void detchView(){
        if (mViewRef!= null && mViewRef.get() != null){
            mViewRef.clear();
            mViewRef = null;
        }
    }

    public boolean isAttach(){
        return mViewRef != null && mViewRef.get() != null;
    }
}
