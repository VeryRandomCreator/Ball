/*------------------------------------------------------------------------------
 Copyright (c) 2022-2023 VeryRandomCreator

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 -----------------------------------------------------------------------------*/

package com.veryrandomcreator.ball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

public interface SimpleObject {
    /**
     *
     * @param canvas The canvas to draw onto.
     * @param context Necessary to run {@link androidx.core.content.ContextCompat#getColor(Context, int)} to set {@link Paint} of {@link Canvas}
     */
    void draw(Canvas canvas, Context context);

    /**
     * Checks if an object at {@param x} and {@param y} with {@param width} and {@param height} intersects with object derived from {@link SimpleObject}
     *
     * @param x x pixel on screen of object
     * @param y y pixel on screen of object
     * @return boolean of whether or not object intersects object derived from {@link SimpleObject}
     */

    boolean intersects(int x, int y);

    /**
     * Retrieves width of object in pixels
     *
     * @return Width of object
     */
    int getWidth();

    /**
     * Retrieves height of object in pixels
     *
     * @return Height of object
     */
    int getHeight();
}
