package hkucs.comp3330.gogocoach;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hkucs.comp3330.gogocoach.firebase.Classes;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements View.OnClickListener{

    private OnItemClickListener mOnItemClickListener = null;

    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    private Context mContext;
    private ArrayList<Classes> mData;

    public MyAdapter(Context context, ArrayList<Classes> classesData) {
        this.mContext = context;
        this.mData = classesData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_class_item, null); //parent, false);
        ViewHolder holder = new ViewHolder(view);

        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.rateTextView.setText("$" + mData.get(position).price);
        holder.venueTextView.setText("Location: " + mData.get(position).location);
        holder.dateTextView.setText("DateTime: "+ mData.get(position).time);
        holder.avatarImageView.setImageResource(R.drawable.testicon1);
        holder.coachTextView.setText(mData.get(position).className);
        holder.classTextView.setText(mData.get(position).name);
        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {

            //getTag can get position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView avatarImageView;
        public TextView coachTextView;
        public TextView rateTextView;
        public TextView classTextView;
        public TextView venueTextView;
        public TextView dateTextView;
        public TextView detailTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            avatarImageView = (ImageView) itemView.findViewById(R.id.avatarImageView);
            coachTextView = (TextView) itemView.findViewById(R.id.coachTextView);
            rateTextView = (TextView) itemView.findViewById(R.id.rateTextView);
            classTextView = (TextView) itemView.findViewById(R.id.classTextView);
            venueTextView = (TextView) itemView.findViewById(R.id.venueTextView);
            dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);
            detailTextView = (TextView) itemView.findViewById(R.id.detailTextView);
        }
    }
}