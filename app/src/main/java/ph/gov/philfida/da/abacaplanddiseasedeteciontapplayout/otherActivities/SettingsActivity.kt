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
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.Dialogs.SettingsDialog1
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.Dialogs.SettingsDialog1.SettingsDialogListener
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.SettingsContainer
import java.text.MessageFormat

class SettingsActivity : AppCompatActivity(), SettingsDialogListener {
    var captureMode: LinearLayout? = null
    var userAccount: LinearLayout? = null
    var locationSettings: LinearLayout? = null
    var confidenceThreshold: LinearLayout? = null
    var showWelcome: LinearLayout? = null
    var language: LinearLayout? = null
    var about: LinearLayout? = null
    var captureModeVal: TextView? = null
    var userAccountVal: TextView? = null
    var locationSettingsVal: TextView? = null
    var confidenceThresholdVal: TextView? = null
    var showWelcomeVal: TextView? = null
    var languageVal: TextView? = null
    var sCaptureMode: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupIDs()
        loadSharedPrefs()


        captureMode!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                openDialog(
                    "Capture Mode",
                    "Select preferred Detection Method: ",
                    "Always Ask First",
                    "Single Capture Mode",
                    "Multi-Capture Mode",
                    0
                )
            }
        })

        locationSettings!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
            }
        })
        confidenceThreshold!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                openDialog(
                    "Enter Confidence Threshold",
                    "Enter desired value for minimum confidence(cancel if you don't know what this does)",
                    "Okay",
                    "Cancel",
                    null,
                    R.layout.custom_confidence_threshold_dialog
                )
            }
        })
        //        confidenceThreshold.setVisibility(View.INVISIBLE);
//        confidenceThreshold.setEnabled(false);
        showWelcome!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                openDialog(
                    "Show Welcome Screen",
                    "Show welcome screen at start of app? ",
                    "Yes",
                    "No",
                    null,
                    0
                )
            }
        })
        about!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(this@SettingsActivity, AboutApp::class.java)
                startActivity(intent)
            }
        })
        languageVal!!.setText(R.string.wip)

        //TODO: Add Change Camera
        //TODO: Add Dark Mode
        //TODO: Add update and notif settings
        //TODO: Weather
    }


    private fun openDialog(
        title: String?, message: String?, option1: String?,
        option2: String?, option3: String?, customLayout: Int
    ) {
        val dialog1 = SettingsDialog1()
        dialog1.setCustomDialogLayout(customLayout)
        dialog1.setContext(this@SettingsActivity)
        dialog1.setDialogTitle(title)
        dialog1.setDialogMessage(message)
        dialog1.setOption1(option1)
        dialog1.setOption2(option2)
        dialog1.setOption3(option3)
        dialog1.show(getSupportFragmentManager(), "dialog1")
    }


    private fun loadSharedPrefs() {
        if (!(this.getApplication() as SettingsContainer).getDiagDialogRemember()) {
            sCaptureMode = "Always Ask"
        } else {
            when ((this.getApplication() as SettingsContainer).getDiagnoseMode()) {
                0 -> sCaptureMode = "Single Capture Mode"
                1 -> sCaptureMode = "Multi-Capture Mode"
            }
        }
        showWelcomeVal!!.setText(
            (this.getApplication() as SettingsContainer).getShowWelcome().toString()
        )
        captureModeVal!!.setText(sCaptureMode)
        val conValThresh =
            Math.round((this.getApplication() as SettingsContainer).getConfidence() * 100)
                .toString()
        confidenceThresholdVal!!.setText(MessageFormat.format("{0}%", conValThresh))
    }

    private fun setupIDs() {
        languageVal = findViewById<TextView>(R.id.languageVal)
        showWelcomeVal = findViewById<TextView>(R.id.ShowWelcomeScreenVal)
        userAccountVal = findViewById<TextView>(R.id.userAccVal)
        captureModeVal = findViewById<TextView>(R.id.captureModeVal)
        captureMode = findViewById<LinearLayout>(R.id.captureModeSet)
        userAccount = findViewById<LinearLayout>(R.id.userAccSet)
        locationSettings = findViewById<LinearLayout>(R.id.LocationSet)
        confidenceThreshold = findViewById<LinearLayout>(R.id.ConfidenceThreshSet)
        confidenceThresholdVal = findViewById<TextView>(R.id.ConfidenceThreshVal)
        showWelcome = findViewById<LinearLayout>(R.id.ShowWelcomeScreen)
        language = findViewById<LinearLayout>(R.id.languageSet)
        about = findViewById<LinearLayout>(R.id.aboutApp)
    }

    fun setCaptureMode(Selection: Int) {
        when (Selection) {
            1 -> (getApplication() as SettingsContainer).setDiagDialogRemember(false)
            2 -> {
                (this.getApplication() as SettingsContainer).setDiagDialogRemember(true)
                (this.getApplication() as SettingsContainer).setDiagnoseMode(0)
            }

            3 -> {
                (this.getApplication() as SettingsContainer).setDiagDialogRemember(true)
                (this.getApplication() as SettingsContainer).setDiagnoseMode(1)
            }
        }
        loadSharedPrefs()
    }


    fun setWelcomeScreen(selection: Int) {
        when (selection) {
            1 -> (this.getApplication() as SettingsContainer).setShowWelcome(true)
            2 -> (this.getApplication() as SettingsContainer).setShowWelcome(false)
        }
        loadSharedPrefs()
    }

    fun setConfidenceThreshold(confidenceThreshold: Int) {
        if (confidenceThreshold == 0) return
        val f = confidenceThreshold.toFloat()
        loadSharedPrefs()
        Toast.makeText(this, "confidence: " + f, Toast.LENGTH_SHORT).show()
    }


    override fun applyConfidence(confidence: Int) {
        loadSharedPrefs()
        if (confidence == 0) return
        val fConfidence = confidence / 100f
        Toast.makeText(this, "confidence: " + confidence, Toast.LENGTH_SHORT).show()
        (this.getApplication() as SettingsContainer).setConfidence(fConfidence)
    }
}
