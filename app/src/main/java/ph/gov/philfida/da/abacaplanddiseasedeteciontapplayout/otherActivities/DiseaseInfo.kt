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
package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.DiseaseIndexAdapter
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.SimpleArrayAdapter
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.SimpleItem
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseInfoDBHelper
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseSymptomsDbHelper

class DiseaseInfo : AppCompatActivity() {
    var diseaseName: TextView? = null
    var diseaseDesc: TextView? = null
    var recyclerView: RecyclerView? = null
    var adapter: SimpleArrayAdapter? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var item: ArrayList<SimpleItem?>? = null
    var diseasePic: ImageView? = null
    var position: Int = 0
    var mDiseaseName: String? = ""
    var mDiseaseDesc: String? = null
    var selectedImage: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disease_info)
        assignIds()
        loadDiseaseInfo()
    }

    private fun assignIds() {
        diseaseDesc = findViewById<TextView>(R.id.diseaseDesc)
        diseaseName = findViewById<TextView>(R.id.diseaseNameInd)
        diseasePic = findViewById<ImageView>(R.id.diseaseImageInd)
        val extras = getIntent().getExtras()
        if (extras != null) {
            mDiseaseName = extras.getString("diseaseName")
            position = extras.getInt("position")
        }
        diseaseName!!.setText(mDiseaseName)
    }

    private fun populateList() {
        val dbHelper = DiseaseSymptomsDbHelper(this)
        val dbModelList = dbHelper.getDiseases()
        item = ArrayList<SimpleItem?>()
        for (symptom in dbModelList) {
            when (mDiseaseName) {
                "0_No_Allocation" -> {
                    if (symptom.getNo_Allocation() == null) break
                    if ((symptom.getNo_Allocation() == "NULL") or (symptom.getNo_Allocation() == "")) break
                    item!!.add(SimpleItem(symptom.getNo_Allocation()))
                }

                "Bract_Mosaic" -> {
                    if (symptom.getBract_Mosaic() == null) break
                    if ((symptom.getBract_Mosaic() == "NULL") or (symptom.getBract_Mosaic() == "")) break
                    item!!.add(SimpleItem(symptom.getBract_Mosaic()))
                }

                "Bunchy_Top" -> {
                    if (symptom.getBunchy_Top() == null) break
                    if ((symptom.getBunchy_Top() == "NULL") or (symptom.getBunchy_Top() == "")) break
                    item!!.add(SimpleItem(symptom.getBunchy_Top()))
                }

                "CMV" -> {
                    if (symptom.getCMV() == null) break
                    if ((symptom.getCMV() == "NULL") or (symptom.getCMV() == "")) break
                    item!!.add(SimpleItem(symptom.getCMV()))
                }

                "Gen_Mosaic" -> {
                    if (symptom.getGen_Mosaic() == null) break
                    if ((symptom.getGen_Mosaic() == "NULL") or (symptom.getGen_Mosaic() == "")) break
                    item!!.add(SimpleItem(symptom.getGen_Mosaic()))
                }

                "SCMV" -> {
                    if (symptom.getSCMV() == null) break
                    if ((symptom.getSCMV() == "NULL") or (symptom.getSCMV() == "")) break
                    item!!.add(SimpleItem(symptom.getSCMV()))
                }
            }
        }
        buildRecyclerView()
    }

    private fun buildRecyclerView() {
        recyclerView = findViewById<RecyclerView>(R.id.symptomList)
        layoutManager = LinearLayoutManager(this)
        adapter = SimpleArrayAdapter(item)
        recyclerView!!.setLayoutManager(layoutManager)
        recyclerView!!.setAdapter(adapter)
        adapter!!.setOnItemClickListener(DiseaseIndexAdapter.OnItemClickListener { position: Int ->
//                open new activity displaying disease info on clicked item
//                DataBaseHelper dataBaseHelper = new DataBaseHelper(getApplicationContext());
//                List<SymptomModel> everySymptom = dataBaseHelper.getSymptoms();
//                String symptomName = everySymptom.get(position).getSymptomName();
            val symptomInfo = Intent(this@DiseaseInfo, SymptomInfo::class.java)
            symptomInfo.putExtra("position", position)
            symptomInfo.putExtra("symptomName", item!!.get(position)!!.getItemName())
            startActivity(symptomInfo)
        })
    }

    private fun loadDiseaseInfo() {
        val display = getIntent().getExtras()
        val position = display!!.getInt("position")
        val diseaseName = display.getString("diseaseName")
        this.diseaseName!!.append(" " + position)
        this.diseaseName!!.setText(diseaseName)
        this.diseaseDesc!!.setText(getmDiseaseDesc(diseaseName))
        populateList()
        getDiseaseImage(diseaseName)
    }

    private fun getmDiseaseDesc(mDiseaseName: String?): String? {
        val dbHelper = DiseaseInfoDBHelper(this)
        val dbModelList = dbHelper.getDiseasesInfo()
        var diseaseDesc: String? = ""
        Log.d(TAG, "getmDiseaseDesc: " + dbModelList.size)
        for (infoDBModel in dbModelList) {
            Log.d(TAG, "getmDiseaseDesc: " + infoDBModel.getDiseaseName() + " = " + mDiseaseName)
            if (infoDBModel.getDiseaseName() == mDiseaseName) {
                diseaseDesc = infoDBModel.getDiseaseDesc()
            }
        }

        return diseaseDesc
    }

    private fun getDiseaseImage(mDiseaseName: String?) {
        val imgDir = Environment.getExternalStorageDirectory()
            .toString() + "/Pictures/Diseases/" + mDiseaseName + ".jpg"
        selectedImage = BitmapFactory.decodeFile(imgDir)
        diseasePic!!.setImageBitmap(selectedImage)
    }


    companion object {
        private const val TAG = "DiseaseInfo"
    }
}