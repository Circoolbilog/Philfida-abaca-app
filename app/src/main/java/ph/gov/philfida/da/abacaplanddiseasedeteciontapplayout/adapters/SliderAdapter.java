package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    String pageOne = "Android Based Abaca Disease Detection App, \n Identify diseases on your Abaca plant";
    String pageTwo = "This app is still under development, please report any bugs that may appear";
    String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas dapibus eu nibh et porta. Vestibulum posuere velit eu dolor tincidunt rutrum. Duis vitae tortor volutpat, ultrices tortor id, congue mi. Pellentesque sit amet urna leo. Integer et gravida leo, sed luctus nisl. Cras vel accumsan ante, vel sodales sem. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Pellentesque porttitor mattis urna, semper dictum sem mattis vitae. Etiam sodales quis sapien quis convallis. Nullam eget tempor purus, nec euismod dolor. ";
    public SliderAdapter(Context context) {
        this.context = context;
    }

    //  Arrays
    public int[] slide_images = {
            R.drawable.abaca_welcome_1,
            R.drawable.under_maintenance
    };
    public String[] headings = {
            "Welcome",
            "Under Development"
    };
    public String[] desc= {
            pageOne,pageTwo
    };

    @Override
    public int getCount() {
        return headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.welcome_slide,container,false);
        ImageView slideImageView = view.findViewById(R.id.imageView2);
        TextView title = view.findViewById(R.id.title1);
        TextView slideDescription = view.findViewById(R.id.slideDescription);

        slideImageView.setImageResource(slide_images[position]);
        title.setText(headings[position]);
        slideDescription.setText(desc[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
