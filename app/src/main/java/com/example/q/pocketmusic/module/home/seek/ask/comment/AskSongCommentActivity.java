package com.example.q.pocketmusic.module.home.seek.ask.comment;

import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.q.pocketmusic.R;
import com.example.q.pocketmusic.callback.AbsOnClickItemHeadListener;
import com.example.q.pocketmusic.model.bean.Song;
import com.example.q.pocketmusic.model.bean.ask.AskSongComment;
import com.example.q.pocketmusic.model.bean.ask.AskSongPost;
import com.example.q.pocketmusic.module.common.AuthActivity;
import com.example.q.pocketmusic.util.common.ToastUtil;
import com.example.q.pocketmusic.view.dialog.CoinDialogBuilder;
import com.example.q.pocketmusic.view.dialog.PicDialog;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.List;

import butterknife.BindView;

/**
 * Created by 鹏君 on 2016/11/14.
 */

public class AskSongCommentActivity extends AuthActivity<AskSongCommentPresenter.IView, AskSongCommentPresenter>
        implements AskSongCommentPresenter.IView, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, PostHeadView.OnClickIndexListener
        , RecyclerArrayAdapter.OnMoreListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.add_pic)
    ImageView addPic;
    @BindView(R.id.user_input_edt)
    EditText userInputEdt;
    @BindView(R.id.send_comment_btn)
    Button sendCommentBtn;
    @BindView(R.id.input_ll)
    LinearLayout inputLl;
    @BindView(R.id.recycler)
    EasyRecyclerView recycler;
    @BindView(R.id.number_pic_tv)
    TextView numberPicTv;
    private AskSongCommentAdapter adapter;
    private PicDialog picDialog;
    private PostHeadView headView;
    public static final String PARAM_POST = "param_post";
    public final static String PARAM_IS_FROM_USER = "param_is_from_user";//是否个人

    @Override
    public int setContentResource() {
        return R.layout.activity_ask_song_comment;
    }


    @Override
    public void initUserView() {
        //各种监听
        adapter = new AskSongCommentAdapter(this);
        recycler.setRefreshListener(this);
        sendCommentBtn.setOnClickListener(this);
        addPic.setOnClickListener(this);
        //数据初始化
        final AskSongPost post = (AskSongPost) getIntent().getSerializableExtra(PARAM_POST);
        Boolean isFromUser = getIntent().getBooleanExtra(PARAM_IS_FROM_USER, false);
        presenter.setPost(post);
        presenter.setUser(user);
        initToolbar(toolbar, presenter.getPost().getTitle());
        initRecyclerView(recycler, adapter);
        adapter.setMore(R.layout.view_more, this);

        headView = new PostHeadView(context,
                presenter.getPost().getContent(),
                presenter.getPost().getUser().getNickName(),
                presenter.getPost().getTitle(),
                presenter.getPost().getUser().getHeadImg(),
                presenter.getPost().getCreatedAt(), isFromUser,
                presenter.getPost().getIndex());
        adapter.addHeader(headView);
        headView.setOnClickIndexListener(this);
        adapter.setAbsOnClickItemHeadListener(new AbsOnClickItemHeadListener() {
            @Override
            public void onClickItem(int position) {
                presenter.alertPicDialog(adapter.getItem(position));  //弹出简略图
            }
        });
        presenter.getInitCommentList(true);
    }


    //加载评论列表
    @Override
    public void setCommentList(boolean isRefreshing,List<AskSongComment> list) {
        if (isRefreshing){
            adapter.clear();
        }
        adapter.addAll(list);
    }


    //发送评论返回
    @Override
    public void sendCommentResult(String s, AskSongComment askSongComment) {
        adapter.add(askSongComment);
        numberPicTv.setText(0 + " 张");
    }


    //添加图片返回,最好有个列表可以展示
    @Override
    public void addPicResult(List<String> picUrls) {
        ToastUtil.showToast("已经添加" + picUrls.size());
        numberPicTv.setText(picUrls.size() + " 张");
    }

    //发送评论后，输入框为空
    @Override
    public void setCommentInput(String s) {
        userInputEdt.setText(s);
    }

    @Override
    public void showPicDialog(final Song song, final AskSongComment askSongComment) {
        picDialog = new PicDialog.Builder(context)
                .setFirstPath(song.getIvUrl().get(0))
                .setOnSelectListener(new PicDialog.Builder.OnSelectListener() {
                    @Override
                    public void onSelectOk() {
                        //进入SongActivity
                        presenter.enterSongActivity(song, askSongComment);
                    }
                })
                .create();
        picDialog.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_comment_btn://发送评论
                String comment = userInputEdt.getText().toString().trim();
                presenter.sendComment(comment);
                break;
            case R.id.add_pic://添加图片
                alertSelectedDialog();
                break;
        }
    }

    //从本地或者相册选择曲谱
    private void alertSelectedDialog() {
        final CharSequence[] charSequences = new CharSequence[2];
        charSequences[0] = "--> 本地曲谱 <--";
        charSequences[1] = "--> 手机相册 <--";
        new AlertDialog.Builder(getCurrentContext())
                .setSingleChoiceItems(charSequences, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {//从本地曲谱中选
                            presenter.queryLocalSongList();
                        } else {//从手机相册中选
                            presenter.addPicByAlbum();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    //弹出框，选择本地曲谱中的某一首
    @Override
    public void alertLocalSongDialog(List<String> list) {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getCurrentContext()
                , android.R.layout.select_dialog_item
                , android.R.id.text1
                , list);

        new AlertDialog.Builder(getCurrentContext())
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.addPicByLocalSong(adapter.getItem(which));//得到他的图片路径
                    }
                })
                .show();
    }

    @Override
    public void addHotIndex() {
        headView.addHotIndex();
    }

    @Override
    public void onRefresh() {
        presenter.getInitCommentList(true);
    }


    @Override
    protected AskSongCommentPresenter createPresenter() {
        return new AskSongCommentPresenter(this);
    }

    @Override
    public void onClick() {
        alertCoinDialog();
    }

    private void alertCoinDialog() {
        new CoinDialogBuilder(this, 2)
                .setMessage("增加求谱指数:1点/2枚硬币")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.reduceIndexCoin();
                    }
                })
                .setNegativeButton("算了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onMoreShow() {
        presenter.getMoreCommentList();
    }

    @Override
    public void onMoreClick() {

    }
}