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


//TODO:Rework the database
package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers;

public class DiseaseDBModel {
    int id;
    private String symptomName;
    private String Bract_Mosaic;
    private String Bunchy_Top;
    private String CMV;
    private String Gen_Mosaic;
    private String SCMV;

    @Override
    public String toString() {
        return "DiseaseDBModel{" +
                "id=" + id +
                ", symptomName='" + symptomName + '\'' +
                ", Bract_Mosaic='" + Bract_Mosaic + '\'' +
                ", Bunchy_Top='" + Bunchy_Top + '\'' +
                ", CMV='" + CMV + '\'' +
                ", Gen_Mosaic='" + Gen_Mosaic + '\'' +
                ", SCMV='" + SCMV + '\'' +
                ", No_Allocation='" + No_Allocation + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSymptomName() {
        return symptomName;
    }

    public void setSymptomName(String symptomName) {
        this.symptomName = symptomName;
    }

    public String getBract_Mosaic() {
        return Bract_Mosaic;
    }

    public void setBract_Mosaic(String bract_Mosaic) {
        Bract_Mosaic = bract_Mosaic;
    }

    public String getBunchy_Top() {
        return Bunchy_Top;
    }

    public void setBunchy_Top(String bunchy_Top) {
        Bunchy_Top = bunchy_Top;
    }

    public String getCMV() {
        return CMV;
    }

    public void setCMV(String CMV) {
        this.CMV = CMV;
    }

    public String getGen_Mosaic() {
        return Gen_Mosaic;
    }

    public void setGen_Mosaic(String gen_Mosaic) {
        Gen_Mosaic = gen_Mosaic;
    }

    public String getSCMV() {
        return SCMV;
    }

    public void setSCMV(String SCMV) {
        this.SCMV = SCMV;
    }

    public String getNo_Allocation() {
        return No_Allocation;
    }

    public void setNo_Allocation(String no_Allocation) {
        No_Allocation = no_Allocation;
    }

    public DiseaseDBModel(int id,String No_Allocation, String bract_Mosaic, String bunchy_Top, String CMV, String gen_Mosaic, String SCMV) {
        this.id = id;
        Bract_Mosaic = bract_Mosaic;
        Bunchy_Top = bunchy_Top;
        this.CMV = CMV;
        Gen_Mosaic = gen_Mosaic;
        this.SCMV = SCMV;
        this.No_Allocation = No_Allocation;
    }

    private String No_Allocation;

    public DiseaseDBModel() {
    }


}
