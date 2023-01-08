/*------------------------------------------------------------------------------
 Copyright (c) 2022-2023 VeryRandomCreator

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 -----------------------------------------------------------------------------*/

package com.veryrandomcreator.ball;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The class of the view the main interface is displayed on. The code for some of this class was referenced from <a href="https://google-developer-training.github.io/android-developer-advanced-course-practicals/unit-5-advanced-graphics-and-views/lesson-11-canvas/11-2-p-create-a-surfaceview/11-2-p-create-a-surfaceview.html">here</a>, code from that website is under Creative Commons Attribution 4.0 International License, and is attributed to the Google Developers Training Team.
 */
@SuppressLint("ViewConstructor")
public class SimpleView extends SurfaceView implements Runnable {
    private Thread thread = null;

    private boolean isActive = false;
    private boolean hasReleased = false;
    private boolean hasStarted = false;
    private boolean isTrashActive = false;

    private final int WIDTH, HEIGHT;
    private final SurfaceHolder SURFACE_HOLDER;
    private final SimpleViewAdapter SIMPLE_VIEW_ADAPTER;

    private int titleRand = 0;
    private long previousTime = -1;
    private byte currentColor = Ball.SOFT_RED;
    private byte currentMode = MODE_DEFAULT;
    private Point actionDown = null;

    private Ball recentBall;
    private List<Ball> balls = new ArrayList<>();
    private List<BallPredictionObject> ballPredictions = new ArrayList<>();
    private List<TitleBall> titleBalls = new ArrayList<>();

    MediaPlayer sound = null;

    public static final byte MODE_DEFAULT = 1;
    public static final byte MODE_COLOR_SWITCH = 2;

