/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

//Welcome screen for the app
public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    String pageOne = "Android Based Abaca Disease Detection App, \n Identify diseases on your Abaca plant";
    String pageTwo = "This app is still under constant improvement, please report any bugs that may appear";

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
            "Instructions coming soon"
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
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
