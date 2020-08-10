package com.follow.order.net;

public abstract class DownloadCallBack {
    public void onStart(){}

    public void onCompleted(){}

    abstract public void onError(Throwable e);

    public void onProgress(long total,long current){}

    abstract public void onSucess(String path, String name, long fileSize);
}
