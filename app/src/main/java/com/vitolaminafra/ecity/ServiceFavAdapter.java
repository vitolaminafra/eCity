package com.vitolaminafra.ecity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServiceFavAdapter extends RecyclerView.Adapter<ServiceFavAdapter.ViewHolder> {

    private List<ServiceView> list;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        TextView address, sub;
        ImageView icon;
        String lid, lat, lng;
        ServiceView mItem;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            address = itemView.findViewById(R.id.addressText);
            sub = itemView.findViewById(R.id.subText);
            icon = itemView.findViewById(R.id.serviceImg);

        }

        public void setItem(ServiceView item) {
            mItem = item;

            lid = item.getLid();
            lat = item.getLat();
            lng = item.getLng();

            if(item.getBike()) {
                icon.setImageResource(R.drawable.bikeicon);
            } else {
                icon.setImageResource(R.drawable.coffee);
            }
            address.setText(item.getAdd());
            sub.setText(item.getSub());
        }

        @Override
        public void onClick(View view) {
            if(mItem.getBike()){
                Intent serviceIntent = new Intent(context, ServiceActivity.class);
                serviceIntent.addCategory("bike");
                serviceIntent.putExtra("lat", lat);
                serviceIntent.putExtra("lng", lng);
                serviceIntent.putExtra("lid", lid);
                context.startActivity(serviceIntent);
            } else {
                Intent serviceIntent = new Intent(context, ServiceActivity.class);
                serviceIntent.putExtra("lat", lat);
                serviceIntent.putExtra("lng", lng);
                serviceIntent.putExtra("lid", lid);
                context.startActivity(serviceIntent);
            }
        }
    }


    public ServiceFavAdapter(List<ServiceView> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_view, parent, false);

        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setItem(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
