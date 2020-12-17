package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class AccountDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);
    }

    public void logOut(View view) {
        SaveSharedPreference.clearUsername(this);
        finish();
    }
}