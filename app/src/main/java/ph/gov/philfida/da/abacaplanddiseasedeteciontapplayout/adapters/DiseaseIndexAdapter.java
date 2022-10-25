/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    private final ArrayList<DiseaseIndexItem> diseaseIndexItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    //attach itemClick listener
    public void setOnItemClickListener(OnItemClickListener listener1){
        listener = listener1;
    }

    @NonNull
    @Override
    public DiseaseIndexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.disease_index_item,parent,false);
        return new DiseaseIndexViewHolder(view , listener);
    }

    @Override
    public void onBindViewHolder(@NonNull DiseaseIndexViewHolder holder, int position) {
        DiseaseIndexItem currentItem = diseaseIndexItems.get(position);
        holder.imageView.setImageResource(currentItem.getDiseaseImageImageResource());
        holder.textView.setText(currentItem.getDiseaseName());
    }

    // Return the number of items in disease index arraylist
    @Override
    public int getItemCount() {
        return diseaseIndexItems.size();
    }

    public static class DiseaseIndexViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textView;

        //ViewHolder. set the view for disease index items.
        public DiseaseIndexViewHolder(@NonNull View itemView,OnItemClickListener itemClickListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.diseaseImage);
            textView = itemView.findViewById(R.id.diseaseName);
            itemView.setOnClickListener(v -> {
                if (itemClickListener !=null){
                    int position = getAdapterPosition();
                    if (position!= RecyclerView.NO_POSITION){
                        itemClickListener.onItemClick(position);
                    }
                }
            });
        }

    }

    //Adapter Constructor
    public DiseaseIndexAdapter(ArrayList<DiseaseIndexItem> diseaseIndexItemList){
        diseaseIndexItems = diseaseIndexItemList;
    }

}
