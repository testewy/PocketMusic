package com.example.q.pocketmusic.module.home.net;

import android.content.Context;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.q.pocketmusic.R;
import com.example.q.pocketmusic.model.bean.Song;
import com.example.q.pocketmusic.model.flag.BannerBean;
import com.example.q.pocketmusic.model.flag.ContentLL;
import com.example.q.pocketmusic.model.flag.TextTv;

import com.example.q.pocketmusic.model.flag.Divider;
import com.example.q.pocketmusic.util.InstrumentFlagUtil;
import com.example.q.pocketmusic.view.widget.view.TypeView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.rollviewpager.RollPagerView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 鹏君 on 2016/11/7.
 */
//暂时取消点赞功能
public class NetFragmentAdapter extends RecyclerArrayAdapter<Object> {
    public static final int BANNER = 0;
    public static final int TEXT = 1;
    public static final int TYPE_SONG = 2;
    public static final int DIVIDER = 3;
    public static final int RECOMMEND = 4;
    private OnOptionListener listener;

    public void setListener(OnOptionListener listener) {
        this.listener = listener;
    }

    public interface OnOptionListener {
        void onSelectType(int position);

        void onSelectRecommendSong(int position);

        void onSelectRollView(int picPosition);
    }

    public NetFragmentAdapter(Context context) {
        super(context);
    }


    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case BANNER:
                return new BannerViewHolder(parent);
            case TEXT:
                return new TextViewHolder(parent);
            case TYPE_SONG:
                return new TypeViewHolder(parent);
            case DIVIDER:
                return new DividerViewHolder(parent);
        }
        return new RecommendViewHolder(parent);
    }

    @Override
    public int getViewType(int position) {
        if (getItem(position) instanceof TextTv) {
            return TEXT;
        } else if (getItem(position) instanceof ContentLL) {
            return TYPE_SONG;
        } else if (getItem(position) instanceof BannerBean) {
            return BANNER;
        } else if (getItem(position) instanceof Divider) {
            return DIVIDER;
        } else
            return RECOMMEND;
    }


    //乐器list的holder
    class TypeViewHolder extends BaseViewHolder<ContentLL> implements View.OnClickListener {


        public TypeViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_combination_type_ll);
            ($(R.id.type_quanbu_tv)).setOnClickListener(this);
            ($(R.id.type_hulu_tv)).setOnClickListener(this);
            ($(R.id.type_jita_tv)).setOnClickListener(this);
            ($(R.id.type_gangqin_tv)).setOnClickListener(this);
            ($(R.id.type_sakesi_tv)).setOnClickListener(this);
            ($(R.id.type_erhu_tv)).setOnClickListener(this);
            ($(R.id.type_guzheng_tv)).setOnClickListener(this);
            ($(R.id.type_dianziqin_tv)).setOnClickListener(this);
            ($(R.id.type_pipa_tv)).setOnClickListener(this);
            ($(R.id.type_kouqin_tv)).setOnClickListener(this);
            ($(R.id.type_changdi_tv)).setOnClickListener(this);
            ($(R.id.type_dixiao_tv)).setOnClickListener(this);
            ($(R.id.type_shoufengqin_tv)).setOnClickListener(this);
            ($(R.id.type_tiqin_tv)).setOnClickListener(this);
            ($(R.id.type_tongguan_tv)).setOnClickListener(this);
            ($(R.id.type_yangqin_tv)).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = InstrumentFlagUtil.getFlag(v.getId());
                listener.onSelectType(position);
            }
        }
    }

    //两个Text的holder
    public class TextViewHolder extends BaseViewHolder<TextTv> {
        public TextView textTv;

        public TextViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_combination_text);
            textTv = $(R.id.text_tv);
        }

        @Override
        public void setData(TextTv data) {
            super.setData(data);
            textTv.setText(data.getName());
        }
    }

    //分割线
    class DividerViewHolder extends BaseViewHolder<Divider> {

        public DividerViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_conbination_divider);
        }
    }

    //广告轮播的holder
    class BannerViewHolder extends BaseViewHolder<BannerBean> {
        RollPagerView rollPagerView;

        public BannerViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_combination_banner);
            rollPagerView = $(R.id.roll_pager_view);
            rollPagerView.setAdapter(new RollPagerAdapter(rollPagerView));
            rollPagerView.setOnItemClickListener(new com.jude.rollviewpager.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if (listener != null) {
                        listener.onSelectRollView(position);
                    }
                }
            });

        }
    }

    //热门曲谱的holder
    class RecommendViewHolder extends BaseViewHolder<Song> {
        TextView nameTv;
        TextView artistTv;
        LinearLayout contentLl;

        public RecommendViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_recommend_list);
            nameTv = $(R.id.name_tv);
            artistTv = $(R.id.artist_tv);
            contentLl = $(R.id.recommend_content);
            contentLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onSelectRecommendSong(getAdapterPosition());
                    }
                }
            });

        }

        @Override
        public void setData(Song data) {
            super.setData(data);
            nameTv.setText(data.getName());
            if (data.getArtist() != null) {
                artistTv.setText(data.getArtist());
            } else {
                artistTv.setText("暂无");
            }
        }

    }
}
