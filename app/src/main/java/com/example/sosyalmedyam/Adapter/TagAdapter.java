package com.example.sosyalmedyam.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sosyalmedyam.R;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder>{

    private Context mContext;
    private List<String> mTags;
    private List<String> mTagsCount;

    public TagAdapter(Context mContext, List<String> mTags, List<String> mTagsCount) {
        this.mContext = mContext;
        this.mTags = mTags;
        this.mTagsCount = mTagsCount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //* bir "ViewHolder" sınıfının yaratılması için "onCreateViewHolder" metodunun içerisinde yer almaktadır
        View view = LayoutInflater.from(mContext).inflate(R.layout.tag_item , parent , false);

        return new TagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //*Bu metod, her bir "ViewHolder" nesnesi için çağrılır
        // ve bu nesnenin görüntülenmesi için gerekli olan verilerin güncellenmesini sağlar.

        holder.tag.setText("# " + mTags.get(position)); //* holder tutma görevi
        holder.noOfPosts.setText(mTagsCount.get(position) + " posts");

    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        //*"RecyclerView" için kullanılan bir adaptör sınıfının içerisinde yer alan "ViewHolder" sınıfının tanımını içermektedir

        public TextView tag;
        public TextView noOfPosts;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tag = itemView.findViewById(R.id.hash_tag);
            noOfPosts = itemView.findViewById(R.id.no_of_posts);
        }
    }

    public void filter (List<String> filterTags , List<String> filterTagsCount) {
        //*Bu metod, adaptörün kullandığı verileri filtrelemek için kullanılır.
        this.mTags = filterTags;
        this.mTagsCount = filterTagsCount;

        notifyDataSetChanged();
    }

}
