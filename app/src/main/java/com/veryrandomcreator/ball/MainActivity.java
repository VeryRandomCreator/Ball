/*------------------------------------------------------------------------------
 Copyright (c) 2022 VeryRandomCreator

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 -----------------------------------------------------------------------------*/

package com.veryrandomcreator.ball;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Objects;

/**
 * The class responsible for launching and setting up the app, instantiating {@link SimpleView}, and control the options {@link PopupWindow}.
 * Implements {@link AppCompatActivity} as the base activity of the app.
 * Implements {@link com.veryrandomcreator.ball.SimpleView.SimpleViewAdapter} to allow {@link SimpleView} to send commands and retrieve data from {@link MainActivity}.
 * Implements {@link android.view.View.OnClickListener} to be click listener for the buttons of {@link MainActivity#optionsPopup} ({@link MainActivity#redBallBtn}, {@link MainActivity#greenBallBtn}, {@link MainActivity#blueBallBtn}, {@link MainActivity#purpleBallBtn}, {@link MainActivity#rainbowBallBtn}, {@link MainActivity#soundTglBtn}).
 */
public class MainActivity extends AppCompatActivity implements SimpleView.SimpleViewAdapter, View.OnClickListener {

    /**
     * {@link MainActivity#fontBold} is {@link Typeface} of {@link com.veryrandomcreator.ball.R.font#quicksand_bold} to use bold font.
     */
    private Typeface fontBold;

    /**
     * {@link MainActivity#fontLight} is {@link Typeface} of {@link com.veryrandomcreator.ball.R.font#quicksand_light} to use light font.
     */
    private Typeface fontLight;

    /**
     * {@link MainActivity#fontRegular} is {@link Typeface} of {@link com.veryrandomcreator.ball.R.font#quicksand_regular} to use regular font.
     */
    private Typeface fontRegular;

    /**
     * Static final values of {@link MainActivity#optionsPopup}.
     */
    public static final int OPTIONS_WIDTH = 200;
    public static final int OPTIONS_HEIGHT = 200;
    public static final int MARGIN_FROM_TOP = 25;

    /**
     * Default amount of time to vibrate.
     */
    public static final int VIBRATE_TIME = 500;

    /**
     * {@link MainActivity#fadeOutAnim} is {@link Animation} of {@link com.veryrandomcreator.ball.R.anim#fade_out}, to fade {@link MainActivity#options} out.
     */
    private Animation fadeOutAnim;

    /**
     * {@link MainActivity#fadeInAnim} is {@link Animation} of {@link com.veryrandomcreator.ball.R.anim#fade_in}, to fade {@link MainActivity#options} in.
     */
    private Animation fadeInAnim;

    private SimpleView simpleView;
    private ImageButton options;
    private TextView titleLbl;
    private PopupWindow optionsPopup;

    /**
     * Buttons in {@link MainActivity#optionsPopup}
     */
    private ImageButton redBallBtn;
    private ImageButton greenBallBtn;
    private ImageButton blueBallBtn;
    private ImageButton purpleBallBtn;
    private ImageButton rainbowBallBtn;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch soundTglBtn;

    /**
     * Stores whether sound should be on or off
     */
    private boolean shouldPlaySound = false;

    /**
     * Loads fonts, animations, instantiates {@link SimpleView}, and sets up options menu button {@link MainActivity#options}.
     *
     * @param savedInstanceState Default param of {@link AppCompatActivity#onCreate(Bundle)}
     */
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        removeActionBar();

        loadFonts();
        loadAnimations();
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        point.y += getWindowManager().getCurrentWindowMetrics().getBounds().height() - point.y;
        simpleView = new SimpleView(this, point, this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addContentView(simpleView, params);

        prepareTitleLbl();
        RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.leftMargin = point.x / 2 - titleLbl.getMeasuredWidth() / 2;
        titleParams.topMargin = point.y / 2 - titleLbl.getMeasuredHeight() / 2;
        titleLbl.setLayoutParams(titleParams);

        addContentView(titleLbl, titleParams);
        loadOptionsButton(point);
    }

    /**
     * Sets up the necessary characteristics of the title {@link TextView}
     */
    public void prepareTitleLbl() {
        titleLbl = new TextView(this);
        titleLbl.setTextColor(getResources().getColor(R.color.soft_dark_grey));
        titleLbl.setText(getResources().getText(R.string.title));
        titleLbl.setTypeface(fontLight);
        titleLbl.setTextSize(100);
        titleLbl.measure(0, 0);
    }

