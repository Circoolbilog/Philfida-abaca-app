package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.DiseaseIndexAdapter;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.DiseaseIndexItem;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.SymptomAdapter;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.SymptomItem;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DataBaseHelper;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseDBModel;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseInfoSymptomsDbHelper;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.SymptomModel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DiseaseInfo extends AppCompatActivity {
    TextView diseaseName,diseaseDesc;
    RecyclerView recyclerView;
    SymptomAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<SymptomItem> item;
    int position;
    String mDiseaseName = "", mDiseaseDesc;
    private static final String TAG = "DiseaseInfo";

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
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mDiseaseName = extras.getString("diseaseName");
            position = extras.getInt("position");
        }
        diseaseName.setText(mDiseaseName);
    }
    private void populateList() {
        DiseaseInfoSymptomsDbHelper dbHelper = new DiseaseInfoSymptomsDbHelper(this);
        List<DiseaseDBModel> dbModelList = dbHelper.getDiseases();
        Log.d(TAG, "populateList: " + dbModelList.size());
//        ArrayAdapter symptomsArrayAdapter = new ArrayAdapter<DiseaseDBModel>(this, android.R.layout.simple_list_item_1, dbModelList);
        item = new ArrayList<SymptomItem>();
        for (DiseaseDBModel symptom:dbModelList){

            Log.d(TAG, "populateList: "+symptom.getNo_Allocation());
            switch (mDiseaseName){
                case "0_No_Allocation":
                    if (symptom.getNo_Allocation() == null)break;
                    if(symptom.getNo_Allocation().equals("NULL")|symptom.getNo_Allocation().equals(""))break;
                    item.add(new SymptomItem(symptom.getNo_Allocation()));
                    break;
                case "Bract_Mosaic":
                    if(symptom.getBract_Mosaic().equals("NULL")|symptom.getBract_Mosaic().equals(""))break;
                    item.add(new SymptomItem(symptom.getBract_Mosaic()));
                    break;
                case "Bunchy_Top":
                    if(symptom.getBunchy_Top().equals("NULL")|symptom.getBunchy_Top().equals(""))break;
                    item.add(new SymptomItem(symptom.getBunchy_Top()));
                    break;
                case "CMV":
                    if(symptom.getCMV().equals("NULL")|symptom.getCMV().equals(""))break;
                    item.add(new SymptomItem(symptom.getCMV()));
                    break;
                case  "Gen_Mosaic":
                    if(symptom.getGen_Mosaic().equals("NULL")|symptom.getGen_Mosaic().equals(""))break;
                    item.add(new SymptomItem(symptom.getGen_Mosaic()));
                    break;
                case "SCMV":
                    if(symptom.getSCMV().equals("NULL")|symptom.getSCMV().equals(""))break;
                    item.add(new SymptomItem(symptom.getSCMV()));
                    break;
            }

        };
        buildRecyclerView();
    }
    private void buildRecyclerView() {
        recyclerView = findViewById(R.id.symptomList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new SymptomAdapter(item);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new DiseaseIndexAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                open new activity displaying disease info on clicked item
//                DataBaseHelper dataBaseHelper = new DataBaseHelper(getApplicationContext());
//                List<SymptomModel> everySymptom = dataBaseHelper.getSymptoms();
//                String symptomName = everySymptom.get(position).getSymptomName();
                Intent symptomInfo = new Intent(DiseaseInfo.this, SymptomInfo.class);
                symptomInfo.putExtra("position", position);
                symptomInfo.putExtra("symptomName", item.get(position).getDiseaseName());
                startActivity(symptomInfo);
                Toast.makeText(DiseaseInfo.this, "item at pos: " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadDiseaseInfo() {
        Bundle display = getIntent().getExtras();
        int position = display.getInt("position");
        String diseaseName = display.getString("diseaseName");
        this.diseaseName.append(" "+position);
        this.diseaseName.setText(diseaseName);
        this.diseaseDesc.setText(getmDiseaseDesc(diseaseName));
        populateList();
    }

    private String getmDiseaseDesc(String mDiseaseName){
        String diseaseDesc = "";
        switch (mDiseaseName){
            case "0_No_Allocation":
                diseaseDesc = "Sypmtoms on this list has not been attributed to any disease in the database";
                break;
            case "Bract_Mosaic":
                diseaseDesc = "(Placeholder) The virus is said to be a significant pathogen of the abaca, along with the Abaca bunchy top virus.[1] There is not much known about this virus: as of January 2021, only five results are returned on Google Scholar for the query \"Abaca bract mosaic virus\" OR \"Abacá bract mosaic virus\". ";
                break;
            case "Bunchy_Top":
                diseaseDesc = "(Placeholder)  ABTV has been isolated from both abacá (Musa textilis) and banana (Musa sp.).[1] ABTV has many similarities to banana bunchy top virus (BBTV) but is both genetically and serologically distinct in that it lacks two open reading frames found in BBTV's genome. ATBV's genome contains six circular components, each of which are 1,000-1,500 base pairs in length.";
                break;
            case "CMV":
                diseaseDesc = "Description pending";
                break;
            case "Gen_Mosaic":
                diseaseDesc = "(Placeholder) Abacá mosaic virus (AbaMV) is related to members of the sugarcane mosaic virus subgroup of the genus Potyvirus. The ~2 kb 3′ terminal region of the viral genome was sequenced and, in all areas analysed, found to be most similar to Sugarcane mosaic virus (SCMV) and distinct from Johnsongrass mosaic virus (JGMV), Maize dwarf mosaic virus (MDMV) and Sorghum mosaic virus (SrMV). ";
                break;
            case "SCMV":
                diseaseDesc = "Description pending ";
                break;
        }
        return  diseaseDesc;
    }

}