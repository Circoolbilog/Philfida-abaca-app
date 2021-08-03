package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.DiseaseIndexAdapter;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.SimpleArrayAdapter;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.SimpleItem;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.util.ArrayList;

public class AboutApp extends AppCompatActivity {
    RecyclerView recyclerView;
    SimpleArrayAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<SimpleItem> item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        setupSettingsItems();
        //TODO: App version
        //TODO: other info
        
    }

    private void setupSettingsItems() {
        item = new ArrayList<SimpleItem>();
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
        adapter.setOnItemClickListener(new DiseaseIndexAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });

    }
}