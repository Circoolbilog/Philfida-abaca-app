package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import androidx.appcompat.app.AppCompatActivity;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DataBaseHelper;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.SymptomModel;

import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class DiseaseInfo extends AppCompatActivity {
    TextView diseaseName,diseaseDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_info);
        assignIds();
        loadDiseaseInfo();
    }

    private void assignIds() {
        diseaseDesc = findViewById(R.id.diseaseDesc);
        diseaseName = findViewById(R.id.diseaseNameInd);
    }

    private void loadDiseaseInfo() {
        Bundle display = getIntent().getExtras();
        int position = display.getInt("position");
        String symptomName = display.getString("symptomName");
        diseaseName.append(" "+position);
        diseaseName.setText(symptomName);
    }
}