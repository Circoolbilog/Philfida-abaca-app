package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.SimpleArrayAdapter;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.SimpleItem;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AboutApp extends AppCompatActivity {
    private static final String TAG = "AboutApp";
    LinearLayout appVersion, tutorial, developer,reportBug;
    TextView appVersionVal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        getSupportActionBar().setTitle("About App");
        assignIDs();
    }

    private void assignIDs() {
        appVersion = findViewById(R.id.AppVersion);
        tutorial = findViewById(R.id.guide);
        developer = findViewById(R.id.Developer);
        reportBug = findViewById(R.id.reportBug);
        appVersionVal = findViewById(R.id.AppVersionVal);
        appVersionVal.setText(getAppVersion());
    }


    private String getAppVersion() {
        String version;
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = e.getMessage();
        }
        return version;
    }

}