package com.example.q.pocketmusic.module.home.profile.collection;

import com.example.q.pocketmusic.callback.ToastQueryListListener;
import com.example.q.pocketmusic.callback.ToastQueryListener;
import com.example.q.pocketmusic.callback.ToastUpdateListener;
import com.example.q.pocketmusic.model.bean.MyUser;
import com.example.q.pocketmusic.model.bean.collection.CollectionPic;
import com.example.q.pocketmusic.model.bean.collection.CollectionSong;
import com.example.q.pocketmusic.module.common.BaseModel;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by 鹏君 on 2017/4/22.
 */

public class CollectionModel extends BaseModel {

    public CollectionModel() {
    }

    public void getUserCollectionList(MyUser user, int page, ToastQueryListener<CollectionSong> listener) {
        BmobQuery<CollectionSong> query = new BmobQuery<>();
        initDefaultListQuery(query, page);
        query.addWhereRelatedTo("collections", new BmobPointer(user));
        query.findObjects(listener);
    }


    public void getCollectionPicList(CollectionSong collectionSong, ToastQueryListener<CollectionPic> listener) {
        BmobQuery<CollectionPic> queryComment = new BmobQuery<>();
        queryComment.addWhereEqualTo("collectionSong", new BmobPointer(collectionSong));
        queryComment.findObjects(listener);
    }

    //删除收藏
    public void deleteCollection(MyUser user, final CollectionSong collectionSong, final ToastUpdateListener listener) {
        BmobRelation relation = new BmobRelation();
        relation.remove(collectionSong);
        user.setCollections(relation);
        user.update(new ToastUpdateListener() {
            @Override
            public void onSuccess() {
                //删除收藏多个图片表,
                BmobQuery<CollectionPic> query = new BmobQuery<>();
                query.addWhereEqualTo("collectionSong", collectionSong);
                query.findObjects(new ToastQueryListener<CollectionPic>() {
                    @Override
                    public void onSuccess(List<CollectionPic> list) {
                        List<BmobObject> pics = new ArrayList<>();
                        pics.addAll(list);
                        new BmobBatch().deleteBatch(pics).doBatch(new ToastQueryListListener<BatchResult>() {
                            @Override
                            public void onSuccess(List<BatchResult> list) {
                                //删除收藏记录
                                collectionSong.delete(listener);
                            }
                        });
                    }
                });

            }
        });
    }


}
