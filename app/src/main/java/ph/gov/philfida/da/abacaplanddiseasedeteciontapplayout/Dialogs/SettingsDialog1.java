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
package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import org.jetbrains.annotations.NotNull;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.SettingsContainer;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.SettingsActivity;

public class SettingsDialog1 extends AppCompatDialogFragment {
    private static final String TAG = "SettingsDialog1";
    String dialogTitle;
    String dialogMessage;
    String option1;
    String option2;
    String option3;
    int customDialogLayout;

    public int getCustomDialogLayout() {
        return customDialogLayout;
    }

    public void setCustomDialogLayout(int customDialogLayout) {
        this.customDialogLayout = customDialogLayout;
    }

    Context context;

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    String option4;

    public String getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public String getDialogMessage() {
        return dialogMessage;
    }

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    EditText confidenceVal;
    LayoutInflater inflater;
    View view;
    private SettingsDialogListener listener;

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        CharSequence[] items = getResources().getStringArray(R.array.colors_array);
        if (customDialogLayout != 0) {
            // Get the layout inflater
            inflater = requireActivity().getLayoutInflater();
            view = inflater.inflate(R.layout.custom_confidence_threshold_dialog, null);
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view);
            confidenceVal = view.findViewById(R.id.customThreshold);

        }
        builder.setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton(option1, (dialog, which) -> {
                    if (customDialogLayout == R.layout.custom_confidence_threshold_dialog) {
                        String confidenceText = confidenceVal.getText().toString().trim();
                        if (!confidenceText.isEmpty()) {
                            try {
                                int confidence = Integer.parseInt(confidenceText);
                                listener.applyConfidence(confidence);
                            } catch (NumberFormatException e) {
                                // Handle the case where input is not a valid integer
                                // Show an error message or take appropriate action
                            }
                        } else {
                            // Handle the case where input is empty
                            // Show an error message or take appropriate action
                        }
                    } else {
                        call(1);
                    }
                })
                .setNegativeButton(option2, (dialog, which) -> call(2))
                .setNeutralButton(option3, (dialog, which) -> call(3));

        return builder.create();

    }

    private void call(int i) {
        SettingsContainer settingsContainer = new SettingsContainer();
        switch (dialogTitle) {
            case "Capture Mode":
                ((SettingsActivity) this.requireActivity()).setCaptureMode(i);
                break;
            case "Show Welcome Screen":
                ((SettingsActivity) this.requireActivity()).setWelcomeScreen(i);
                break;
            case "Enter Confidence Threshold":
                ((SettingsActivity) this.requireActivity()).setConfidenceThreshold(i);
                break;
        }
    }
    public interface SettingsDialogListener{
        void applyConfidence(int confidence);
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        try {
            listener = (SettingsDialogListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + "must implement SettingsDialogListener");
        }
    }
}
