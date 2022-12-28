/*------------------------------------------------------------------------------
 Copyright (c) 2022 VeryRandomCreator

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 -----------------------------------------------------------------------------*/

package com.veryrandomcreator.ball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

/**
 * The class of the balls used in the title screen, with pre-set movements and characteristics.
 * Implements {@link SimpleObject} to efficiently call {@link #draw(Canvas, Context)} with other child classes.
 */
public class TitleBall implements SimpleObject {
    public int x, y, velX, velY;
    public static final int RADIUS = 50;

    /**
     * Constructor of {@link BallPredictionObject}
     *
     * @param x The initial x location on screen for the ball to spawn at.
     * @param y The initial y location on screen for the ball to spawn at.
     * @param velX The speed the ball will travel on the x axis of the screen. (velX per time)
     * @param velY The speed the ball will travel on the y axis of the screen. (velY per time)
     */
    public TitleBall(int x, int y, int velX, int velY) {
        this.x = x;
        this.y = y;
        this.velX = velX;
        this.velY = velY;
    }

    @Override
    public void draw(Canvas canvas, Context context) {
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.soft_red));
        canvas.drawCircle(x, y, RADIUS, paint);
    }

    @Override
    public boolean intersects(int x, int y, int width, int height) {
        return false;
    }

    @Override
    public int getWidth() {
        return RADIUS * 2;
    }

    @Override
    public int getHeight() {
        return RADIUS * 2;
    }
}
