package com.cac.youtubeurldetect.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cac.youtubeurldetect.viewholder.ItemListViewholder;
import com.commit451.youtubeextractor.VideoStream;
import io.reactivex.subjects.PublishSubject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ac.hsu on 2020/1/6.
 * Mail: ac.hsu@104.com.tw
 * Copyright (c) 2020 104 Corporation
 */
public class ItemListAdapter extends RecyclerView.Adapter<ItemListViewholder> {

    private List<VideoStream> itemList = Collections.synchronizedList(new ArrayList<>());

    private PublishSubject<VideoStream> itemClick = PublishSubject.create();
    private PublishSubject<VideoStream> itemLongClick = PublishSubject.create();

    public void setList(List<VideoStream> itemList) {
        this.itemList.clear();
        this.itemList.addAll(itemList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemListViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return ItemListViewholder.getInstance(viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemListViewholder itemListViewholder, int position) {
        VideoStream item = getItem(position);
        itemListViewholder.setPathName("Format: " + item.getFormat() + "_Resolution: " + item.getResolution());
        itemListViewholder.itemView.setOnClickListener(v -> getItemClick().onNext(item));
        itemListViewholder.itemView.setLongClickable(true);
        itemListViewholder.itemView.setOnLongClickListener(v -> {
            getItemLongClick().onNext(item);
            return false;
        });
    }

    private VideoStream getItem(int position) {
        return this.itemList.get(position);
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public PublishSubject<VideoStream> getItemClick() {
        return itemClick;
    }

    public PublishSubject<VideoStream> getItemLongClick() {
        return itemLongClick;
    }
}
