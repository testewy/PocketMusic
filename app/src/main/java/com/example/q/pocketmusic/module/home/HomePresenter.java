package com.example.q.pocketmusic.module.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import com.example.q.pocketmusic.R;
import com.example.q.pocketmusic.callback.ToastQueryListener;
import com.example.q.pocketmusic.module.common.BasePresenter;
import com.example.q.pocketmusic.module.common.IBaseView;
import com.example.q.pocketmusic.module.home.seek.HomeSeekFragment;
import com.example.q.pocketmusic.module.home.local.HomeLocalFragment;
import com.example.q.pocketmusic.module.home.net.HomeNetFragment;
import com.example.q.pocketmusic.module.home.profile.HomeProfileFragment;
import com.example.q.pocketmusic.util.common.LogUtils;
import com.example.q.pocketmusic.util.common.update.UpdateUtils;
import com.example.q.pocketmusic.util.common.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.update.AppVersion;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;

/**
 * Created by 鹏君 on 2016/11/22.
 */

public class HomePresenter extends BasePresenter<HomePresenter.IView> {
    private IView activity;
    private List<Fragment> fragments;
    private FragmentManager fm;
    private Fragment totalFragment;
    private static int FLAG_SELECT_NET = 1001;
    private static int FLAG_SELECT_ASK = 1002;
    private static int FLAG_SELECT_LOCAL = 1003;
    private static int FLAG_SELECT_PROFILE = 1004;
    private HomeNetFragment homeNetFragment;//1
    private HomeSeekFragment homeSeekFragment;//2
    private HomeLocalFragment homeLocalFragment;//3
    private HomeProfileFragment homeProfileFragment;//4
    private int FLAG;//标记当前Fragment


    public HomePresenter(IView activity) {
        attachView(activity);
        this.activity = getIViewRef();
        initFragment();
    }

    private void initFragment() {
        fragments = new ArrayList<>();
        homeNetFragment = new HomeNetFragment();
        homeSeekFragment = new HomeSeekFragment();
        homeLocalFragment = new HomeLocalFragment();
        homeProfileFragment = new HomeProfileFragment();
        fragments.add(homeNetFragment);
        fragments.add(homeSeekFragment);
        fragments.add(homeLocalFragment);
        fragments.add(homeProfileFragment);
    }

    //网络
    public void clickNet() {
        if (FLAG != FLAG_SELECT_NET) {
            FLAG = FLAG_SELECT_NET;
            showFragment(fragments.get(0));
            activity.onSelectNet();
        }
    }


    //求谱
    public void clickAsk() {
        if (FLAG != FLAG_SELECT_ASK) {
            FLAG = FLAG_SELECT_ASK;
            showFragment(fragments.get(1));
            activity.onSelectAsk();
        }
    }

    //本地
    public void clickLocal() {
        if (FLAG != FLAG_SELECT_LOCAL) {
            FLAG = FLAG_SELECT_LOCAL;
            showFragment(fragments.get(2));
            activity.onSelectLocal();
        }
    }

    public void clickProfile() {
        if (FLAG != FLAG_SELECT_PROFILE) {
            FLAG = FLAG_SELECT_PROFILE;
            showFragment(fragments.get(3));
            activity.onSelectProfile();
        }
    }

    private void showFragment(Fragment fragment) {
        if (!fragment.isAdded()) {
            if (totalFragment == null) {
                fm.beginTransaction().add(R.id.home_content, fragment, fragment.getClass().getName()).commit();
            } else {
                fm.beginTransaction().hide(totalFragment).add(R.id.home_content, fragment, fragment.getClass().getName()).commit();
            }
        } else {
            fm.beginTransaction().hide(totalFragment).show(fragment).commit();
        }
        totalFragment = fragment;
    }

    //检查版本更新
    public void checkVersion() {
        UpdateUtils.getInstanse().update(activity.getCurrentContext());
    }


    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fm = fragmentManager;
    }


    public interface IView extends IBaseView {

        void onSelectLocal();

        void onSelectNet();

        void onSelectAsk();

        void onSelectProfile();
    }
}
