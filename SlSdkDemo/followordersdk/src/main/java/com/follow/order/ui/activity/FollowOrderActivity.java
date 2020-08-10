package com.follow.order.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.follow.order.R;
import com.follow.order.base.MVPBaseActivity;
import com.follow.order.bean.MenuBean;
import com.follow.order.bean.TipBean;
import com.follow.order.event.MenuEvent;
import com.follow.order.event.MenuSelectEvent;
import com.follow.order.presenter.FollowOrderPresenter;
import com.follow.order.presenter.contract.FollowOrderContract;
import com.follow.order.ui.adapter.FollowMenuAdapter;
import com.follow.order.ui.dialog.FollowTipDialog;
import com.follow.order.ui.dialog.OrderWarnDialog;
import com.follow.order.ui.fragment.FollowOrderFragment;
import com.follow.order.ui.fragment.MyOrderFragment;
import com.follow.order.utils.CommonUtils;
import com.follow.order.utils.ShareUtil;
import com.follow.order.widget.CustomViewPager;
import com.follow.order.widget.MyFragmentPageAdapter;
import com.follow.order.widget.shape.RoundTextView;
import com.follow.order.widget.tab.SegmentTabLayout;
import com.follow.order.widget.tab.listener.OnTabSelectListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class FollowOrderActivity extends MVPBaseActivity<FollowOrderContract.View, FollowOrderPresenter> implements FollowOrderContract.View {
    private DrawerLayout drawerLayout;
    private LinearLayout llContent;
    private ImageButton ib_back;
    private SegmentTabLayout tabLayout;
    private CustomViewPager vpContent;
    private NestedScrollView menuView;
    private LinearLayout llMenu;
    private RecyclerView rv_menu1, rv_menu2;
    private RoundTextView btn_reset, btn_confirm;
    private FollowOrderPagerAdapter pagerAdapter;
    private FollowMenuAdapter menuAdapter1, menuAdapter2;
    private List<MenuBean> menu1List, menu2List;
    private String[] titles;

    public static void start(Context context) {
        context.startActivity(new Intent(context, FollowOrderActivity.class));
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_follow_order);
        drawerLayout = findViewById(R.id.drawer_layout);
        llContent = findViewById(R.id.ll_content);
        ib_back = findViewById(R.id.ib_back);
        tabLayout = findViewById(R.id.tab_layout);
        vpContent = findViewById(R.id.vp_content);
        menuView = findViewById(R.id.menu_view);
        llMenu = findViewById(R.id.ll_menu);
        rv_menu1 = findViewById(R.id.rv_menu1);
        rv_menu2 = findViewById(R.id.rv_menu2);
        btn_reset = findViewById(R.id.btn_reset);
        btn_confirm = findViewById(R.id.btn_confirm);

        titles = getResources().getStringArray(R.array.fo_tab_array);
        tabLayout.setTabData(titles);
        pagerAdapter = new FollowOrderPagerAdapter(getSupportFragmentManager(), titles);
        vpContent.setAdapter(pagerAdapter);

        if (menu1List == null) {
            menu1List = new ArrayList<>();
        }
        if (menu2List == null) {
            menu2List = new ArrayList<>();
        }

        GridLayoutManager layoutManager1 = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        rv_menu1.setLayoutManager(layoutManager1);
        menuAdapter1 = new FollowMenuAdapter(menu1List);
        rv_menu1.setAdapter(menuAdapter1);

        GridLayoutManager layoutManager2 = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        rv_menu2.setLayoutManager(layoutManager2);
        menuAdapter2 = new FollowMenuAdapter(menu2List);
        rv_menu2.setAdapter(menuAdapter2);


        EventBus.getDefault().register(this);
    }

    @Override
    protected void initListener() {
        ib_back.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public boolean onTabSelectBefore(int position) {
                return false;
            }

            @Override
            public void onTabSelect(int position) {
                if (vpContent != null) {
                    vpContent.setCurrentItem(position);
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        vpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (tabLayout != null) {
                    tabLayout.setCurrentTab(position);
                }
                if (position == 0) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);//打开手势滑动
                } else {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//关闭手势滑动
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initData() {
        mPresenter.getMenuData();
        mPresenter.getCoinList();
        showWarnDialog();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn_reset) {
            menuReset();
            menuConfirm();
            drawerLayout.closeDrawer(menuView);
        } else if (v.getId() == R.id.btn_confirm) {
            menuConfirm();
            drawerLayout.closeDrawer(menuView);
        } else if (v.getId() == R.id.ib_back) {
            back();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void showMenuData(List<MenuBean> menuData) {
        if (menuData == null || menuData.isEmpty()) {
            return;
        }
//        menuData.get(0).setSelect(1);
        menu1List.clear();
        menu1List.addAll(menuData);
        menuAdapter1.notifyDataSetChanged();
    }

    @Override
    public void showCoinList(List<MenuBean> coinData) {
        if (coinData == null || coinData.isEmpty()) {
            return;
        }
//        coinData.get(0).setSelect(1);
        menu2List.clear();
        menu2List.addAll(coinData);
        menuAdapter2.notifyDataSetChanged();
    }

    @Override
    public void showCommonDialog(TipBean tipData) {
        if (tipData == null || tipData.getIs_show() != 1) {
            return;
        }
        String key = "follow_tip_dialog" + tipData.getId();
        boolean isShow = ShareUtil.getBoolean(key, false);
        if (isShow) {
            return;
        }
        ShareUtil.putBoolean(key, true);
        FollowTipDialog dialog = new FollowTipDialog(this);
        dialog.setTitle(tipData.getTitle());
        dialog.setContent(tipData.getContent());
        if (tipData.getBtns() != null && tipData.getBtns().size() > 0) {
            if (tipData.getBtns().size() == 1) {
                String buttonStr = tipData.getBtns().get(0).getTitle();
                dialog.setButtonOnly(buttonStr, tipData.getBtns().get(0).getColor(), new FollowTipDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(View button, FollowTipDialog dialog) {
                        dialog.dismiss();
                    }
                });
            } else if (tipData.getBtns().size() > 1) {
                String button1 = tipData.getBtns().get(0).getTitle();
                final String url1 = tipData.getBtns().get(0).getUrl();
                String color1 = tipData.getBtns().get(0).getColor();
                String button2 = tipData.getBtns().get(1).getTitle();
                final String url2 = tipData.getBtns().get(1).getUrl();
                String color2 = tipData.getBtns().get(1).getColor();

                if (!TextUtils.isEmpty(color1) && color1.startsWith("#")) {
                    try {
                        dialog.getButton1().setTextColor(Color.parseColor(color1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!TextUtils.isEmpty(color2) && color2.startsWith("#")) {
                    try {
                        dialog.getButton2().setTextColor(Color.parseColor(color2));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                dialog.setButton1(button1, new FollowTipDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(View button, FollowTipDialog dialog) {
                        dialog.dismiss();
                        if (!TextUtils.isEmpty(url1)) {
                            CommonUtils.openUrlWoithDefaultBrower(FollowOrderActivity.this, url1);
                        }
                    }
                });
                dialog.setButton2(button2, new FollowTipDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(View button, FollowTipDialog dialog) {
                        dialog.dismiss();
                        if (!TextUtils.isEmpty(url2)) {
                            CommonUtils.openUrlWoithDefaultBrower(FollowOrderActivity.this, url2);
                        }
                    }
                });
            }
            dialog.show();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void menuToggle(MenuEvent event) {
        if (drawerLayout == null) {
            return;
        }
        if (drawerLayout.isDrawerOpen(menuView)) {
            drawerLayout.closeDrawer(menuView);
        } else {
            drawerLayout.openDrawer(menuView);
        }
    }

    private void menuReset() {
        for (int i = 0; i < menu1List.size(); i++) {
//            if (i == 0) {
//                menu1List.get(i).setSelect(1);
//            } else {
            menu1List.get(i).setSelect(0);
//            }
        }
        for (int i = 0; i < menu2List.size(); i++) {
//            if (i == 0) {
//                menu2List.get(i).setSelect(1);
//            } else {
            menu2List.get(i).setSelect(0);
//            }
        }
        menuAdapter1.notifyDataSetChanged();
        menuAdapter2.notifyDataSetChanged();
    }

    /**
     * 筛选单选
     */
    private void menuConfirm() {
        String style = "";
        String coin = "";

        for (int i = 0; i < menu1List.size(); i++) {
            MenuBean menuBean = menu1List.get(i);
            if (menuBean.getSelect() == 1) {
                style = menuBean.getId();
            }
        }
        for (int i = 0; i < menu2List.size(); i++) {
            MenuBean menuBean = menu2List.get(i);
            if (menuBean.getSelect() == 1) {
                coin = menuBean.getTitle();
            }
        }
        if (TextUtils.equals(coin, getString(R.string.fo_action_all))) {
            coin = "";
        }
        MenuSelectEvent event = new MenuSelectEvent();
        event.setStyle(style);
        event.setCoin(coin);
        EventBus.getDefault().post(event);
    }

//    /**
//     * 筛选多选
//     */
//    private void menuConfirm() {
//        String style = "";
//        String coin = "";
//
//        for (int i = 0; i < menu1List.size(); i++) {
//            MenuBean menuBean = menu1List.get(i);
//            if (menuBean.getSelect() == 1) {
//                style = style + menuBean.getId() + ",";
//            }
//        }
//        for (int i = 0; i < menu2List.size(); i++) {
//            MenuBean menuBean = menu2List.get(i);
//            if (menuBean.getSelect() == 1) {
//                coin = coin + menuBean.getId() + ",";
//            }
//        }
//        if (style.endsWith(",")) {
//            style = style.substring(0, style.length() - 1);
//        }
//        if (coin.endsWith(",")) {
//            coin = coin.substring(0, coin.length() - 1);
//        }
//        if (TextUtils.isEmpty(style)) {
//            style = "0";
//        }
//        if (TextUtils.isEmpty(coin)) {
//            coin = "0";
//        }
//        MenuSelectEvent event = new MenuSelectEvent();
//        event.setStyle(style);
//        event.setCoin(coin);
//        EventBus.getDefault().post(event);
//    }

    private void showWarnDialog() {
        boolean isFirst = ShareUtil.getBoolean("follow_warn", true);
        if (isFirst) {
            OrderWarnDialog dialog = new OrderWarnDialog(this);
            dialog.show();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mPresenter.getCommonDialog();
                }
            });
            ShareUtil.putBoolean("follow_warn", false);
        } else {
            mPresenter.getCommonDialog();
        }
    }


    class FollowOrderPagerAdapter extends MyFragmentPageAdapter {
        private FragmentManager mFm;
        private String[] mTitles;

        public FollowOrderPagerAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            mFm = fm;
            mTitles = titles;
            List<String> tags = new ArrayList<>();
            for (String title : titles) {
                tags.add(title + "FollowOrderPagerAdapter");
            }
            setTagName(tags);
        }

        /**
         * Return the Fragment associated with a specified positionTab.
         *
         * @param position
         */
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = mFm.findFragmentByTag(tagNames.get(position));
            if (fragment == null) {
                String title = mTitles[position];
                if (TextUtils.equals(title, getString(R.string.fo_tab_1))) {
                    fragment = new FollowOrderFragment();
                } else if (TextUtils.equals(title, getString(R.string.fo_tab_2))) {
                    fragment = new MyOrderFragment();
                }
            }
            return fragment;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            return tagNames.size();
        }
    }
}
