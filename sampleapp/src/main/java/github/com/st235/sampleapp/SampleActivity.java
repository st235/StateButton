package github.com.st235.sampleapp;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import github.com.st235.statebutton.StateButton;
import github.com.st235.statebutton.events.OnStateChangedListener;

public class SampleActivity extends AppCompatActivity {

    private StateButton stateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        stateButton = findViewById(R.id.statebutton);

        stateButton.addStatesDrawable(R.drawable.ic_format_align_left_black,
                R.drawable.ic_format_align_center_black, R.drawable.ic_format_align_right_black);
        stateButton.setOnStateChangedListener(new OnStateChangedListener() {
            @Override
            public void onStateChanged(boolean isEnabled, int state) {
                if (isEnabled) {
                    GradientDrawable bgShape = (GradientDrawable) stateButton.getBackground();
                    bgShape.setColor(Color.parseColor("#4DD0E1"));
                } else {
                    GradientDrawable bgShape = (GradientDrawable) stateButton.getBackground();
                    bgShape.setColor(Color.parseColor("#BDBDBD"));
                }

                Log.d("Here", String.valueOf(state));
            }
        });
    }
}
