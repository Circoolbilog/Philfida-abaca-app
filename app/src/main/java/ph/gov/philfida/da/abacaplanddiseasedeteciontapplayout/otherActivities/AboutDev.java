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
        String developersHTML = "<a href = \"https://github.com/Circoolbilog/\" >Clark Llarena</a>";
        String aiDev = "<a href = \"https://github.com/TaliyaB/\">Christalline Joie Borjal</a>";
        devsTextView.setText(HtmlCompat.fromHtml(developersHTML,HtmlCompat.FROM_HTML_MODE_LEGACY));
        devsTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}