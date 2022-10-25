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

//TODO: Check if this is or will be used, if not, delete it
public class TempDBModel {
    int id;
    String imageName, symptomsDetected;
    byte[] capturedImage1;
    byte[] capturedImage2;

    public TempDBModel(int id, String imageNeme, byte[] capturedImage1, byte[] capturedImage2, String symptomsDetected, Float confidence) {
    }

    @Override
    public String toString() {
        return "TempDBModel{" +
                "id=" + id +
                ", imageName='" + imageName + '\'' +
                ", symptomsDetected='" + symptomsDetected + '\'' +
                ", capturedImage1=" + capturedImage1 +
                ", capturedImage2=" + capturedImage2 +
                ", confidence=" + confidence +
                '}';
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    Float confidence;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSymptomsDetected() {
        return symptomsDetected;
    }

    public void setSymptomsDetected(String symptomsDetected) {
        this.symptomsDetected = symptomsDetected;
    }

    public byte[] getCapturedImage1() {
        return capturedImage1;
    }

    public void setCapturedImage1(byte[] capturedImage1) {
        this.capturedImage1 = capturedImage1;
    }

    public byte[] getCapturedImage2() {
        return capturedImage2;
    }

    public void setCapturedImage2(byte[] capturedImage2) {
        this.capturedImage2 = capturedImage2;
    }

    public Float getConfidence() {
        return confidence;
    }

    public void setConfidence(Float confidence) {
        this.confidence = confidence;
    }
}
