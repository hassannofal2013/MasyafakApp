package com.tirefiesama.masifk;


import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;


public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private String[] imageUrls;
    private int custom_position = 0;

    ViewPagerAdapter(Context context, String[] imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        return  imageUrls.length;
                //Integer.MAX_VALUE;
                //
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

if(!(TextUtils.isEmpty(String.valueOf(imageUrls))))

        if(custom_position==imageUrls.length)
            custom_position = 0;
        ImageView imageView = new ImageView(context);
        Picasso.get()
                .load(imageUrls[custom_position])
                .fit()
                .centerCrop()
                .into(imageView);
        container.addView(imageView);
        custom_position++;

        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}