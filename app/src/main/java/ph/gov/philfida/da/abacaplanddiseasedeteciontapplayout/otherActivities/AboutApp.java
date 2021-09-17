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
import android.widget.Toast;

import java.util.ArrayList;

public class AboutApp extends AppCompatActivity {
    private static final String TAG = "AboutApp";
    RecyclerView recyclerView;
    SimpleArrayAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<SimpleItem> item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        setupSettingsItems();
        getSupportActionBar().setTitle("About App");
        //TODO: App version
        //TODO: other info
        
    }

    private void setupSettingsItems() {
        item = new ArrayList<>();
        item.add(new SimpleItem("App version : " + getAppVersion()));
        item.add(new SimpleItem("Developer"));
        item.add(new SimpleItem("Report Bug"));
        setupRecyclerView();
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

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.settingsItems);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new SimpleArrayAdapter(item);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {
            Toast.makeText(AboutApp.this, "item", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onItemClick: asdasd");
        });

    }
}