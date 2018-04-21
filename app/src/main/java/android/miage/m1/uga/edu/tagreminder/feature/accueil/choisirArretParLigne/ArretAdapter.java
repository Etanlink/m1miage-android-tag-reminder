package android.miage.m1.uga.edu.tagreminder.feature.accueil.choisirArretParLigne;

import android.content.Context;
import android.miage.m1.uga.edu.tagreminder.R;
import android.miage.m1.uga.edu.tagreminder.model.Arret;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class ArretAdapter extends RecyclerView.Adapter<ArretAdapter.ArretHolder> {

    Context context;
    List<Arret> items;
    private ArretItemClickListener arretItemClickListener;

    public ArretAdapter(Context context, List<Arret> items, ArretItemClickListener arretItemClickListener) {
        this.context = context;
        this.items = items;
        this.arretItemClickListener = arretItemClickListener;
    }

    @NonNull
    @Override
    public ArretAdapter.ArretHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.arret_item, parent, false);
        return new ArretHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArretHolder holder, final int position) {
        final Arret arret = items.get(position);

        holder.txt_stop_name.setText(arret.getName());
        holder.txt_stop_city.setText(arret.getCity());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arretItemClickListener.onItemClick(arret);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ArretHolder extends RecyclerView.ViewHolder {

        TextView txt_stop_name, txt_stop_city;

        public ArretHolder(View view) {
            super(view);
            txt_stop_name = (TextView) view.findViewById(R.id.txt_stop_name);
            txt_stop_city = (TextView) view.findViewById(R.id.txt_stop_city);
        }
    }
}
