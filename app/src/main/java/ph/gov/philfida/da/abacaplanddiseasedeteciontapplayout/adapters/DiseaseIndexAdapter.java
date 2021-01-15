package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;

public class DiseaseIndexAdapter extends RecyclerView.Adapter<DiseaseIndexAdapter.DiseaseIndexViewHolder> {
    private ArrayList<DiseaseIndexItem> diseaseIndexItems;
    @NonNull
    @Override
    public DiseaseIndexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.disease_index_item,parent,false);
        DiseaseIndexViewHolder viewHolder = new DiseaseIndexViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DiseaseIndexViewHolder holder, int position) {
        DiseaseIndexItem currentItem = diseaseIndexItems.get(position);
        holder.imageView.setImageResource(currentItem.getDiseaseImageImageResource());
        holder.textView.setText(currentItem.getDiseaseName());
    }

    @Override
    public int getItemCount() {
        return diseaseIndexItems.size();
    }

    public static class DiseaseIndexViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textView;
        public DiseaseIndexViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.diseaseImage);
            textView = itemView.findViewById(R.id.diseaseName);
        }

    }
    public DiseaseIndexAdapter(ArrayList<DiseaseIndexItem> diseaseIndexItemList){
        diseaseIndexItems = diseaseIndexItemList;
    }
}