    /**
     * The constructor for this view.
     *
     * @param context           Necessary param from inheriting {@link SurfaceView}
     * @param point             Size of screen stored in {@link Point}
     * @param simpleViewAdapter Allows commands to be send and data to be retrieved from {@link MainActivity}.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public SimpleView(Context context, Point point, SimpleViewAdapter simpleViewAdapter) {
        super(context);
        SIMPLE_VIEW_ADAPTER = simpleViewAdapter;
        WIDTH = point.x;
        HEIGHT = point.y;
        SURFACE_HOLDER = getHolder();
        setupSurface();
    }

    /**
     * Sets up {@link SurfaceView}. Sets a custom {@link android.view.View.OnTouchListener}.
     */
    @SuppressLint("ClickableViewAccessibility")
    public void setupSurface() {
        titleRand = new Random().nextInt(2);
        setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onPressDown(event);
                    break;
                case MotionEvent.ACTION_UP:
                    onRelease(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!hasReleased) {
                        onDrag(event);
                    }
                    break;
            }
            return true;
        });
    }

    /**
     * Predicts the path of the ball.
     *
     * @param event Data necessary for the prediction of the ball's path, such as the location the {@link MotionEvent#ACTION_MOVE} is occurring at.
     */
    public void onDrag(MotionEvent event) {
        if (!isTrashActive) {
            ballPredictions = new ArrayList<>();
            int currentVelX = getVelFromDis(actionDown.x, (int) event.getX());
            int currentVelY = getVelFromDis(actionDown.y, (int) event.getY());
            BallPredictionObject previous = null;
            BallPredictionObject future;
            for (int i = 0; i < 15; i++) {
                if (previous == null) {
                    previous = new BallPredictionObject(actionDown.x + currentVelX, actionDown.y + currentVelY);
                    future = new BallPredictionObject(actionDown.x + currentVelX, actionDown.y + currentVelY);
                } else {
                    future = new BallPredictionObject(previous.x + currentVelX, previous.y + currentVelY);
                }
                if (future.x <= 0) {
                    future.x *= -1;
                    currentVelX *= -1;
                }
                if (future.x >= WIDTH) {
                    future.x = WIDTH - (previous.x + currentVelX - WIDTH);
                    currentVelX *= -1;
                }
                if (future.y <= 0) {
                    future.y *= -1;
                    currentVelY *= -1;
                }
                if (future.y >= HEIGHT) {
                    future.y = HEIGHT - (previous.y + currentVelY - HEIGHT);
                    currentVelY *= -1;
                }
                ballPredictions.add(future);
                previous = future;
            }
        } else {
            deleteBallAt((int) event.getX(), (int) event.getY());
        }
    }

    /**
     * @param event Data necessary for the determination of the ball's velocity, such as the location the {@link MotionEvent#ACTION_DOWN} is occurring at.
     */
    public void onPressDown(MotionEvent event) {
        if (!isTrashActive) {
            spawnBall((int) event.getX(), (int) event.getY(), 0, 0);
            actionDown = new Point((int) event.getX(), (int) event.getY());
            hasReleased = false;
            if (!hasStarted) {
                clearTitle();
            }
        }
    }

    /**
     * Removes the {@link TitleBall}s and {@link android.widget.TextView} from the screen.
     */
    public void clearTitle() {
        hasStarted = true;
        titleBalls.clear();
        SIMPLE_VIEW_ADAPTER.removeTitle();
    }

    /**
     * Spawns the ball.
     *
     * @param event Data necessary for the determination of the ball's velocity, such as the location the {@link MotionEvent#ACTION_UP} is occurring at.
     */
    public void onRelease(MotionEvent event) {
        if (!isTrashActive) {
            hasReleased = true;
            switch (currentMode) {
                case MODE_DEFAULT:
                    break;
                case MODE_COLOR_SWITCH:
                    switch (currentColor) {
                        case Ball.SOFT_RED:
                            currentColor = Ball.SOFT_BLUE;
                            break;
                        case Ball.SOFT_BLUE:
                            currentColor = Ball.SOFT_GREEN;
                            break;
                        case Ball.SOFT_GREEN:
                            currentColor = Ball.SOFT_PURPLE;
                            break;
                        case Ball.SOFT_PURPLE:
                            currentColor = Ball.SOFT_RED;
                            break;
                    }
                    break;
            }
            launchBall(getVelFromDis(actionDown.x, (int) event.getX()), getVelFromDis(actionDown.y, (int) event.getY()));
            ballPredictions = new ArrayList<>();
            SIMPLE_VIEW_ADAPTER.onClickRelease();
        } else {
            deleteBallAt((int) event.getX(), (int) event.getY());
        }
    }

    public void deleteBallAt(int x, int y) {
        for (int i = 0; i < balls.size(); i++) {
            if (balls.get(i).intersects(x, y)) {
                balls.remove(i);
            }
        }
    }

    public byte getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(byte currentMode) {
        this.currentMode = currentMode;
    }

    /**
     * Calculates the velocity of the ball based on the start and end positions of the ball.
     *
     * @param start The location that the {@link MotionEvent#ACTION_DOWN} occurred at
     * @param end   The location that the {@link MotionEvent#ACTION_UP} occurred at
     * @return The velocity of the ball
     */
    public int getVelFromDis(int start, int end) {
        return (start - end) / 15;
    }

    /**
     * Spawns the ball.
     *
     * @param x    The x position of the ball on screen
     * @param y    The y position of the ball on screen
     * @param velX The ball's velocity on the x axis
     * @param velY The ball's velocity on the y axis
     */
    public void spawnBall(int x, int y, int velX, int velY) {
        recentBall = new Ball(x, y, velX, velY, currentColor);
        balls.add(recentBall);
    }

    /**
     * Applies velocity onto the frozen ball
     *
     * @param velX The ball's velocity on the x axis
     * @param velY The ball's velocity on the y axis
     */
    public void launchBall(int velX, int velY) {
        if (recentBall != null) {
            recentBall.velX = velX;
            recentBall.velY = velY;
        }
        if (SIMPLE_VIEW_ADAPTER.shouldPlaySound()) {
            if (sound != null) {
                sound.stop();
                sound.reset();
                sound.release();
            }
            sound = MediaPlayer.create(getContext(), R.raw.ball_spawn_effect);
            sound.start();
        }
    }

    public void clearTemp() {
        balls.remove(recentBall);
        recentBall = null;
        ballPredictions.clear();
    }

    //Code to improve the sound system
