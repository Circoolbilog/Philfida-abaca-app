package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.DiseaseIndexAdapter;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.DiseaseIndexItem;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseDBModel;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseSymptomsDbHelper;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class DiseaseIndex extends AppCompatActivity {
    RecyclerView recyclerView;
    DiseaseIndexAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<DiseaseIndexItem> item;
    private static final String TAG = "DiseaseIndex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_index);
        item = new ArrayList<>();
        populateList();
        DiseaseSymptomsDbHelper dbHelper = new DiseaseSymptomsDbHelper(this);
        List<DiseaseDBModel> dbModelList = dbHelper.getDiseases();
    }

    private void populateList() {
        item.add(new DiseaseIndexItem(R.drawable.ic_m_disease_index, "No Allocation"));
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
        adapter.setOnItemClickListener(position -> {
//                open new activity displaying disease info on clicked item
            String diseaseName;
            switch (position){
                case 0:
                    diseaseName = "0_No_Allocation";
                    break;
                case 1:
                    diseaseName = "Bract_Mosaic";
                    break;
                case 2:
                    diseaseName = "Bunchy_Top";
                    break;
                case 3:
                    diseaseName = "CMV";
                    break;
                case 4:
                    diseaseName = "Gen_Mosaic";
                    break;
                case 5:
                    diseaseName = "SCMV";
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + position);
            }
            Intent diseaseInfo = new Intent(DiseaseIndex.this, DiseaseInfo.class);
            diseaseInfo.putExtra("position", position);
            diseaseInfo.putExtra("diseaseName", diseaseName);
            startActivity(diseaseInfo);
        });
    }
}