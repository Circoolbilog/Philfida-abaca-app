package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.SliderAdapter;

public class WelcomeScreen extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout page;
    TextView[] pageDots;
    Button nextBtn, prevBtn;

    int mCurrentPage;

    private SliderAdapter sliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        viewPager = findViewById(R.id.slideViewPager);
        page = findViewById(R.id.dotsPageIndicator);

        nextBtn = findViewById(R.id.nextPageButtn);
        prevBtn = findViewById(R.id.previousPageButtn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPage == pageDots.length-1){
                    finish();
                }
                viewPager.setCurrentItem(mCurrentPage+1);
            }
        });
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(mCurrentPage-1);
            }
        });

        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);
        addDotsIndicator(0);

        viewPager.addOnPageChangeListener(viewListener);
    }

    public void addDotsIndicator(int position) {
        pageDots = new TextView[2];
        page.removeAllViews();
        for (int i = 0; i < pageDots.length; i++) {
            pageDots[i] = new TextView(this);
            pageDots[i].setText(Html.fromHtml("&#8226;"));
            pageDots[i].setTextSize(45);
            pageDots[i].setTextColor(getResources().getColor(R.color.translucent_bg));

            page.addView( pageDots[i]);
        }
        if (pageDots.length>0){
            pageDots[position].setTextColor(getResources().getColor(R.color.appClick));
        }
    }
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            mCurrentPage = position;
            if (position==0){
                nextBtn.setEnabled(true);
                prevBtn.setEnabled(false);
                prevBtn.setVisibility(View.INVISIBLE);
            }else if (position == pageDots.length-1){
                nextBtn.setEnabled(true);
                prevBtn.setEnabled(true);
                prevBtn.setVisibility(View.VISIBLE);
                nextBtn.setText("Finish");
            }else{
                nextBtn.setEnabled(true);
                prevBtn.setEnabled(true);
                prevBtn.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}