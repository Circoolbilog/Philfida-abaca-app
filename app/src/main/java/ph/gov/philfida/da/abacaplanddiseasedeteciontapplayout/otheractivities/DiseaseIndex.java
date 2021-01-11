package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otheractivities;

import androidx.appcompat.app.AppCompatActivity;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.DiseaseIntexItem;

import android.os.Bundle;

import java.util.ArrayList;

public class DiseaseIndex extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_index);
        ArrayList<DiseaseIntexItem> item = new ArrayList<>();
        item.add(new DiseaseIntexItem(R.drawable.ic_m_disease_index,"Example1"));
        item.add(new DiseaseIntexItem(R.drawable.ic_m_disease_index,"Example2"));
        item.add(new DiseaseIntexItem(R.drawable.ic_m_disease_index,"Example3"));
    }
}