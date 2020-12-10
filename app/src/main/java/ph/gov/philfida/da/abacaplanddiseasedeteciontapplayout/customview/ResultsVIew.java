package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.customview;

import java.util.List;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.tflite.Classifier;

public interface ResultsVIew {
    public void setResults(final List<Classifier.Recognition> results);
}
