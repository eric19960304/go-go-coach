package hkucs.comp3330.gogocoach;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import hkucs.comp3330.gogocoach.firebase.NewsFeed;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> implements View.OnClickListener{
    private NewsAdapter.OnItemClickListener mOnItemClickListener = null;

    public interface OnItemClickListener {
        void onItemClick(View view , NewsFeed c);
    }
    private Context mContext;
    private ArrayList<NewsFeed> mData;

    public NewsAdapter(Context context, ArrayList<NewsFeed> classesData) {
        this.mContext = context;
        this.mData = classesData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_news_item, null); //parent, false);
        ViewHolder holder = new ViewHolder(view);

        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.topicTextView.setText(mData.get(position).topic);
        holder.contentTextView.setText(mData.get(position).content);
        holder.dateTextView.setText(mData.get(position).time);

        //holder.coachTextView.setText(mData.get(position).className);
        //holder.classTextView.setText(mData.get(position).name);
       // holder.itemView.setTag(mData);
        final ViewHolder _holder = holder;
        (new Thread(new Runnable(){
            @Override
            public void run() {
                final Bitmap avatar = loadImageFromNetwork(mData.get(position).photoUrl);
                _holder.avatarImageView.post(new Runnable(){
                    @Override
                    public void run() {
                        _holder.avatarImageView.setImageBitmap(avatar);
                    }
                });
            }
        })).start();
        final int _position = position;
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, mData.get(_position));
            }
        });
    }

    public Bitmap loadImageFromNetwork(String url) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream
                    ((InputStream) new URL(url).getContent());
            return bitmap;
        } catch(
                Exception e)

        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (NewsFeed) v.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView avatarImageView;
        public TextView topicTextView;
        public TextView contentTextView;
        public TextView dateTextView;
        public LinearLayout rootLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            avatarImageView = (ImageView) itemView.findViewById(R.id.avatarImageView);
            topicTextView = (TextView) itemView.findViewById(R.id.topicTextView);
            contentTextView = (TextView) itemView.findViewById(R.id.contentTextView);
            dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);
            rootLayout = (LinearLayout) itemView.findViewById(R.id.rootLayout);
        }
    }




}
