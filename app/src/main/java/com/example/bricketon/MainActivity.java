package com.example.bricketon;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;


public class MainActivity extends Activity {
    BrickView brickview;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        brickview = new BrickView(this);
        setContentView(brickview);
    }
    class BrickView extends SurfaceView implements Runnable {
        Thread gamethread = null;
        SurfaceHolder ourholder;
        volatile boolean playing;
        boolean paused = true;
        Canvas can;
        Paint paint;
        long fps;
        private long timethisframe;
        int screenx;
        int screeny;
        Paddle paddle;
        Paddle1 paddle1;
        Ball ball;
        Brick[] bricks = new Brick[200];
        int numbrick = 0;
        SoundPool soundpool;
        int beep1ID = -1;
        int beep2ID = -1;
        int beep3ID = -1;
        int loselifeID = -1;
        int score1 = 0;
        int score2=0;
        int lives1 = 3;
        int lives2=3;

        public BrickView(Context context) {
            super(context);
            ourholder = getHolder();
            paint = new Paint();
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenx = size.x;
            screeny = size.y;
            paddle = new Paddle(screenx, screeny);
            paddle1 =new Paddle1(screenx,screeny);
            ball = new Ball(screenx, screeny);
            soundpool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            try {
                AssetManager assetManager = context.getAssets();
                AssetFileDescriptor descriptor;
                descriptor = assetManager.openFd("beep1.ogg");
                beep1ID = soundpool.load(descriptor, 0);
                descriptor = assetManager.openFd("beep2.ogg");
                beep2ID = soundpool.load(descriptor, 0);
                descriptor = assetManager.openFd("beep3.ogg");
                beep3ID = soundpool.load(descriptor, 0);
                descriptor = assetManager.openFd("loselife.ogg");
                loselifeID = soundpool.load(descriptor, 0);
            } catch (IOException e) {
                Log.e("error", "failed to load sound files");
            }
            restart();
        }

        public void restart() {
            ball.reset(screenx, screeny);
            int brickwidth = 50;
            int brickheight = 50;
            numbrick = 0;
            for (int column = 0; column < 1; column++) {
                for (int row = 0; row < 9; row+=4) {
                    bricks[numbrick] = new Brick(row, column, brickwidth, brickheight,screenx/2);
                    numbrick++;
                }
            }
            score1 = 0;
            score2=0;
            lives1=3;
            lives2=3;
        }

        @Override
        public void run() {
            while (playing) {
                long startframetime = System.currentTimeMillis();
                if (!paused) {
                    update();
                }
                draw();
                timethisframe = System.currentTimeMillis() - startframetime;
                if (timethisframe >= 1) {
                    fps = 1000 / timethisframe;
                }
            }
        }

        public void update() {
            paddle.update(fps);
            paddle1.update(fps);
            ball.update(fps);
            if (RectF.intersects(paddle.getRect(), ball.getRect())) {
                ball.randomyvelocity();
                ball.reversexvelocity();
                ball.clearobstaclex(paddle.getRect().left- 20);
                soundpool.play(beep1ID, 1, 1, 0, 0, 1);
            }
            if (RectF.intersects(paddle1.getRect(), ball.getRect())) {
                ball.randomyvelocity();
                ball.reversexvelocity();
                ball.clearobstaclex(paddle1.getRect().right+20);
                soundpool.play(beep1ID, 1, 1, 0, 0, 1);
            }
            if (ball.getRect().bottom > screeny-20) {
                ball.reverseyvelocity();
                ball.clearobstacley(screeny - 20 );
                soundpool.play(beep2ID, 1, 1, 0, 0, 1);
            }
            if (ball.getRect().top < 0) {
                ball.reverseyvelocity();
                ball.clearobstacley(20);
                soundpool.play(beep2ID, 1, 1, 0, 0, 1);
            }
            if (ball.getRect().left < 0) {
                ball.reversexvelocity();
                ball.clearobstaclex(20);
                lives1--;
                score2++;
                soundpool.play(loselifeID, 5, 5, 0, 0, 1);
                if (lives1 <= 0) {
                    paused = true;
                }
            }
            if (ball.getRect().right > screenx) {
                ball.reversexvelocity();
                ball.clearobstaclex(screenx-20);
                lives2--;
                score1++;
                soundpool.play(loselifeID, 5, 5, 0, 0, 1);
                if (lives2 <= 0) {
                    paused = true;
                }
            }

            for (int i = 0; i < numbrick; i++) {
                if (RectF.intersects(bricks[i].getRect(), ball.getRect())) {
                    ball.reverseyvelocity();
                    ball.reversexvelocity();
                    soundpool.play(beep2ID, 5, 5, 0, 0, 1);
                }

            }

        }


        public void draw() {
            if (ourholder.getSurface().isValid()) {
                can = ourholder.lockCanvas();
                can.drawColor(Color.argb(255, 204, 255, 255));
                paint.setColor(Color.argb(255, 255, 153, 51));
                can.drawRect(paddle.getRect(), paint);
                paint.setColor(Color.argb(255,255,0,255));
                can.drawRect(paddle1.getRect(),paint);
                paint.setColor(Color.argb(255, 0, 0, 255));
                can.drawRect(ball.getRect(), paint);
                paint.setColor(Color.argb(255, 252, 69, 3));
                for (int i = 0; i < numbrick; i++) {
                        can.drawRect(bricks[i].getRect(), paint);

                }
                paint.setColor(Color.argb(255, 0, 0, 0));
                paint.setTextSize(40);
                can.drawText("Score : " + score1 + " Lives : " + lives1, 10, 50, paint);
                can.drawText("Score : "+score2+" Lives : "+lives2,screenx-350,50,paint);
                if (lives1 <= 0) {
                    paint.setTextSize(90);
                    can.drawText("PLAYER 1 LOSE !", screenx/2, screeny/2, paint);

                }
                if(lives2 <= 0){
                    paint.setTextSize(90);
                    can.drawText("PLAYER 2 LOSE !",screenx/2, screeny/2,paint);

                }

                ourholder.unlockCanvasAndPost(can);
            }
        }

        public void pause() {
            playing = false;
            try {
                gamethread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }
        }

        public void resume() {
            playing = true;
            gamethread = new Thread(this);
            gamethread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionevent) {
            switch (motionevent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if(lives1!=0 &&  lives2!=0) {
                        paused = false;
                    }
                    if (motionevent.getX() >= screenx / 2 && motionevent.getY() >= screeny/2) {
                        paddle.setMovementState(paddle.BOTTOM);
                    }
                    if(motionevent.getX() >= screenx/2 && motionevent.getY() <=screeny/2){
                        paddle.setMovementState(paddle.TOP);
                    }
                    if(motionevent.getX() <= screenx/2 && motionevent.getY() >=screeny/2){
                        paddle1.setMovementState(paddle1.BOTTOM);
                    }
                    if(motionevent.getX() <= screenx/2 && motionevent.getY() <=screeny/2){
                        paddle1.setMovementState(paddle1.TOP);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    paddle.setMovementState(paddle.STOPPED);
                    paddle1.setMovementState(paddle1.STOPPED);
                    break;
            }
            return true;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        brickview.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        brickview.pause();
    }
}

