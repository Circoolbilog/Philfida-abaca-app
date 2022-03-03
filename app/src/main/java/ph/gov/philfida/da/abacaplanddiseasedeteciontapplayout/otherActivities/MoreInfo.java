package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;

public class MoreInfo extends AppCompatActivity {
TextView title, symptomsList;
ImageView withBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);
        symptomsList = findViewById(R.id.symptomsList);
        withBox = findViewById(R.id.withBox);
        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            double lat = extras.getDouble("lat");
            double longt = extras.getDouble("longt");
            Bitmap bitmap = BitmapFactory.decodeByteArray(extras.getByteArray("imageWithBox"), 0, extras.getByteArray("imageWithBox").length);
            withBox.setImageBitmap(bitmap);
            symptomsList.setText(HtmlCompat.fromHtml(extras.getString("Symptoms"),HtmlCompat.FROM_HTML_MODE_LEGACY));
            symptomsList.append("\n");
            symptomsList.append("GEOLOCATION: \n");
            symptomsList.append("Latitude: " + lat);
            symptomsList.append("\n");
            symptomsList.append("Longitude: " + longt);
        }
    }
}