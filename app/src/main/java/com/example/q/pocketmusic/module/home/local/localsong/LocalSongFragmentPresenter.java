package com.example.q.pocketmusic.module.home.local.localsong;

import android.content.Intent;

import com.example.q.pocketmusic.config.Constant;
import com.example.q.pocketmusic.model.bean.Song;
import com.example.q.pocketmusic.model.bean.SongObject;
import com.example.q.pocketmusic.model.bean.local.LocalSong;
import com.example.q.pocketmusic.model.db.ImgDao;
import com.example.q.pocketmusic.model.db.LocalSongDao;
import com.example.q.pocketmusic.model.net.LoadLocalSongList;
import com.example.q.pocketmusic.model.net.SynchronizeLocalSong;
import com.example.q.pocketmusic.module.common.BasePresenter;
import com.example.q.pocketmusic.module.common.IBaseView;
import com.example.q.pocketmusic.module.share.ShareActivity;
import com.example.q.pocketmusic.module.song.SongActivity;
import com.example.q.pocketmusic.util.SortUtil;
import com.example.q.pocketmusic.util.common.LogUtils;
import com.example.q.pocketmusic.util.common.ToastUtil;
import com.example.q.pocketmusic.util.common.SharedPrefsUtil;

import java.util.List;

/**
 * Created by 鹏君 on 2016/9/2.
 */
public class LocalSongFragmentPresenter extends BasePresenter<LocalSongFragmentPresenter.IView> {
    private IView fragment;
    private LocalSongDao localSongDao;
    private ImgDao imgDao;


    //Dao有必要关闭吗？iterator呢？
    public LocalSongFragmentPresenter(IView fragment) {
        attachView(fragment);
        this.fragment = getIViewRef();
        localSongDao = new LocalSongDao(fragment.getAppContext());
        imgDao = new ImgDao(fragment.getAppContext());

    }

    public void loadLocalSong() {
        if (fragment.getAppContext() == null) {
            return;
        }
        new LoadLocalSongList(localSongDao, fragment.getAppContext()) {
            @Override
            protected void onPostExecute(List<LocalSong> localSongs) {
                super.onPostExecute(localSongs);
                fragment.setList(localSongs);
                LogUtils.e(TAG, "本地乐谱数量：" + localSongs.size());
            }
        }.execute();

    }

    //删除乐谱要删除数据库和list.position,还有本地的文件！
    public void deleteSong(LocalSong localSong) {
        localSongDao.deleteLocalRelation(fragment.getCurrentContext(),localSong);
    }

    //同步乐谱
    public void synchronizedSong() {
        //先遍历文件夹的图片，添加到数据库（不重复添加），然后再从数据库取出来
        new SynchronizeLocalSong(imgDao, localSongDao) {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                loadLocalSong();
            }
        }.execute();
    }


    public void enterShareActivity(LocalSong localSong) {
        Intent intent = new Intent(fragment.getCurrentContext(), ShareActivity.class);
        intent.putExtra(ShareActivity.LOCAL_SONG, localSong);
        fragment.getCurrentContext().startActivity(intent);
    }

    public void enterPictureActivity(LocalSong localSong) {
        Intent intent = new Intent(fragment.getCurrentContext(), SongActivity.class);
        Song song = new Song();
        song.setName(localSong.getName());
        SongObject songObject = new SongObject(song, Constant.FROM_LOCAL, Constant.SHOW_NO_MENU, Constant.LOCAL);
        intent.setExtrasClassLoader(getClass().getClassLoader());
        intent.putExtra(SongActivity.PARAM_SONG_OBJECT_SERIALIZEABLE,songObject);
        intent.putExtra(SongActivity.LOCAL_SONG,localSong);
        fragment.getCurrentContext().startActivity(intent);
    }

    public void setTop(LocalSong item) {
        int top_value = SharedPrefsUtil.getInt(SortUtil.sort_key, SortUtil.sort_value);
        top_value++;
        item.setSort(top_value);
        SharedPrefsUtil.putInt(SortUtil.sort_key, top_value);//修改最高值
        localSongDao.update(item);
        ToastUtil.showToast( "已置顶");
        fragment.onRefresh();
    }


    public interface IView extends IBaseView {
        void setList(List<LocalSong> list);


        void onRefresh();
    }
}
