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


//TODO: Rework Database
public class DiseaseInfoDBModel {
    int id;
    private String diseaseName;
    private String diseaseDesc;
    private String picture;
    private String treatment;

    @Override
    public String toString() {
        return "DiseaseInfoDBModel{" +
                "id=" + id +
                ", diseaseName='" + diseaseName + '\'' +
                ", diseaseDesc='" + diseaseDesc + '\'' +
                ", picture='" + picture + '\'' +
                ", treatment='" + treatment + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getDiseaseDesc() {
        return diseaseDesc;
    }

    public void setDiseaseDesc(String diseaseDesc) {
        this.diseaseDesc = diseaseDesc;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public DiseaseInfoDBModel() {

    }

    public DiseaseInfoDBModel(int id, String diseaseName, String diseaseDesc, String picture, String treatment) {
        this.id = id;
        this.diseaseName = diseaseName;
        this.diseaseDesc = diseaseDesc;
        this.picture = picture;
        this.treatment = treatment;
    }
}
