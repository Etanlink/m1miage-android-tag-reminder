package android.miage.m1.uga.edu.tagreminder.feature.accueil;

import android.content.Context;
import android.miage.m1.uga.edu.tagreminder.R;
import android.miage.m1.uga.edu.tagreminder.model.LigneTransport;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class LigneTransportAdapter extends RecyclerView.Adapter<LigneTransportAdapter.LigneTransportHolder> {

    Context context;
    List<LigneTransport> items;
    private RecyclerItemClickListener recyclerItemClickListener;

    public LigneTransportAdapter(Context context, List<LigneTransport> items, RecyclerItemClickListener recyclerItemClickListener) {
        this.context = context;
        this.items = items;
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    @NonNull
    @Override
    public LigneTransportHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.ligne_item, parent, false);
        return new LigneTransportHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LigneTransportHolder holder, final int position) {
        LigneTransport ligne = items.get(position);

        holder.shortNameTxt.setText(ligne.getShortName());
        holder.modeTxt.setText(ligne.getMode());
        holder.typeTxt.setText(ligne.getType());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerItemClickListener.onItemClick(items.get(position));
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


    public class LigneTransportHolder extends RecyclerView.ViewHolder {

        TextView shortNameTxt, modeTxt, typeTxt;

        public LigneTransportHolder(View view) {
            super(view);
            shortNameTxt = (TextView) view.findViewById(R.id.txt_ligne_shortName);
            modeTxt = (TextView) view.findViewById(R.id.txt_ligne_mode);
            typeTxt = (TextView) view.findViewById(R.id.txt_ligne_type);
        }
    }
}
