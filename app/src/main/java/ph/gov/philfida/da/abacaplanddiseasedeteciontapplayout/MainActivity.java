package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    boolean runOnce = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!runOnce){
            runOnce = true;
            Intent intent = new Intent(this , Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(intent,0);
        }
    }

    public void openDiagnoseActivity(View view) {
        Intent diagnose = new Intent(this,Diagnose.class);
        startActivity(diagnose);
    }
}
