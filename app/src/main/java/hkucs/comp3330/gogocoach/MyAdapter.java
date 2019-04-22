package hkucs.comp3330.gogocoach;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private Context mContext;
    private String[] mData;

    public MyAdapter(Context context, String[] data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_class_item, null); //parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.rateTextView.setText(mData[position]);
        holder.venueTextView.setText("Location: TBC");
        holder.dateTextView.setText("Date: TBC");
        if (position == 1){
            holder.avatarImageView.setImageResource(R.drawable.testicon1);
            holder.coachTextView.setText("Coach 1");
            holder.classTextView.setText("Badminton class");
        }else if (position == 2){
            holder.avatarImageView.setImageResource(R.drawable.testicon2);
            holder.coachTextView.setText("Coach2");
            holder.classTextView.setText("Golf class");
        }else{
            holder.avatarImageView.setImageResource(R.drawable.testicon2);
            holder.coachTextView.setText("Coach2");
            holder.classTextView.setText("Golf class2");
        }

    }

    @Override
    public int getItemCount() {
        return mData.length;
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