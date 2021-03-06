package com.example.q.pocketmusic.module.home.seek;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.example.q.pocketmusic.R;
import com.example.q.pocketmusic.module.common.BaseFragment;
import com.example.q.pocketmusic.module.common.BasePresenter;
import com.example.q.pocketmusic.module.common.IBaseView;
import com.example.q.pocketmusic.module.home.seek.ask.AskListFragment;
import com.example.q.pocketmusic.module.home.seek.publish.AskSongActivity;
import com.example.q.pocketmusic.module.home.seek.share.ShareListFragment;
import com.example.q.pocketmusic.module.search.SearchMainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 鹏君 on 2017/5/16.
 */

public class HomeSeekPresenter extends BasePresenter<HomeSeekPresenter.IView> {
    private static int FLAG_SELECT_ASK = 1001;
    private static int FLAG_SELECT_SHARE = 1002;
    private AskListFragment askListFragment;
    private ShareListFragment shareListFragment;
    private Fragment totalFragment;
    private FragmentManager fm;
    private int FLAG;
    private IView fragment;
    private List<Fragment> fragments;

    public HomeSeekPresenter(IView fragment) {
        attachView(fragment);
        this.fragment = getIViewRef();
        initFragment();
    }

    public void setFragmentManager(FragmentManager fm) {
        this.fm = fm;
    }

    private void initFragment() {
        fragments = new ArrayList<>();
        askListFragment = new AskListFragment();
        shareListFragment = new ShareListFragment();
        fragments.add(askListFragment);
        fragments.add(shareListFragment);
    }

    //点击求谱列表
    public void clickAsk() {
        if (FLAG != FLAG_SELECT_ASK) {
            FLAG = FLAG_SELECT_ASK;
            showFragment(fragments.get(0));
            fragment.onSelectAsk();
        }
    }

    //点击分享列表
    public void clickShare() {
        if (FLAG != FLAG_SELECT_SHARE) {
            FLAG = FLAG_SELECT_SHARE;
            showFragment(fragments.get(1));
            fragment.onSelectShare();
        }
    }

    private void showFragment(Fragment fragment) {
        if (!fragment.isAdded()) {
            if (totalFragment == null) {
                fm.beginTransaction().add(R.id.seek_content, fragment, fragment.getClass().getName()).commit();
            } else {
                fm.beginTransaction().hide(totalFragment).add(R.id.seek_content, fragment, fragment.getClass().getName()).commit();
            }
        } else {
            fm.beginTransaction().hide(totalFragment).show(fragment).commit();
        }
        totalFragment = fragment;
    }

    //跳转到AskSongActivity
    public void enterAskSongActivity() {
        Intent intent = new Intent(fragment.getCurrentContext(), AskSongActivity.class);
        //注意这里使用的是Fragment的方法，而不能用Activity的方法
        ((BaseFragment) fragment).startActivityForResult(intent, AskSongActivity.REQUEST_ASK);
    }

    public void enterSearchMainActivity() {
        fragment.getCurrentContext().startActivity(new Intent(fragment.getCurrentContext(), SearchMainActivity.class));
    }

    interface IView extends IBaseView {

        void onSelectAsk();

        void onSelectShare();
    }
}
