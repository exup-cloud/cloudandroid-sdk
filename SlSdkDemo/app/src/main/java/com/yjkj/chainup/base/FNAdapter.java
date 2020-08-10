package com.yjkj.chainup.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjkj.chainup.R;

import java.util.List;


public class FNAdapter<T> extends RecyclerView.Adapter<FNAdapter.MyViewHolder> {
    public static final int TYPE_LOAD_MORE = 20;

    private ViewProvider mViewProvider;
    private boolean isLoadMore;  //是否加载更多
    private boolean mLoadMore;   //标记记载状态
    private Context mContext;
    private List<T> mList;

    public void setViewProvider(ViewProvider mViewProvider) {
        this.mViewProvider = mViewProvider;
    }

    public FNAdapter(Context context, List<T> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOAD_MORE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_footer, parent, false);
            return new MyViewHolder(view);
        }
        if (mViewProvider != null) {
            View view = mViewProvider.getView(parent, viewType);
            return new MyViewHolder(view);
        }

        View view = new View(mContext);
        return new MyViewHolder(view);
    }


    public void enableLoadMore(boolean isLoadMore) {
        this.isLoadMore = isLoadMore;
        notifyDataSetChanged();
    }


    //获取列表加载状态
    public boolean getLoadMoreStatus() {
        return mLoadMore;
    }

    //设置加载更多状态
    public void setLoadMoreStatus(boolean isLoadMore) {
        mLoadMore = isLoadMore;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (isLoadMore) {
            if (holder.getItemViewType() == TYPE_LOAD_MORE) {
                if (mLoadMore) {
                    return;
                }
                mViewProvider.loadMore();
                mLoadMore = true;
                return;
            }
        }

        if (mViewProvider != null) {
            mViewProvider.bindDataToView(holder, position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position != mList.size())
                        mViewProvider.onItemClick(holder, holder.getAdapterPosition());
                    else
                        return;
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoadMore) {
            if (position == getItemCount() - 1) {
                return 20;
            }
        }
        if (mViewProvider != null) {
            return mViewProvider.getItemViewType(position);
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (mViewProvider != null) {
            return mViewProvider.getItemCount();
        }else {
            return 0;
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private SparseArray<View> mViews = new SparseArray<>();

        public MyViewHolder(View itemView) {
            super(itemView);
        }

        @SuppressWarnings("unchecked")
        public <T extends View> T getView(int resId) {
            T t = (T) mViews.get(resId);
            if (t == null) {
                t = itemView.findViewById(resId);
                if (t != null) {
                    mViews.put(resId, t);
                }
            }
            return t;
        }

        public void setText(int resId, CharSequence text) {
            TextView textView = itemView.findViewById(resId);
            textView.setText(text);
        }
    }

    interface ViewProvider {
        View getView(ViewGroup parent, int viewType);

        void bindDataToView(MyViewHolder holder, int position);

        int getItemViewType(int position);

        int getItemCount();

        void onItemClick(MyViewHolder holder, int position);

        void loadMore();
    }
}
