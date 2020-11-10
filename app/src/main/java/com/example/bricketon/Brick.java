package com.example.bricketon;
import android.graphics.RectF;

public class Brick {
    RectF rect;
    int padding = 200;
    float yvelocity=100;
    int height=50;
    public Brick(int row,int column,int width,int height,int x){

        rect = new RectF(x +column*width ,row*height +padding,x+column*width +width,row*height+height+padding);
        padding=padding+200;
    }public RectF getRect(){
        return rect;
    }


}