package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.DiseaseIndexAdapter;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.DiseaseIndexItem;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DataBaseHelper;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseDBModel;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseInfoSymptomsDbHelper;
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
    }

    private void populateList() {
        item.add(new DiseaseIndexItem(R.drawable.ic_m_disease_index, "Bract Mosaic"));
        item.add(new DiseaseIndexItem(R.drawable.ic_m_disease_index, "Bunchy Top"));
        item.add(new DiseaseIndexItem(R.drawable.ic_m_disease_index, "CMV"));
        item.add(new DiseaseIndexItem(R.drawable.ic_m_disease_index, "Gen Mosaic"));
        item.add(new DiseaseIndexItem(R.drawable.ic_m_disease_index, "SCMV"));
        buildRecyclerView();
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
                List<SymptomModel> everySymptom = dataBaseHelper.getSymptoms();
                String symptomName = everySymptom.get(position).getSymptomName();
                Intent diseaseInfo = new Intent(DiseaseIndex.this, DiseaseInfo.class);
                diseaseInfo.putExtra("position", position);
                diseaseInfo.putExtra("symptomName", symptomName);
                startActivity(diseaseInfo);
            }
        });
    }
}