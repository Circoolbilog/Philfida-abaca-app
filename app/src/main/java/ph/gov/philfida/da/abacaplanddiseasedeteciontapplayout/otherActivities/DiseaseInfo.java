/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/
package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.SimpleArrayAdapter;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.SimpleItem;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseDBModel;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseInfoDBHelper;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseInfoDBModel;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseSymptomsDbHelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
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
    Bitmap selectedImage;
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
        item = new ArrayList<>();
        for (DiseaseDBModel symptom : dbModelList) {
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
        adapter.setOnItemClickListener(position -> {
//                open new activity displaying disease info on clicked item
//                DataBaseHelper dataBaseHelper = new DataBaseHelper(getApplicationContext());
//                List<SymptomModel> everySymptom = dataBaseHelper.getSymptoms();
//                String symptomName = everySymptom.get(position).getSymptomName();
            Intent symptomInfo = new Intent(DiseaseInfo.this, SymptomInfo.class);
            symptomInfo.putExtra("position", position);
            symptomInfo.putExtra("symptomName", item.get(position).getItemName());
            startActivity(symptomInfo);
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
        DiseaseInfoDBHelper dbHelper = new DiseaseInfoDBHelper(this);
        List<DiseaseInfoDBModel> dbModelList = dbHelper.getDiseasesInfo();
        String diseaseDesc = "";
        Log.d(TAG, "getmDiseaseDesc: " + dbModelList.size());
        for (DiseaseInfoDBModel infoDBModel:dbModelList){
            Log.d(TAG, "getmDiseaseDesc: " + infoDBModel.getDiseaseName() + " = " + mDiseaseName);
            if (infoDBModel.getDiseaseName().equals(mDiseaseName)){
                diseaseDesc = infoDBModel.getDiseaseDesc();
            }
        }

        return diseaseDesc;
    }

    private void getDiseaseImage(String mDiseaseName) {
        String imgDir =  Environment.getExternalStorageDirectory() + "/Pictures/Diseases/" + mDiseaseName + ".jpg";
        selectedImage = BitmapFactory.decodeFile(imgDir);
        diseasePic.setImageBitmap(selectedImage);
    }


}