//    public void playSound() {
//        try {
//            sounds.add(MediaPlayer.create(getContext(), R.raw.ball_spawn_effect));
//            sounds.get(sounds.size() - 1).start();
//            isReset = false;
//            int i = sounds.size() - 1;
//            if (i == 10) {
//                resetSoundSystem();
//            }
//            Runnable runnable;
//            runnable = () -> {
//                if (!isReset) {
//                    sounds.get(i).stop();
//                    sounds.get(i).reset();
//                    sounds.get(i).release();
//                }
//            };
//            soundHandler.postDelayed(runnable, 2000);
//        } catch (Exception e) {
//            resetSoundSystem();
//        }
//    }

//    public void resetSoundSystem() {
//        isReset = true;
//        sounds.clear();
//        soundHandler = new Handler();
//    }


    @Override
    public void run() {
        while (isActive) {
            update();
            draw();
        }
    }

    /**
     * Updates the ball's position based on its velocity
     */
    public void update() {
        if (!hasStarted) {
            ArrayList<Integer> removes = new ArrayList<>();
            if (titleRand == 0) {
                if (previousTime == -1) {
                    TitleBall ball = new TitleBall(51, 51, 10, -7);
                    titleBalls.add(ball);
                    previousTime = System.currentTimeMillis();
                    return;
                }
                if ((System.currentTimeMillis() - previousTime) >= 500) {
                    TitleBall ball = new TitleBall(51, 51, 10, -7);
                    titleBalls.add(ball);
                    previousTime = System.currentTimeMillis();
                }
            } else if (titleRand == 1) {
                if ((System.currentTimeMillis() - previousTime) >= 300) {
                    TitleBall ball = new TitleBall(WIDTH - 51, 51, 10, HEIGHT / 2 / 100);
                    titleBalls.add(ball);

                    ball = new TitleBall(51, 51, -10, -HEIGHT / 2 / 100);
                    titleBalls.add(ball);

                    previousTime = System.currentTimeMillis();
                }
            }
            for (int i = 0; i < titleBalls.size(); i++) {
                TitleBall ball = titleBalls.get(i);
                ball.x += ball.velX;
                ball.y += ball.velY;

                if (ball.x + TitleBall.RADIUS >= WIDTH) {
                    ball.velX *= -1;
                }
                if (ball.y + TitleBall.RADIUS >= HEIGHT) {
                    removes.add(i);
                }
                if (ball.y - TitleBall.RADIUS <= 0) {
                    ball.velY *= -1;
                }
                if (ball.x - TitleBall.RADIUS <= 0) {
                    ball.velX *= -1;
                }
            }
            for (int i = 0; i < removes.size(); i++) {
                titleBalls.remove(removes.get(i).intValue());
            }
        }
        for (int i = 0; i < balls.size(); i++) {
            Ball ball = balls.get(i);
            ball.x += ball.velX;
            ball.y += ball.velY;

            if (ball.x + ball.RADIUS >= WIDTH) {
                ball.velX *= -1;
            }
            if (ball.y + ball.RADIUS >= HEIGHT) {
                ball.velY *= -1;
            }
            if (ball.y - ball.RADIUS <= 0) {
                ball.velY *= -1;
            }
            if (ball.x - ball.RADIUS <= 0) {
                ball.velX *= -1;
            }
        }
    }

    /**
     * Draws the balls onto the {@link SimpleView}
     */
    public void draw() {
        if (SURFACE_HOLDER.getSurface().isValid()) {
            Canvas canvas = SURFACE_HOLDER.lockCanvas();
            canvas.drawARGB(255, 255, 255, 255);
            for (int i = 0; i < balls.size(); i++) {
                Ball ball = balls.get(i);
                ball.draw(canvas, getContext());
            }
            if (!ballPredictions.isEmpty()) {
                for (int i = 0; i < ballPredictions.size(); i++) {
                    ballPredictions.get(i).draw(canvas, getContext());
                }
            }
            for (int i = 0; i < titleBalls.size(); i++) {
                titleBalls.get(i).draw(canvas, getContext());
            }
            SURFACE_HOLDER.unlockCanvasAndPost(canvas);
        }
    }

    public void onResume() {
        isActive = true;
        thread = new Thread(this);
        thread.start();
    }

    public void onPause() {
        isActive = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public byte getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(byte currentColor) {
        this.currentColor = currentColor;
    }

    public void setTrashActive(boolean trashActive) {
        isTrashActive = trashActive;
    }

    public interface SimpleViewAdapter {
        void onClickRelease();

        boolean shouldPlaySound();

        void removeTitle();
    }
}
