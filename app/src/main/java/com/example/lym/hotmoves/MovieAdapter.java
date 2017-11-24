package com.example.lym.hotmoves;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.lym.hotmoves.bean.MovieBean;
import com.example.lym.hotmoves.util.NetworkUtils;

import java.util.ArrayList;

/**
 * @Description：
 * @author：Bux on 2017/11/23 15:38
 * @email: 471025316@qq.com
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {
    private static final String TAG = "MovieAdapter";

    //图片宽度 =屏幕宽度/spancount
    private int mWidth;


    private ArrayList<MovieBean> mList;
    private OnMovieItemClickListener mListener;

    public MovieAdapter(OnMovieItemClickListener listener,int width) {
        this.mListener = listener;
        this.mWidth=width;
    }

    public void setList(ArrayList<MovieBean> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void addList(ArrayList<MovieBean> list){
        if(list!=null&list.size()>0){
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public MovieAdapter.MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mian_moves_item_layout, parent, false);
        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    public interface OnMovieItemClickListener {
        /**
         * 点击事件
         * @param movieBean
         */
        void movieItemClick(MovieBean movieBean);
    }

    class MovieHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        MovieBean mMovieBean;

        public MovieHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_poster);

            ViewGroup.LayoutParams params=mImageView.getLayoutParams();
            params.width=mWidth;
            params.height= (int) (mWidth*1.5);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.movieItemClick(mMovieBean);
                    }
                }
            });
        }

        public void bind(MovieBean movieBean) {
            mMovieBean = movieBean;
            Glide.with(itemView.getContext())
                    .load(NetworkUtils.getImagePath(movieBean.getPoster_path()))
                    .apply(new RequestOptions().placeholder(R.mipmap.ic_launcher))
                    .into(mImageView);
        }
    }
}
