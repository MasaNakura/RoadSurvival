package com.example.roadsurvival;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Car {
    float x, y, width, height;
    private boolean actionDown = false;
    public Bitmap car;
    public int screenX;
    public boolean p1;
    public int pointerID;

    /**
     *
     * @param x x coordinate on the screen
     * @param y y coordinate on the screen
     * @param id The image "path" (e.g. R.drawable.car)
     * @param res resource
     */
    Car(int x, int y, int id, Resources res, int screenX, boolean player) {
        this.x = x;
        this.y = y;
        this.screenX = screenX;
        car = BitmapFactory.decodeResource(res, id);
        int pointerID = -1;
        width = car.getWidth();
        height = car.getHeight();
        // reduce the car's size
        float ratio = width/height;
        width = (float) (ratio * screenX * 0.15);
        height = (float) (screenX * 0.15);
        if(com.example.roadsurvival.GameActivity.versus) {
            p1 = player;
        }
        car = Bitmap.createScaledBitmap(car, (int) width, (int) height, false);
    }
    public void setActionDown(boolean actionDown, int ID) {
        pointerID = ID;
        this.actionDown = actionDown;
    }
    Rect getCollisionShape () {
        return new Rect((int)x, (int) (y + height * 0.1), (int)x + (int) (width*0.9), (int)y + (int)(height*0.9));
    }
    public int getID() {
        return pointerID;
    }

    public boolean getActionDown() {
        return actionDown;
    }

    public void setPosition(float x, float y, boolean versus) {
        if (!versus) {
            if (x >= width/2 && x <= screenX-width/2){
                this.x = x - width/2;
            }
        } else {
            if (p1) {
                if (x >= width/2 && x <= (int)(screenX/2)-(int)(width/7)){
                    this.x = x - width/2;
                }
            } else {
                if (x >= (int) (screenX/2) + (int)(width/7)&& x <= (int) screenX-width/2){
                    this.x = x - width/2;
                }
            }
        }
        this.y = y;
    }
}
