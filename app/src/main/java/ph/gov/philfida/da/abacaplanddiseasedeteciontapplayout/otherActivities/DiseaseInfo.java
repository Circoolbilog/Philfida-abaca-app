package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.DiseaseIndexAdapter;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.SimpleArrayAdapter;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.SimpleItem;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseDBModel;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseSymptomsDbHelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class DiseaseInfo extends AppCompatActivity {
    TextView diseaseName, diseaseDesc;
    RecyclerView recyclerView;
    SimpleArrayAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<SimpleItem> item;
    ImageView diseasePic;
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
        diseasePic = findViewById(R.id.diseaseImageInd);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mDiseaseName = extras.getString("diseaseName");
            position = extras.getInt("position");
        }
        diseaseName.setText(mDiseaseName);
    }

    private void populateList() {
        DiseaseSymptomsDbHelper dbHelper = new DiseaseSymptomsDbHelper(this);
        List<DiseaseDBModel> dbModelList = dbHelper.getDiseases();
        Log.d(TAG, "populateList: " + dbModelList.size());
//        ArrayAdapter symptomsArrayAdapter = new ArrayAdapter<DiseaseDBModel>(this, android.R.layout.simple_list_item_1, dbModelList);
        item = new ArrayList<SimpleItem>();
        for (DiseaseDBModel symptom : dbModelList) {

            Log.d(TAG, "populateList: " + symptom.getNo_Allocation());
            switch (mDiseaseName) {
                case "0_No_Allocation":
                    if (symptom.getNo_Allocation() == null) break;
                    if (symptom.getNo_Allocation().equals("NULL") | symptom.getNo_Allocation().equals(""))
                        break;
                    item.add(new SimpleItem(symptom.getNo_Allocation()));
                    break;
                case "Bract_Mosaic":
                    if (symptom.getBract_Mosaic().equals("NULL") | symptom.getBract_Mosaic().equals(""))
                        break;
                    item.add(new SimpleItem(symptom.getBract_Mosaic()));
                    break;
                case "Bunchy_Top":
                    if (symptom.getBunchy_Top().equals("NULL") | symptom.getBunchy_Top().equals(""))
                        break;
                    item.add(new SimpleItem(symptom.getBunchy_Top()));
                    break;
                case "CMV":
                    if (symptom.getCMV().equals("NULL") | symptom.getCMV().equals("")) break;
                    item.add(new SimpleItem(symptom.getCMV()));
                    break;
                case "Gen_Mosaic":
                    if (symptom.getGen_Mosaic().equals("NULL") | symptom.getGen_Mosaic().equals(""))
                        break;
                    item.add(new SimpleItem(symptom.getGen_Mosaic()));
                    break;
                case "SCMV":
                    if (symptom.getSCMV().equals("NULL") | symptom.getSCMV().equals("")) break;
                    item.add(new SimpleItem(symptom.getSCMV()));
                    break;
            }

        }
        buildRecyclerView();
    }

    private void buildRecyclerView() {
        recyclerView = findViewById(R.id.symptomList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new SimpleArrayAdapter(item);
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
                symptomInfo.putExtra("symptomName", item.get(position).getItemName());
                startActivity(symptomInfo);
                Toast.makeText(DiseaseInfo.this, "item at pos: " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDiseaseInfo() {
        Bundle display = getIntent().getExtras();
        int position = display.getInt("position");
        String diseaseName = display.getString("diseaseName");
        this.diseaseName.append(" " + position);
        this.diseaseName.setText(diseaseName);
        this.diseaseDesc.setText(getmDiseaseDesc(diseaseName));
        populateList();
        getDiseaseImage(diseaseName);
    }

    private String getmDiseaseDesc(String mDiseaseName) {
        String diseaseDesc = "";
        switch (mDiseaseName) {
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
        return diseaseDesc;
    }

    private void getDiseaseImage(String mDiseaseName) {
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("Diseases").child(mDiseaseName+".png");
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(DiseaseInfo.this)
                        .load(uri)
                        .into(diseasePic);
            }
        });

    }


}