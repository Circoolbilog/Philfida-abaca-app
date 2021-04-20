package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers;

public class CapturedImageDiseasePrediction {
    
    public class Predictions{
        public String symptom;
        public float confidence;
        public String symptomName(){

           return symptom;
        }
        public float symptomConfidence(){

            return confidence;
        }
        public Predictions(String symptomName, float confidence){
            this.symptom = symptomName;
            this.confidence = confidence;
        }
    }
}
