package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;

public class SymptomAdapter extends RecyclerView.Adapter<DiseaseIndexAdapter.DiseaseIndexViewHolder> {
    private ArrayList<SymptomItem> symptomItems;
    private DiseaseIndexAdapter.OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(DiseaseIndexAdapter.OnItemClickListener listener1){
        listener = listener1;
    }
    @NonNull
    @Override
    public DiseaseIndexAdapter.DiseaseIndexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.symptom_index_item,parent,false);
        DiseaseIndexAdapter.DiseaseIndexViewHolder viewHolder = new DiseaseIndexAdapter.DiseaseIndexViewHolder(view , listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DiseaseIndexAdapter.DiseaseIndexViewHolder holder, int position) {
        SymptomItem currentItem = symptomItems.get(position);
        holder.textView.setText(currentItem.getDiseaseName());
    }

    @Override
    public int getItemCount() {
        return symptomItems.size();
    }

    public static class DiseaseIndexViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textView;
        public DiseaseIndexViewHolder(@NonNull View itemView, DiseaseIndexAdapter.OnItemClickListener itemClickListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.diseaseImage);
            textView = itemView.findViewById(R.id.diseaseName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener !=null){
                        int position = getAdapterPosition();
                        if (position!= RecyclerView.NO_POSITION){
                            itemClickListener.onItemClick(position);
                        }
                    }
                }
            });
        }

    }
    public SymptomAdapter(ArrayList<SymptomItem> symptomItemArrayList){
        symptomItems = symptomItemArrayList;
    }
}
