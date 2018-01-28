package com.prestonbui.glancesocial.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.prestonbui.glancesocial.R;
import com.prestonbui.glancesocial.models.Person;
import com.prestonbui.glancesocial.models.Post;
import com.prestonbui.glancesocial.utils.UniversalImageLoader;

import java.util.ArrayList;

/**
 * Created by phuongbui on 11/13/17.
 */

public class PersonListAdapter extends ArrayAdapter<Post> {

    private static final String TAG = "PersonListAdapter";

    private Context mContext;
    private int mResource;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView name;
        TextView birthday;
        TextView sex;
        ImageView image;
    }

    /**
     * Default constructor for the PersonListAdapter
     *
     * @param context
     * @param resource
     * @param objects
     */
    public PersonListAdapter(Context context, int resource, ArrayList<Post> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the persons information
        String birthday = getItem(position).getCaption();
        String imgUrl = getItem(position).getImage_path();

        //ViewHolder object
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.birthday = (TextView) convertView.findViewById(R.id.textView2);
            holder.image = (ImageView) convertView.findViewById(R.id.image);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.birthday.setText(birthday);

        //download and display image from url
        if(imgUrl != null) {
            UniversalImageLoader.setImage(imgUrl, holder.image, null, "");
        }

        return convertView;
    }

    /**
     * Required for setting up the Universal Image loader Library
     */

}


