package com.example.bricketon;

import android.graphics.RectF;
public class Paddle1 {
    private RectF rect;
    private float length;
    private float height;
    private float x;
    private float y;
    private float paddlespeed;
    public final int STOPPED=0;
    public final int TOP=1;
    public final int BOTTOM=2;
    private int paddlemoving=STOPPED;
    public Paddle1(int screenx,int screeny){
        length=20;
        height=150;
        x=0;
        y=screeny/2;
        rect= new RectF(x,y,x+length,y+height);
        paddlespeed=500;
    }
    public RectF getRect(){
        return rect;
    }
    public void setMovementState(int state){
        paddlemoving=state;
    }
    public void update(long fps){
        if(paddlemoving == BOTTOM){
            y=y+(paddlespeed/fps);
        }
        if(paddlemoving == TOP){
            y=y-(paddlespeed/fps);
        }

        rect.top=y;
        rect.bottom=y+height;
    }


}
