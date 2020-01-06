package com.cac.youtubeurldetect.viewholder;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cac.youtubeurldetect.R;
import com.cac.youtubeurldetect.databinding.ViewHolderListItemBinding;

/**
 * Created by ac.hsu on 2020/1/6.
 * Mail: ac.hsu@104.com.tw
 * Copyright (c) 2020 104 Corporation
 */
public class ItemListViewholder extends RecyclerView.ViewHolder {

    private ViewHolderListItemBinding binding;

    public ItemListViewholder(@NonNull View itemView) {
        super(itemView);
        this.binding = DataBindingUtil.bind(itemView);
    }

    public static ItemListViewholder getInstance(ViewGroup viewGroup) {
        return new ItemListViewholder(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.view_holder_list_item, viewGroup, false).getRoot());
    }

    public void setPathName(String urlName) {
        binding.urlNameTextView.setText(urlName);
    }
}
