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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;


//Simple Adapter for generalized purposes
public class SimpleArrayAdapter extends RecyclerView.Adapter<DiseaseIndexAdapter.DiseaseIndexViewHolder> {
    private final ArrayList<SimpleItem> simpleItems;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_index_item,parent,false);
        return new DiseaseIndexAdapter.DiseaseIndexViewHolder(view , listener);
    }

    @Override
    public void onBindViewHolder(@NonNull DiseaseIndexAdapter.DiseaseIndexViewHolder holder, int position) {
        SimpleItem currentItem = simpleItems.get(position);
        holder.textView.setText(currentItem.getItemName());
    }

    @Override
    public int getItemCount() {
        return simpleItems.size();
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
    public SimpleArrayAdapter(ArrayList<SimpleItem> simpleItemArrayList){
        simpleItems = simpleItemArrayList;
    }
}
