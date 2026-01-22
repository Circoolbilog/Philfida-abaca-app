package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.database.CaptureDao;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.database.CaptureRecord;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.database.DatabaseHelper;

public class DatabaseViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_viewer);

        TextView databaseContent = findViewById(R.id.databaseContent);
        
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        CaptureDao dao = new CaptureDao(dbHelper.getReadableDatabase());
        List<CaptureRecord> records = dao.getAllCaptures();

        if (records.isEmpty()) {
            databaseContent.setText("No records found in database.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (CaptureRecord record : records) {
                sb.append("ID: ").append(record.getId()).append("\n");
                sb.append("Image: ").append(record.getImageName()).append("\n");
                sb.append("Symptoms: ").append(record.getSymptomsDetected()).append("\n");
                sb.append("Confidence: ").append(record.getConfidenceScores()).append("\n");
                sb.append("Results: ").append(record.getDetectionResults()).append("\n");
                sb.append("Location: ").append(record.getLatitude()).append(", ").append(record.getLongitude()).append("\n");
                sb.append("Timestamp: ").append(record.getTimestamp()).append("\n");
                sb.append("----------------------------\n\n");
            }
            databaseContent.setText(sb.toString());
        }
    }
}