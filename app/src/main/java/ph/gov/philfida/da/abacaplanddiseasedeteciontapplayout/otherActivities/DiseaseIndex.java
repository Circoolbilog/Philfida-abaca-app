package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.DiseaseIndexAdapter;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.DiseaseIndexItem;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DataBaseHelper;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.SymptomModel;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class DiseaseIndex extends AppCompatActivity {
    RecyclerView recyclerView;
    DiseaseIndexAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<DiseaseIndexItem> item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_index);
        item = new ArrayList<>();
        populateList();
       /* item.add(new DiseaseIndexItem(R.drawable.ic_m_disease_index,"Disease0"));
        item.add(new DiseaseIndexItem(R.drawable.ic_m_disease_index,"Disease1"));
        item.add(new DiseaseIndexItem(R.drawable.ic_m_disease_index,"Disease2"));
        item.add(new DiseaseIndexItem(R.drawable.ic_m_disease_index,"Disease3"));
        item.add(new DiseaseIndexItem(R.drawable.ic_m_disease_index,"Disease4"));
        item.add(new DiseaseIndexItem(R.drawable.ic_m_disease_index,"Disease5"));*/
    }

    private void populateList() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        List<SymptomModel> everSymptom = dataBaseHelper.getSymptomList();
        if (everSymptom != null) {
            for (SymptomModel symptom : everSymptom) {
//                dataBaseHelper.clear(symptom);
//                   Clear table
                String symptomName = symptom.getSymptomName();
                item.add(new DiseaseIndexItem(R.drawable.ic_m_disease_index, symptomName));
            }
            buildRecyclerView();
        }


    }

    private void buildRecyclerView() {
        recyclerView = findViewById(R.id.diseaseIndexRV);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new DiseaseIndexAdapter(item);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new DiseaseIndexAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                open new activity displaying disease info on clicked item

                DataBaseHelper dataBaseHelper = new DataBaseHelper(getApplicationContext());
                List<SymptomModel> everySymptom = dataBaseHelper.getSymptomList();
                String symptomName = everySymptom.get(position).getSymptomName();
                Intent diseaseInfo = new Intent(DiseaseIndex.this, DiseaseInfo.class);
                diseaseInfo.putExtra("position", position);
                diseaseInfo.putExtra("symptomName", symptomName);
                startActivity(diseaseInfo);
            }
        });
    }
}