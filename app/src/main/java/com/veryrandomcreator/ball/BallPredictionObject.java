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
 * The class of the balls used in the prediction of where ball will travel.
 * Implements {@link SimpleObject} to efficiently call {@link #draw(Canvas, Context)} with other child classes.
 */
public class BallPredictionObject implements SimpleObject {
    public int x, y;
    public static final int RADIUS = 10;

    /**
     * Constructor of {@link BallPredictionObject}
     *
     * @param x The x location on screen for the ball to spawn at.
     * @param y The y location on screen for the ball to spawn at.
     */
    public BallPredictionObject(int x, int y) {
        this.x = x;
        this.y = y;
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
