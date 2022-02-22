package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;

public class AboutDev extends AppCompatActivity {
    TextView devsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_dev);
        devsTextView = findViewById(R.id.developers);
        String developersHTML = "<p>Android Application: </p><a href = \"https://github.com/Circoolbilog/\" >Clark L. Llarena</a> <p>Machine Learning Model: </p><a href = \"https://github.com/TaliyaB/\">Christalline Joie Borjal</a>";
        devsTextView.setText(HtmlCompat.fromHtml(developersHTML,HtmlCompat.FROM_HTML_MODE_LEGACY));
        devsTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}