package com.veryworks.android.myutility;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "MyItemRecycler";
    private final Context context;
    private List<String> datas;

    public MyItemRecyclerViewAdapter(Context context, List<String> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.imageUri = datas.get(position);
//        holder.imageView.setImageURI(holder.imageUri); // 이미지 사이즈 때문에 터짐
        Glide.with(context)
                .load(holder.imageUri)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public String imageUri;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.id);
            imageUri = null;

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 클릭시 큰이미지 보여주기
//                    Intent intent = new Intent(context,DetailActivity.class);
//                    context.startActivity(intent);
                }
            });
        }
    }
}