    /**
     * Sets up {@link MainActivity#options}
     *
     * @param point Window bounds point (side of window)
     */
    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    public void loadOptionsButton(Point point) {
        options = new ImageButton(getApplicationContext());
        options.setImageDrawable(getResources().getDrawable(R.drawable.options));
        options.setBackgroundColor(getResources().getColor(R.color.transparent));
        options.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    options.setImageDrawable(getResources().getDrawable(R.drawable.options_clicked));
                    break;
                case MotionEvent.ACTION_UP:
                    options.setImageDrawable(getResources().getDrawable(R.drawable.options));
                    launchOptionsPopup();
                    break;
            }
            return true;
        });

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width = OPTIONS_WIDTH;
        params.height = OPTIONS_HEIGHT;
        params.leftMargin = point.x - OPTIONS_WIDTH;
        params.topMargin = MARGIN_FROM_TOP;
        addContentView(options, params);

        toggleOptionsVisibility();
    }

    /**
     * Loads fonts
     */
    public void loadFonts() {
        fontBold = ResourcesCompat.getFont(getApplicationContext(), R.font.quicksand_bold);
        fontLight = ResourcesCompat.getFont(getApplicationContext(), R.font.quicksand_light);
        fontRegular = ResourcesCompat.getFont(getApplicationContext(), R.font.quicksand_regular);
    }

    /**
     * Loads animations
     */
    public void loadAnimations() {
        fadeOutAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        fadeInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
    }

    /**
     * Options the {@link MainActivity#optionsPopup}. Sets {@link MainActivity} as {@link android.view.View.OnClickListener}, and sets the necessary fonts and scales for {@link MainActivity#soundTglBtn}, {@link MainActivity#redBallBtn}, {@link MainActivity#greenBallBtn}, {@link MainActivity#blueBallBtn}, {@link MainActivity#purpleBallBtn}, and {@link MainActivity#rainbowBallBtn}.
     */
    public void launchOptionsPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupMenu = inflater.inflate(R.layout.options_popup, null);
        optionsPopup = new PopupWindow(popupMenu, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        optionsPopup.showAtLocation(simpleView, Gravity.CENTER, 0, 0);

        soundTglBtn = popupMenu.findViewById(R.id.soundTglBtn);
        redBallBtn = popupMenu.findViewById(R.id.redBallBtn);
        greenBallBtn = popupMenu.findViewById(R.id.greenBallBtn);
        blueBallBtn = popupMenu.findViewById(R.id.blueBallBtn);
        purpleBallBtn = popupMenu.findViewById(R.id.purpleBallBtn);
        rainbowBallBtn = popupMenu.findViewById(R.id.rainbowBallBtn);

        redBallBtn.setOnClickListener(this);
        greenBallBtn.setOnClickListener(this);
        blueBallBtn.setOnClickListener(this);
        purpleBallBtn.setOnClickListener(this);
        rainbowBallBtn.setOnClickListener(this);

        soundTglBtn.setTypeface(fontLight);
        soundTglBtn.setChecked(shouldPlaySound);
        soundTglBtn.setOnClickListener(v -> shouldPlaySound = !shouldPlaySound);

        setSquareScale(rainbowBallBtn, 1.25f);

        updateSelectedBall();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrate();
        }
    }

    /**
     * Sets the clicked button to its according color.
     *
     * @param v Default param for {@link android.view.View.OnClickListener#onClick(View)}. The view that is clicked
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.redBallBtn:
                simpleView.setCurrentMode(Mode.DEFAULT);
                simpleView.setCurrentColor(Ball.SOFT_RED);
                break;
            case R.id.greenBallBtn:
                simpleView.setCurrentMode(Mode.DEFAULT);
                simpleView.setCurrentColor(Ball.SOFT_GREEN);
                break;
            case R.id.blueBallBtn:
                simpleView.setCurrentMode(Mode.DEFAULT);
                simpleView.setCurrentColor(Ball.SOFT_BLUE);
                break;
            case R.id.purpleBallBtn:
                simpleView.setCurrentMode(Mode.DEFAULT);
                simpleView.setCurrentColor(Ball.SOFT_PURPLE);
                break;
            case R.id.rainbowBallBtn:
                simpleView.setCurrentMode(Mode.COLOR_SWITCH);
        }
        updateSelectedBall();
    }

    /**
     * Updates the visuals in {@link MainActivity#optionsPopup} to match the currently selected button.
     */
    public void updateSelectedBall() {
        if (simpleView.getCurrentMode().equals(Mode.COLOR_SWITCH)) {
            setSquareScale(rainbowBallBtn, 2f);
            setSquareScale(redBallBtn, 1f);
            setSquareScale(greenBallBtn,1f);
            setSquareScale(blueBallBtn, 1f);
            setSquareScale(purpleBallBtn, 1f);
            return;
        }
        switch (simpleView.getCurrentColor()) {
            case Ball.SOFT_RED:
                setSquareScale(redBallBtn, 1.35f);
                setSquareScale(greenBallBtn,1f);
                setSquareScale(blueBallBtn, 1f);
                setSquareScale(purpleBallBtn, 1f);
                setSquareScale(rainbowBallBtn, 1.25f);
                break;
            case Ball.SOFT_GREEN:
                setSquareScale(greenBallBtn,1.35f);
                setSquareScale(redBallBtn, 1f);
                setSquareScale(blueBallBtn, 1f);
                setSquareScale(purpleBallBtn, 1f);
                setSquareScale(rainbowBallBtn, 1.25f);
                break;
            case Ball.SOFT_BLUE:
                setSquareScale(blueBallBtn, 1.35f);
                setSquareScale(redBallBtn, 1f);
                setSquareScale(greenBallBtn,1f);
                setSquareScale(purpleBallBtn, 1f);
                setSquareScale(rainbowBallBtn, 1.25f);
                break;
            case Ball.SOFT_PURPLE:
                setSquareScale(purpleBallBtn, 1.35f);
                setSquareScale(redBallBtn, 1f);
                setSquareScale(greenBallBtn,1f);
                setSquareScale(blueBallBtn, 1f);
                setSquareScale(rainbowBallBtn, 1.25f);
                break;
        }
    }

    /**
     * A more efficient way of scaling up ball buttons.
     *
     * @param view Ball button ({@link MainActivity#soundTglBtn}, {@link MainActivity#redBallBtn}, {@link MainActivity#greenBallBtn}, {@link MainActivity#blueBallBtn}, {@link MainActivity#purpleBallBtn}, {@link MainActivity#rainbowBallBtn}).
     * @param scale Size multiplier
     */
    public void setSquareScale(View view, float scale) {
        view.setScaleX(scale);
        view.setScaleY(scale);
    }

    /**
     * A method to toggle the visibility of {@link MainActivity#options}. Calls {@link MainActivity#setOptionsVisibility(int)}.
     */
    public void toggleOptionsVisibility() {
        if (options.getVisibility() == View.VISIBLE) {
            setOptionsVisibility(View.INVISIBLE);
        } else {
            setOptionsVisibility(View.VISIBLE);
        }
    }

    /**
     * A method to set the visibility of {@link MainActivity#options}.
     *
     * @param visibility {@link View#VISIBLE} or {@link View#INVISIBLE} depending on what state {@link MainActivity#options} should be in.
     */
    public void setOptionsVisibility(int visibility) {
        switch (visibility) {
            case View.VISIBLE:
                if (options.getVisibility() != View.VISIBLE) {
                    options.startAnimation(fadeInAnim);
                    options.setVisibility(View.VISIBLE);
                }
                break;
            case View.INVISIBLE:
                if (options.getVisibility() != View.INVISIBLE) {
                    options.startAnimation(fadeOutAnim);
                    options.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onResume() {
        super.onResume();
        simpleView.onResume();
        removeActionBar();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onPause() {
        super.onPause();
        simpleView.onPause();
        removeActionBar();
        simpleView.clearTemp();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onBackPressed() {
        removeActionBar();
        setOptionsVisibility(View.VISIBLE);
        simpleView.clearTemp();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        removeActionBar();
        setOptionsVisibility(View.VISIBLE);
        simpleView.clearTemp();
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    /**
     * Removes navigation bar
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void removeActionBar() {
        getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    /**
     * Closes {@link MainActivity#optionsPopup} when ball is spawned.
     */
    @Override
    public void onClickRelease() {
        setOptionsVisibility(View.INVISIBLE);
    }

    /**
     * @return A boolean of whether or not sound should be on.
     */
    @Override
    public boolean shouldPlaySound() {
        return shouldPlaySound;
    }

    @Override
    public void removeTitle() {
        titleLbl.setVisibility(View.INVISIBLE);
    }

    /**
     * Vibrates device
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(VibrationEffect.createOneShot(VIBRATE_TIME, VibrationEffect.DEFAULT_AMPLITUDE));
    }
}