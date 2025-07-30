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
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;

public class DiagnoseModeDialog extends DialogFragment {
    DiagModeListener listener;
    CheckBox rememberChoice;
    Boolean isRememberChoice;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DiagModeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context
                    + "mustImplementEulaListener");
        }
    }

    //TODO: Add default mode and remember mode sleected checkbox
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.diagnose_mode_dialog, null);


        builder.setView(view)
                .setNeutralButton("Single Capture Mode", (dialog, id) -> {
                    isRememberChoice = rememberChoice.isChecked();
                    listener.onSingleModeClicked(isRememberChoice);
                })
                .setPositiveButton("Multiple Capture Mode", (dialog, id) -> {
                    isRememberChoice = rememberChoice.isChecked();
                    listener.onDualModeClicked(isRememberChoice);
                });
        // Create the AlertDialog object and return it
        rememberChoice = view.findViewById(R.id.rememberChoice);

        return builder.create();
    }

    public interface DiagModeListener {
        void onSingleModeClicked(Boolean isRemember);

        void onDualModeClicked(Boolean isRemember);
    }
}
