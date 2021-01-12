package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otheractivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.DiseaseIndexAdapter;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.DiseaseIndexItem;

import android.os.Bundle;

import java.util.ArrayList;

public class DiseaseIndex extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_index);
        ArrayList<DiseaseIndexItem> item = new ArrayList<>();
        item.add(new DiseaseIndexItem(R.drawable.ic_m_disease_index,"Example1"));
        item.add(new DiseaseIndexItem(R.drawable.ic_m_disease_index,"Example2"));
        item.add(new DiseaseIndexItem(R.drawable.ic_m_disease_index,"Example3"));

        recyclerView = findViewById(R.id.diseaseIndexRV);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new DiseaseIndexAdapter(item);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}