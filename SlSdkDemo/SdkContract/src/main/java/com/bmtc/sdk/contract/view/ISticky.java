package com.bmtc.sdk.contract.view;

/**
 * Created by zhoujing on 2018/12/5.
 */

public interface ISticky {
    //判断是否为同类别的第一个位置
    boolean isFirstPosition(int pos);
    //获取标题
    String getGroupTitle(int pos);
}
