package com.follow.order.ui.adapter;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.follow.order.FollowOrderSDK;
import com.follow.order.R;
import com.follow.order.bean.MenuBean;
import com.follow.order.widget.shape.RoundTextView;
import com.follow.order.widget.shape.RoundViewDelegate;

import java.util.List;


public class FollowMenuAdapter extends BaseQuickAdapter<MenuBean, FollowMenuAdapter.MenuViewHolder> {

    public FollowMenuAdapter(@Nullable List<MenuBean> data) {
        super(R.layout.fo_item_menu, data);
    }

    @Override
    protected void convert(MenuViewHolder holder, final MenuBean bean) {
        if (bean == null) {
            return;
        }
        bean.setTitle(bean.getTitle());
        holder.tvMenu.setText(bean.getTitle());
        RoundViewDelegate delegate = holder.tvMenu.getDelegate();
        if (bean.getSelect() == 1) {
            holder.tvMenu.setTextColor(ContextCompat.getColor(mContext, R.color.fo_blue));
            delegate.setBackgroundColor(FollowOrderSDK.ins().getCustomAttrColor(mContext, R.attr.fo_menu_item_bg_sel_color));
        } else {
            holder.tvMenu.setTextColor(ContextCompat.getColor(mContext, R.color.fo_desc_color));
            delegate.setBackgroundColor(FollowOrderSDK.ins().getCustomAttrColor(mContext, R.attr.fo_menu_item_bg_nor_color));
        }
        holder.tvMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                if (TextUtils.equals("0", bean.getId())) {//全部
//                    if (bean.getSelect() == 1) {
//                        bean.setSelect(0);
//                    } else {
//                        bean.setSelect(1);
//                        queryAll();
//                    }
//                } else {//其他
//                    queryOther();
//                    bean.setSelect(bean.getSelect() == 1 ? 0 : 1);
//                }
                selectItem(bean);
                notifyDataSetChanged();
            }
        });
    }

    private void queryAll() {
        for (int i = 0; i < getData().size(); i++) {
            MenuBean menuBean = getData().get(i);
            if (!TextUtils.equals("0", menuBean.getId()) && menuBean.getSelect() == 1) {
                menuBean.setSelect(0);
            }
        }
    }

    private void queryOther() {
        for (int i = 0; i < getData().size(); i++) {
            MenuBean menuBean = getData().get(i);
            if (TextUtils.equals("0", menuBean.getId())) {
                menuBean.setSelect(0);
            }
        }
    }

    private void selectItem(MenuBean bean) {
        if (bean.getSelect() == 1) {
            bean.setSelect(0);
        } else {
            for (int i = 0; i < getData().size(); i++) {
                MenuBean menuBean = getData().get(i);
                if (TextUtils.equals(bean.getId(), menuBean.getId())) {
                    menuBean.setSelect(1);
                } else {
                    menuBean.setSelect(0);
                }
            }
        }
    }

    public static class MenuViewHolder extends BaseViewHolder {
        RoundTextView tvMenu;

        public MenuViewHolder(View view) {
            super(view);
            tvMenu = view.findViewById(R.id.tv_menu);
        }
    }

}
