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
 * The class of the different colored balls in-game.
 * Implements {@link SimpleObject} to efficiently call {@link #draw(Canvas, Context)} with other child classes.
 */
public class Ball implements SimpleObject {
    public static final byte SOFT_RED = 1;
    public static final byte SOFT_GREEN = 2;
    public static final byte SOFT_BLUE = 3;
    public static final byte SOFT_PURPLE = 4;
    public final int RADIUS = 50;

    public int x, y, velX, velY, color;

    /**
     * Constructor of {@link Ball}
     *
     * @param x The initial x location on screen for the ball to spawn at.
     * @param y The initial y location on screen for the ball to spawn at.
     * @param velX The speed the ball will travel on the x axis of the screen. (velX per time)
     * @param velY The speed the ball will travel on the y axis of the screen. (velY per time)
     * @param color The color of the ball, expressed as a byte in reference to the according color: {@link Ball#SOFT_RED}, {@link Ball#SOFT_GREEN}, {@link Ball#SOFT_BLUE}, or {@link Ball#SOFT_PURPLE}. The
     */
    public Ball(int x, int y, int velX, int velY, byte color) {
        this.x = x;
        this.y = y;
        this.velX = velX;
        this.velY = velY;
        this.color = color;
    }

    @Override
    public void draw(Canvas canvas, Context context) {
        Paint paint = new Paint();
        switch (color) {
            case SOFT_RED:
                paint.setColor(ContextCompat.getColor(context, R.color.soft_red));
                break;
            case SOFT_BLUE:
                paint.setColor(ContextCompat.getColor(context, R.color.soft_blue));
                break;
            case SOFT_GREEN:
                paint.setColor(ContextCompat.getColor(context, R.color.soft_green));
                break;
            case SOFT_PURPLE:
                paint.setColor(ContextCompat.getColor(context, R.color.soft_purple));
                break;
        }
        canvas.drawCircle(x, y, RADIUS, paint);
    }

    @Override
    public boolean intersects(int x, int y, int width, int height) {
        if (x + RADIUS >= x) {
            return true;
        }
        if (y+ RADIUS >= x) {
            return true;
        }
        if (y - RADIUS <= y + height) {
            return true;
        }
        return x - RADIUS <= x + width;
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
