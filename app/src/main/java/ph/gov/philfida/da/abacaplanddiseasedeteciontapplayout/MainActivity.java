package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otheractivities.AssesmentActivity;

public class MainActivity extends AppCompatActivity {
    TextView username, account_type;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignIds();
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (SaveSharedPreference.getUsername(MainActivity.this).length() == 0) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        } else {
            username.setText(SaveSharedPreference.getUsername(MainActivity.this));
        }
    }

    private void assignIds() {
        username = findViewById(R.id.userName);
    }

    public void openDiagnoseActivity(View view) {
        Intent diagnose = new Intent(this,DetectorActivity.class);
        startActivity(diagnose);
    }

    public void openAssesmentActivity(View view){
        Intent assessment = new Intent(this, AssesmentActivity.class);
        startActivity(assessment);
    }
    public void openAccoutDetails(View view) {
        Intent intent = new Intent(MainActivity.this, AccountDetails.class);
        startActivity(intent);
    }
}
