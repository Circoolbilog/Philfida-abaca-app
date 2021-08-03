package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.DiseaseIndexAdapter;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.SimpleArrayAdapter;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.SimpleItem;

import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    SimpleArrayAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<SimpleItem> item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupRecyclerView();
        setupSettingsItems();


        //TODO: Add default diagnose mode
        //TODO: Add language (English/Filipino)
        //TODO: Add Change Camera
        //TODO: Add Dark Mode
        //TODO: Add update and notif settings
        //TODO: Weather
        //TODO: Other
        //TODO: About App(Might merge about app with settings
    }

    private void setupSettingsItems() {
        item = new ArrayList<SimpleItem>();
        item.add(new SimpleItem("Capture Mode"));
        item.add(new SimpleItem("User Account"));
        item.add(new SimpleItem("Location Settings"));
        setupRecyclerView();
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