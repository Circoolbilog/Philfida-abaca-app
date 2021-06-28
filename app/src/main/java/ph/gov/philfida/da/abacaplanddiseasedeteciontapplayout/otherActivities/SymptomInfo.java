package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;

public class SymptomInfo extends AppCompatActivity {
    TextView symptomName,symptomDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_info);
        assignIDs();
        getExtras();
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String sName = extras.getString("symptomName");
            symptomName.setText(sName);
        }
    }

    private void assignIDs() {
        symptomName = findViewById(R.id.symptomName);
        symptomDesc = findViewById(R.id.symptomDesc);

    }
}