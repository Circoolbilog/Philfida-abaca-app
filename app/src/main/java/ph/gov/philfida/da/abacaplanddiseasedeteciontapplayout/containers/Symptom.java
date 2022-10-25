/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers;
//TODO: Check if this is or will be used
public class Symptom {
    private String SymptomName, Bract_Mosaic, Bunchy_Top, CMV, Gen_Mosaic, SCMV;

    public Symptom() {
    }

    public Symptom(String SymptomName, String Bract_Mosaic, String Bunchy_Top, String CMV, String Gen_Mosaic, String SCMV) {
        this.SymptomName = SymptomName;
        this.Bract_Mosaic = Bract_Mosaic;
        this.Bunchy_Top = Bunchy_Top;
        this.CMV = CMV;
        this.Gen_Mosaic = Gen_Mosaic;
        this.SCMV = SCMV;
    }
}
