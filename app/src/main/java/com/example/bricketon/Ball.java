package com.example.bricketon;

import android.graphics.RectF;
import java.util.Random;

public class Ball {
    RectF rect;
    float xvelocity;
    float yvelocity;
    float ballwidth=20;
    float ballheight=20;
    float x;
    float y;
    float speedx=10;
    float speedy=10;
    public Ball(int screenx,int screeny){
        xvelocity=500;
        yvelocity=-200;
        x=screenx/2;
        y=screeny/2;
        rect=new RectF(x,y,x+ballwidth,y+ballheight);
    }
    public RectF getRect(){
        return rect;
    }
    public void update(long fps){
        rect.left=rect.left+speedx+xvelocity/fps;
        rect.top=rect.top+speedy+yvelocity/fps;
        rect.right=rect.left+ballwidth;
        rect.bottom=rect.top-ballheight;
    }
    public void reverseyvelocity(){
        yvelocity=-yvelocity;
        speedy=-speedy;
    }
    public void reversexvelocity(){
        xvelocity=-xvelocity;
        speedx=-speedx;
    }
    public void randomyvelocity(){
        Random generator=new Random();
        int answer =generator.nextInt(2);
        if(answer==0){
            reverseyvelocity();
        }
    }
    public void clearobstacley(float y){
        rect.bottom=y;
        rect.top=y-ballheight;
    }
    public void clearobstaclex(float x){
        rect.left=x;
        rect.right=x+ballwidth;
    }
    public void reset(int x,int y){
        rect.left=x/2;
        rect.bottom=y/2-ballheight-20;
        rect.top=y/2-20;
        rect.right=x/2+ballwidth;
    }
}
