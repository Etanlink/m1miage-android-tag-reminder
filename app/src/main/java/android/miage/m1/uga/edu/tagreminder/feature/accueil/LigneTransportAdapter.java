package android.miage.m1.uga.edu.tagreminder.feature.accueil;

import android.content.Context;
import android.graphics.Color;
import android.miage.m1.uga.edu.tagreminder.R;
import android.miage.m1.uga.edu.tagreminder.model.LigneTransport;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class LigneTransportAdapter extends RecyclerView.Adapter<LigneTransportAdapter.LigneTransportHolder> {

    Context context;
    List<LigneTransport> items;
    private LigneItemClickListener ligneItemClickListener;

    public LigneTransportAdapter(Context context, List<LigneTransport> items, LigneItemClickListener ligneItemClickListener) {
        this.context = context;
        this.items = items;
        this.ligneItemClickListener = ligneItemClickListener;
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
        final LigneTransport ligne = items.get(position);

        holder.shortNameTxt.setText(ligne.getShortName());

        if(ligne.getTextColor() != null){
            holder.shortNameTxt.setTextColor(Color.parseColor("#"+ligne.getTextColor()));
        }

        if(ligne.getColor() != null){
            holder.logoCard.setCardBackgroundColor(Color.parseColor("#"+ligne.getColor()));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ligneItemClickListener.onItemClick(ligne);
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

        TextView shortNameTxt;
        CardView logoCard;

        public LigneTransportHolder(View view) {
            super(view);
            shortNameTxt = (TextView) view.findViewById(R.id.txt_ligne_shortName);
            logoCard = (CardView) view.findViewById((R.id.crd_ligne));
        }
    }
}
