package com.example.q.pocketmusic.module.home.seek.ask.comment;

import com.example.q.pocketmusic.callback.ToastQueryListener;
import com.example.q.pocketmusic.config.BmobConstant;
import com.example.q.pocketmusic.config.Constant;
import com.example.q.pocketmusic.model.bean.ask.AskSongComment;
import com.example.q.pocketmusic.model.bean.ask.AskSongPic;
import com.example.q.pocketmusic.model.bean.ask.AskSongPost;
import com.example.q.pocketmusic.module.common.BaseModel;
import com.example.q.pocketmusic.util.BmobUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;

/**
 * Created by 鹏君 on 2017/4/22.
 */

public class AskSongCommentModel extends BaseModel {
    private List<String> picUrls;

    public AskSongCommentModel() {
        picUrls = new ArrayList<>();
    }

    public void getInitCommentList(AskSongPost post, ToastQueryListener<AskSongComment> listener) {
        BmobQuery<AskSongComment> queryComment = new BmobQuery<>();
        queryComment.order("-agreeNum," + BmobConstant.BMOB_CREATE_AT);
        queryComment.setLimit(10);
        queryComment.setSkip(0);
        queryComment.addWhereEqualTo("post", new BmobPointer(post));
        queryComment.include("user,post.user");
        queryComment.findObjects(listener);
    }

    public void getUserCommentList(AskSongPost post, int page, ToastQueryListener<AskSongComment> listener) {
        BmobQuery<AskSongComment> queryComment = new BmobQuery<>();
        queryComment.order("-agreeNum," + BmobConstant.BMOB_CREATE_AT);
        queryComment.setLimit(DEFAULT_LIMIT);
        queryComment.setSkip(DEFAULT_LIMIT * page);
        queryComment.addWhereEqualTo("post", new BmobPointer(post));
        queryComment.include("user,post.user");
        queryComment.findObjects(listener);
    }

    public void getPicList(AskSongComment askSongComment, ToastQueryListener<AskSongPic> listener) {
        BmobQuery<AskSongPic> query = new BmobQuery<>();
        query.addWhereEqualTo("comment", new BmobPointer(askSongComment));
        query.findObjects(listener);
    }

    public List<String> getPicUrls() {
        return picUrls;
    }

}
