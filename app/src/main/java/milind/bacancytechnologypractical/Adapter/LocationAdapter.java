package milind.bacancytechnologypractical.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import milind.bacancytechnologypractical.ModelClasses.LatLng;
import milind.bacancytechnologypractical.R;


public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private Context context;
    private ArrayList<LatLng> locations = new ArrayList<>();

    public LocationAdapter(Context context, ArrayList<LatLng> locations) {
        super();
        this.context = context;
        this.locations = locations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.locationitem_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.latitudeValue.setText("Latitude :" + locations.get(i).getLatitude());
        viewHolder.longitudeValue.setText("Longitude :" + locations.get(i).getLongitude());

    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView latitudeValue, longitudeValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            latitudeValue = itemView.findViewById(R.id.latitudeValue);
            longitudeValue = itemView.findViewById(R.id.longitudeValue);
        }
    }

    public void notifyDatasetChange(){

        notifyDataSetChanged();
    }
}
