package com.hackathon.pebbles.hackathonapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.hackathon.pebbles.hackathonapp.R;
import com.hackathon.pebbles.hackathonapp.models.Venue;

import java.util.ArrayList;

/**
 * Created by @priyankvex on 5/3/16.
 */
public class TopPlacesListAdapter extends ArrayAdapter<Venue>{

    private Context mContext;
    private ArrayList<Venue> mVenues;
    private LayoutInflater inflater;

    public TopPlacesListAdapter(Context context, ArrayList<Venue> venues){
        super(context, 0, venues);
        mContext = context;
        mVenues = venues;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Venue venue = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item_top_places, parent, false);
            viewHolder.textViewName = (TextView) convertView.findViewById(R.id.textViewVenueName2);
            viewHolder.textViewAddress = (TextView) convertView.findViewById(R.id.textViewAddress2);
            viewHolder.textViewCategory = (TextView) convertView.findViewById(R.id.textViewCategory2);
            viewHolder.textViewCheckIns = (TextView) convertView.findViewById(R.id.textViewRating2);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageViewVenue2);
            MaterialRippleLayout.on(viewHolder.imageView)
                    .rippleColor(Color.WHITE)
                    .rippleHover(true)
                    .rippleAlpha(0.2f)
                    .rippleOverlay(true)
                    .create();
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textViewName.setText(venue.getName());
        viewHolder.textViewAddress.setText(venue.getAddress());
        viewHolder.textViewCategory.setText(venue.getCategory());
        viewHolder.textViewCheckIns.setText(venue.getCheckIns());

        return convertView;
    }

    private class ViewHolder{
        TextView textViewName;
        TextView textViewAddress;
        TextView textViewCategory;
        TextView textViewCheckIns;
        ImageView imageView;
    }

}
