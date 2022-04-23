package com.example.roadsurvival;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Obstacle {

    public int x, y, width, height;
    Bitmap image;
    float screenX;


    /*public Obstacle (int x, int y, int id, float screenX, float screenY, Resources res) {
        this(id, res, screenX);
        this.x = x;
        this.y = y;
    }*/

    // Only use if you intend to assign the other values later
    public Obstacle (int id, Resources res, float screenX, float screenY) {
        image = BitmapFactory.decodeResource(res, id);
        this.screenX = screenX;
        width = image.getWidth();
        height = image.getHeight();
        // reduce the obstacle's size
        float ratio = width/height;
        width = (int) (ratio * screenX * 0.16);
        height =  (int) (screenY * 0.2);

        image = Bitmap.createScaledBitmap(image, width, height, false);

    }

    public Rect getCollisionShape () {
        return new Rect((int)(x + width * 0.3), (int)(y + (height*0.4)), (int)(x + width*0.58), (int) (y + height*0.65));
    }

}
