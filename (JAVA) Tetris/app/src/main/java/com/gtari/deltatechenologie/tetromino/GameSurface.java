package com.gtari.deltatechenologie.tetromino;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Random;

import static java.lang.Math.abs;


public class GameSurface extends SurfaceView  implements SurfaceHolder.Callback,Runnable {

    int M = 20;
    int N = 10;
    public int canvasWidth=0;
    public int canvasHeight=0;

    private Thread gameThread;
    private final SurfaceHolder ourHolder;
    private Canvas canvas;
    private Bitmap bitmapRunningMan;
    private Bitmap bitmapBackground;
    private Bitmap bitmapFiledOfGame;
    private Bitmap bitmapMagicStar;
    private Bitmap bitmapMagicStarCount;
    private Bitmap bitmapAskForHelp;
    private Bitmap bitmapColorBomb;
    private Bitmap bitmapMemoire;
    private Bitmap bitmapRoyaleGift;
    private Bitmap bitmapJokerCards;

    private float manXPos = 10, manYPos = 10;
    private int frameWidth, frameHeight;
    private int shiftHorizontal=4,shiftVertical=3;
    private int frameCount = 8;
    private long lastFrameChangeTime = 0;

    private Rect frameToDraw ;
    private Rect frameToDrawMemoire ;
    private RectF whereToDraw ;
    private Rect frameColorToDraw = new Rect(0, 0, 200, 200);
    private Rect frameJokerCardsToDraw = new Rect(0, 0, 158, 500);
    private RectF whereToDrawField ;

    public int dx=0; boolean rotate=false;
    int colorNum=1;
    int delay=500;
    int addDelay=500;

    public volatile boolean isPlaying=true;
    private boolean isNeedMagicHelp=false;
    private boolean  isWonRoyaleGift=false;
    public boolean isGameOver=false;
    private boolean isStratDetect=false;

    // Sound Effect
    private MediaPlayer explosionSound;
    private MediaPlayer magicSound;
    private MediaPlayer emptyFieldSound;
    private MediaPlayer goSound;
    private MediaPlayer readySound;
    private MediaPlayer perfectSound;


    //My variables Tetris Game
    int Score=0;

    private class Point{
        float x;
        float y;
    }
    Point[] a = new Point[4];
    Point[] b = new Point[4];
    Point[] m = new Point[4];
    Point s = new Point();

    int figures[][] =
            {
                    {1,3,5,7}, // I
                    {2,4,5,7}, // Z
                    {3,5,4,6}, // S
                    {3,5,4,7}, // T
                    {2,3,5,7}, // L
                    {3,5,7,6}, // J
                    {2,3,4,5}  // O
            };

    Random rand;
    Paint paint;
    TextPaint textPaint;
    int n;
    int memoire,colorNumMemoire;
    int positionY,positionX;
    Context mContext;


    public GameSurface(Context context) {
        super(context);
        this.mContext=context;
        ourHolder = this.getHolder();
        ourHolder.addCallback(this);
        ourHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        bitmapFiledOfGame = BitmapFactory.decodeResource(getResources(), R.drawable.branche_wood_background_min);

        bitmapBackground = BitmapFactory.decodeResource(getResources(), R.drawable.wooden_background_and_banner_min_a);
        bitmapMagicStar = BitmapFactory.decodeResource(getResources(), R.drawable.star_jump);
        bitmapMagicStarCount = BitmapFactory.decodeResource(getResources(), R.drawable.star_count);
        bitmapAskForHelp = BitmapFactory.decodeResource(getResources(), R.drawable.ask_for_help);
        bitmapMemoire = BitmapFactory.decodeResource(getResources(), R.drawable.memoire_background_a);

        bitmapFiledOfGame = Bitmap.createScaledBitmap(bitmapFiledOfGame,310 , 520, true);
        bitmapColorBomb = BitmapFactory.decodeResource(getResources(), R.drawable.explode_green);
        bitmapColorBomb = Bitmap.createScaledBitmap(bitmapColorBomb,200*4,200*4, false);

        bitmapJokerCards = BitmapFactory.decodeResource(getResources(), R.drawable.joker_cards_anim);
        bitmapJokerCards = Bitmap.createScaledBitmap(bitmapJokerCards,158*4,500*4, false);

        //intialise game Objects
        rand=new Random();
        paint=new Paint();
        paint.setColor(0xFF96661d);
        paint.setFakeBoldText(true);
        paint.setTextSize(5.0f);


        a[0]= new Point();a[1]= new Point();a[2]= new Point();a[3]= new Point();
        b[0]= new Point();b[1]= new Point();b[2]= new Point();b[3]= new Point();
        m[0]= new Point();m[1]= new Point();m[2]= new Point();m[3]= new Point();


        n=abs(rand.nextInt())%7;
        colorNum=1+abs(rand.nextInt())%7;
        if(a[0].x==0)
            for(int i=0;i<4;i++){
                a[i].x=figures[n][i]%2+N/2-1;
                a[i].y=figures[n][i]/2;
            }

        memoire=abs(rand.nextInt())%7;
        colorNumMemoire=1+abs(rand.nextInt())%7;
        for(int j=0;j<4;j++){
            m[j].x=figures[memoire][j]%2;
            m[j].y=figures[memoire][j]/2;
        }


        // Load Sounds effect varialbls
        explosionSound=MediaPlayer.create(mContext,R.raw.explosion);
        magicSound=MediaPlayer.create(mContext,R.raw.ask_magic_help);
        emptyFieldSound=MediaPlayer.create(mContext,R.raw.empty_field);
        goSound=MediaPlayer.create(mContext,R.raw.go);
        perfectSound=MediaPlayer.create(mContext,R.raw.perfect);


        //Launch Start Game Dialog
        LaunchGameDialog();
    }



    public GameSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext=context;
        ourHolder = this.getHolder();
        ourHolder.addCallback(this);
        ourHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        bitmapFiledOfGame = BitmapFactory.decodeResource(getResources(), R.drawable.branche_wood_background_min);

        bitmapBackground = BitmapFactory.decodeResource(getResources(), R.drawable.wooden_background_and_banner_min_a);
        //bitmapBackground = Bitmap.createScaledBitmap(bitmapBackground, getWidth(), getHeight(), true);

        bitmapMagicStar = BitmapFactory.decodeResource(getResources(), R.drawable.star_jump);
        bitmapMagicStarCount = BitmapFactory.decodeResource(getResources(), R.drawable.star_count);
        bitmapAskForHelp = BitmapFactory.decodeResource(getResources(), R.drawable.ask_for_help);
        bitmapMemoire = BitmapFactory.decodeResource(getResources(), R.drawable.memoire_background_a);



        bitmapFiledOfGame = Bitmap.createScaledBitmap(bitmapFiledOfGame,310 , 520, true);
        bitmapColorBomb = BitmapFactory.decodeResource(getResources(), R.drawable.explode_green);
        bitmapColorBomb = Bitmap.createScaledBitmap(bitmapColorBomb,200*4,200*4, false);

        bitmapJokerCards = BitmapFactory.decodeResource(getResources(), R.drawable.joker_cards_anim);
        bitmapJokerCards = Bitmap.createScaledBitmap(bitmapJokerCards,158*4,500*4, false);


        //intialise game Objects
        rand=new Random();
        paint=new Paint();
        paint.setColor(0xFF96661d);
        paint.setFakeBoldText(true);
        paint.setTextSize(5.0f);



        a[0]= new Point();a[1]= new Point();a[2]= new Point();a[3]= new Point();
        b[0]= new Point();b[1]= new Point();b[2]= new Point();b[3]= new Point();
        m[0]= new Point();m[1]= new Point();m[2]= new Point();m[3]= new Point();


        n=abs(rand.nextInt())%7;
        colorNum=1+abs(rand.nextInt())%7;
        if(a[0].x==0)
            for(int i=0;i<4;i++){
                a[i].x=figures[n][i]%2+N/2-1;
                a[i].y=figures[n][i]/2;
            }

        memoire=abs(rand.nextInt())%7;
        colorNumMemoire=1+abs(rand.nextInt())%7;
        for(int j=0;j<4;j++){
            m[j].x=figures[memoire][j]%2;
            m[j].y=figures[memoire][j]/2;
        }


        // Load Sounds effect varialbls
        explosionSound=MediaPlayer.create(mContext,R.raw.explosion);
        magicSound=MediaPlayer.create(mContext,R.raw.ask_magic_help);
        emptyFieldSound=MediaPlayer.create(mContext,R.raw.empty_field);
        goSound=MediaPlayer.create(mContext,R.raw.go);
        perfectSound=MediaPlayer.create(mContext,R.raw.perfect);


        //Launch Start Game Dialog
        LaunchGameDialog();
    }


    private int shift_fog;
    private int shift_fog1;
    private int shift_fog2;
    private int shift_fog3;

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        canvasWidth=getWidth();
        canvasHeight=getHeight();

        frameWidth = Math.round(2.65f*getHeight()/100);
        frameHeight = frameWidth;

        frameToDraw = new Rect(0, 0, frameWidth, frameHeight);
        frameToDrawMemoire = new Rect(0, 0, frameWidth, frameHeight);
        whereToDraw = new RectF(manXPos, manYPos, manXPos + frameWidth, frameHeight);

        bitmapRunningMan = BitmapFactory.decodeResource(getResources(), R.drawable.tiles);
        bitmapRunningMan = Bitmap.createScaledBitmap(bitmapRunningMan, frameWidth * frameCount, frameHeight, false);

        textPaint=new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(spToPx(16));
        //Math.round(2.08f*getWidth()/100)
        textPaint.setTypeface(Typeface.create("cursive", Typeface.BOLD));
        textPaint.setColor(0xFF19C5F1);//0xFF633912);

        shift_fog=frameWidth*4;//Math.round(16.66f*getWidth()/100);
        shift_fog1=Math.round(7.95f*getHeight()/100);
        shift_fog2=Math.round(75f*getWidth()/100);
        shift_fog3=Math.round(26.23f*getHeight()/100);

        whereToDrawField = new RectF(frameWidth*2,//Math.round(8.33f*getWidth()/100),
                -Math.round(2f*getWidth()/100),
                frameWidth*(N+4)+frameWidth*2,//+Math.round(8.33f*getWidth()/100),
                shift_fog1 + frameHeight * (M+3));

        // Game Thread
        gameThread = new Thread(this);
        gameThread.start();
    }


    private int spToPx(int sp) {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        float density = metrics.density;
        double px;
        double a=-0.0034375, b=0.0075;
        px=sp*(a*height+b*width);

        return Math.round((float) px *density);
    }


    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
      if(isGameOver)
        for(int i=0;i<M;i++)
            for (int j=0;j<N;j++)
                      setField(i,j,0);
    }

    int count=1 ;
    long changeFrame=0;
    long changeFrame1=0;
    long newTime2=0;
    int v=-1,w=0;
    int v1=4/*-1,4*/,w1=0;

    private boolean isStart=false;
    //Shift background memoire, buzzel memoire, score, stars counter
       private int x1=0,y1=0;
       private long time1 = 0;
       private long time2=0;
       private boolean isTestVideoReward=false;




    @Override
        public void run() {
        RectF whereToDrawBackground=new RectF(0,0,getWidth(),getHeight());

        if (ourHolder.getSurface().isValid()) {
            bitmapBackground = Bitmap.createScaledBitmap(bitmapBackground, getWidth(), getHeight(), false);

            while (isPlaying) {

                // Move Puzzle
                for (int i = 0; i < 4; i++) {
                    b[i] = a[i];
                    a[i].x += dx;
                }
                positionX+=dx;
                if (!check())
                    System.arraycopy(b, 0, a, 0, 4);
                //////Rotate//////
                if (rotate && n != 6) {
                    Point p = a[1]; //center of rotation
                    for (int i = 0; i < 4; i++) {
                        int x = (int) (a[i].y - p.y);
                        int y = (int) (a[i].x - p.x);
                        a[i].x = p.x - x;
                        a[i].y = p.y + y;
                    }
                    if (!check())
                        System.arraycopy(b, 0, a, 0, 4);
                }

                // Clock System
                long time = System.currentTimeMillis();
                if (time > lastFrameChangeTime + delay && !isAskedForHelp) {
                    lastFrameChangeTime = time;
                    for (int i = 0; i < 4; i++) {
                        b[i] = a[i];
                        a[i].y += 1;
                    }
                    positionY++;

                    if (!check()) {
                        for (int i = 0; i < 4; i++)
                            setField((int) b[i].y - 1, (int) b[i].x, colorNum);

                        if(nbreOfCards==0){ isTestVideoReward=true;nbreOfCards=-1;}

                        colorNum = colorNumMemoire;
                        n = memoire;

                        memoire = abs(rand.nextInt()) % 7;
                        colorNumMemoire = 1 + abs(rand.nextInt()) % 7;
                        frameToDrawMemoire.left = colorNumMemoire * frameWidth;
                        frameToDrawMemoire.right = frameToDrawMemoire.left + frameWidth;

                        for (int i = 0; i < 4; i++) {
                            a[i].x = figures[n][i] % 2 + N / 2 - 1;
                            a[i].y = figures[n][i] / 2;

                            m[i].x = figures[memoire][i] % 2;
                            m[i].y = figures[memoire][i] / 2;
                        }


                        if (delay != addDelay) delay = addDelay;

                        positionY = 0;
                        positionX = 0;
                        isNotClickedOnRoulette = true;
                    }
                }
                if (time > lastFrameChangeTime + delay && isAskedForHelp) {
                    lastFrameChangeTime = time;

                    // Magic Help
                    if (!checkForMagicHelp()) {
                        isAskedForHelp = false;

                        for (int i = 0; i < M - 3; i++)
                            for (int j = 0; j < N; j++)
                                setField(i, j, 0);
                        for (int i = 1; i < N - 1; i++) setField(M - 3, i, 0);
                        for (int i = 3; i < N - 3; i++) setField(M - 2, i, 0);


                        delay = addDelay;
                        for (int i = 0; i < 4; i++) {
                            a[i].x = figures[n][i] % 2 + N / 2 - 1;
                            a[i].y = figures[n][i] / 2;
                        }

                        v = 3;
                        explosionSound.start();

                    } else
                        s.y += 1;
                }


                //Check lines

                int k = M - 1;
                for (int i = M - 1; i > 0; i--) {
                    int count = 0;
                    for (int j = 0; j < N; j++) {
                        if (field(i, j) != 0) count++;
                        setField(k, j, field(i, j));
                    }
                    if (count < N) k--;
                    else {
                        //Score++;
                        if(addDelay<150)addDelay=400;
                        addDelay=addDelay-10;
                        for (int s = 0; s < N; s++) Score+=field(k, s);
                    }
                }

                //Detect Royale Gift
                int sum1 = 0;
                if (isStratDetect) {
                    for (int p = 0; p < N; p++)
                        sum1 += field(M - 1, p);
                    //setField(M,p,5);
                    isWonRoyaleGift = sum1 == 0;
                }

                for (int p = 0; p < N; p++)
                    sum1 += field(M - 1, p);
                //setField(M,p,5);
                isStratDetect = sum1 != 0;


                //Game Over Detect
                int sum = 0;
                if (!check())
                    for (int i = 0; i < N; i++)
                        if (field(1, i) != 0) {
                            isGameOver = true;
                            isNeedMagicHelp = false;
                        }


                // Ask for magic Help

                if (!endHelp)
                    for (int i = 0; i < N; i++) sum += field(9, i);
                isNeedMagicHelp = sum != 0;


                if (isNeedMagicHelp && !isStart) {
                    magicSound.start();
                    isStart = true;
                }
                if (!isNeedMagicHelp) isStart = false;

                dx = 0;
                rotate = false;


                canvas = ourHolder.lockCanvas();
                if(canvas.isOpaque()) {
                    canvas.drawBitmap(bitmapBackground, null, whereToDrawBackground, null);
                 //   onDraw(canvas);
                    drawFieldOfGame();
                    drawScoreStarsCounterNextPuzzle();
                    drawColorBomb();
                    drawMoreJokerCards();
                }


                //  Draw Star Asking for Magic Help
                if (isNeedMagicHelp && !endHelp) {

                    whereToDraw.set((int) Math.round(65.27f*getWidth()/100),
                            (int) Math.round(45.05f*getHeight()/100),
                            (int) Math.round(95.83f*getWidth()/100),
                            (int) Math.round(64.48f*getHeight()/100));
                    canvas.drawBitmap(bitmapAskForHelp, null, whereToDraw, null);
                }

                // Royale Gift Animation
                    newTime2 = System.currentTimeMillis();
                    whereToDraw.set(frameWidth * (shiftHorizontal - 3), frameHeight * (shiftVertical),
                            frameWidth * (shiftHorizontal - 3) + Math.round(76.38f*getWidth()/100), frameHeight * (shiftVertical) + Math.round(61.83f*getHeight()/100));
                    if (isWonRoyaleGift) {
                        emptyFieldSound.start();
                        perfectSound.setVolume(Float.parseFloat(getSoundVolumeDB()),Float.parseFloat(getSoundVolumeDB()));
                        perfectSound.start();
                        bitmapRoyaleGift = BitmapFactory.decodeResource(getResources(), R.drawable.royale_gift);
                        canvas.drawBitmap(bitmapRoyaleGift, null, whereToDraw, null);
                    }


                    ourHolder.unlockCanvasAndPost(canvas);

                    // Cancel Royale Gift Animation
                    if (isWonRoyaleGift) {
                        while (time1 < newTime2 + 2000) {
                            time1 = System.currentTimeMillis();
                        }
                        Score=Score*2;
                        isWonRoyaleGift = false;

                        if (bitmapRoyaleGift != null) {
                            bitmapRoyaleGift.recycle();
                            bitmapRoyaleGift = null;
                        }
                    }


                    if (!isReady) isPlaying = false;
                }
            }
        }

           //Draw Field of Game
        private void drawFieldOfGame(){

            canvas.drawBitmap(bitmapFiledOfGame, null, whereToDrawField, null);
            for (int i = 0; i <= M; i++)
                canvas.drawLine(shift_fog, shift_fog1 + frameHeight * i, frameWidth * N + shift_fog, shift_fog1 + frameHeight * i, paint);


            for (int i = 0; i <= N; i++)
                canvas.drawLine(frameWidth * i +shift_fog, shift_fog1, frameWidth * i + shift_fog, shift_fog1 + frameHeight * M, paint);


            // Draw Field
            for (int i = 0; i < M; i++)
                for (int j = 0; j < N; j++) {
                    if (field(i, j) == 0) continue;

                    frameToDraw.left = field(i, j) * frameWidth;
                    frameToDraw.right = frameToDraw.left + frameWidth;

                    whereToDraw.set(shift_fog + j * frameWidth, shift_fog1 + i * frameHeight, shift_fog+ j * frameWidth + frameWidth, shift_fog1 + i * frameHeight + frameHeight);
                    canvas.drawBitmap(bitmapRunningMan, frameToDraw, whereToDraw, null);
                }


            if (!isAskedForHelp) {
                frameToDraw.left = colorNum * frameWidth;
                frameToDraw.right = frameToDraw.left + frameWidth;
                for (int i = 0; i < 4; i++) {
                    whereToDraw.set((int) shift_fog + a[i].x * frameWidth, (int) shift_fog1 + a[i].y * frameHeight, (int) shift_fog + a[i].x * frameWidth + frameWidth, (int) shift_fog1 + a[i].y * frameHeight + frameHeight);
                    canvas.drawBitmap(bitmapRunningMan, frameToDraw, whereToDraw, null);
                }
            } else {
                //Draw Magic Stars in the field
                whereToDraw.set((int) shift_fog + frameWidth*(s.x-1), (int) shift_fog1 + frameHeight*s.y, (int) shift_fog + frameWidth*(s.x + 1), (int) shift_fog1 +frameHeight*( s.y + 2));
                canvas.drawBitmap(bitmapMagicStar, null, whereToDraw, null);
            }

        }


    // Draw background Score + Magic Stars Count + Next Puzzle
    private void drawScoreStarsCounterNextPuzzle(){
            whereToDraw.set(Math.round(69.16f*getWidth()/100) + x1,
                    Math.round(13.25f*getHeight()/100)+ y1,
                    Math.round(91.38f*getWidth()/100) + x1,
                    Math.round(15.9f*getHeight()/100) + frameHeight * 7
                    + Math.round(1f*getHeight()/100)  + Math.round(2.65f*getHeight()/100)+Math.round(3f*getHeight()/100)+ y1);//Math.round(38.42f*getHeight()/100)
            canvas.drawBitmap(bitmapMemoire, null, whereToDraw, null);

        // Draw Score Text
            canvas.drawText(String.valueOf("SCORE"), shift_fog2 -Math.round(1f*getHeight()/100)+ x1,Math.round(16f*getHeight()/100) + frameHeight * 3 + Math.round(1f*getHeight()/100) + y1, textPaint);//shift_fog3
            canvas.drawText(String.valueOf(Score), shift_fog2 -Math.round(1f*getHeight()/100) + x1, Math.round(16f*getHeight()/100) + frameHeight * 3 + Math.round(1f*getHeight()/100)  + Math.round(3f*getHeight()/100)+ y1, textPaint);//shift_fog3

        // Next puzzel
            for (int i = 0; i < 4; i++) {
                whereToDraw.set((int) m[i].x * frameWidth + shift_fog2 + x1,
                        (int) m[i].y * frameHeight + Math.round(15.9f*getHeight()/100) + frameHeight * 3 + Math.round(1f*getHeight()/100)  + Math.round(2.65f*getHeight()/100)+ y1+Math.round(1.5f*getHeight()/100), //shift_fog3 + Math.round(0.706f*getHeight()/100)
                        (int) m[i].x * frameWidth + frameWidth + shift_fog2 + x1,
                        (int) m[i].y * frameHeight + frameHeight +Math.round(15.9f*getHeight()/100) + frameHeight * 3 + Math.round(1f*getHeight()/100)  + Math.round(2.65f*getHeight()/100)+Math.round(1.5f*getHeight()/100) + y1); //+shift_fog3 + Math.round(0.706f*getHeight()/100)
                canvas.drawBitmap(bitmapRunningMan, frameToDrawMemoire, whereToDraw, null);
            }

        // Magic Stars Counter
            for (int i = 0; i < nbrMagicHelp; i++) {
                whereToDraw.set((int) frameWidth*i + Math.round(72.22f*getWidth()/100) + x1,
                        (int)  Math.round(15.9f*getHeight()/100) + y1,
                        (int) frameWidth*i + Math.round(72.22f*getWidth()/100)+ frameWidth * 2  + x1,
                        (int) Math.round(15.9f*getHeight()/100) + frameHeight * 2 + y1);
                canvas.drawBitmap(bitmapMagicStarCount, null, whereToDraw, null);
            }
        }


    //Color Bombe Frames (Animation)
    private  void drawColorBomb(){
         whereToDraw.set(shift_fog - Math.round(20.83f*getWidth()/100),
                 shift_fog1 + frameHeight,
                 shift_fog - Math.round(20.83f*getWidth()/100) + Math.round(83.33f*getWidth()/100),
                 shift_fog1 + Math.round(2.65f*getHeight()/100) + Math.round(61.83f*getHeight()/100));

         if (v >= 0) {
             while (time1 < changeFrame + 65) {
                 time1 = System.currentTimeMillis();
             }
             changeFrame = time1;
             frameColorToDraw.left = w * 200;
             frameColorToDraw.right = frameColorToDraw.left + 200;
             frameColorToDraw.top = 200 * v;
             frameColorToDraw.bottom = frameColorToDraw.top + 200;
             canvas.drawBitmap(bitmapColorBomb, frameColorToDraw, whereToDraw, null);

             w++;
             if (w > 3) v--;
             w = w % 4;
             // if (v<0) v = 3;
         }
     }

    /// Draw Pack of cards More Joker Cards Animation
    private void drawMoreJokerCards(){
         whereToDraw.set(shift_fog + Math.round(4.16f*getWidth()/100),
                 shift_fog1 + Math.round(22.96f*getHeight()/100),
                 shift_fog + Math.round(33.32f*getWidth()/100),
                 shift_fog1 + Math.round(75.97f*getHeight()/100));

         if (v1 <= 3) {
             while (time2 < changeFrame1 + 150) {
                 time2 = System.currentTimeMillis();
             }
             changeFrame1 = time2;
             frameJokerCardsToDraw.left = w1 * 158;
             frameJokerCardsToDraw.right = frameJokerCardsToDraw.left + 158;
             frameJokerCardsToDraw.top = 500 * v1;
             frameJokerCardsToDraw.bottom = frameJokerCardsToDraw.top + 500;
             canvas.drawBitmap(bitmapJokerCards, frameJokerCardsToDraw, whereToDraw, null);

             w1++;
             if (w1 > 3) v1++;
             //v1 = v1 % 4;
             w1 = w1 % 4;
         }
     }



    private boolean check()
        {
            for (int i=0;i<4;i++) {
                if (a[i].x < 0 || a[i].x >= N || a[i].y >= M) return false;
                else if (field((int) a[i].y, (int) a[i].x) != 0) return false;
            }
            return true;
        };


    private boolean checkForMagicHelp()
    {
        if (s.y >= M-1) return false;
        else if (field((int) s.y-1, (int) s.x) != 0) return false;

        return true;
    }

        public int getScore(){
            return Score;
         }



    //Add native Class C++
    static {
        System.loadLibrary("native-lib");
    }

    //Add native Methode
    public native int field(int i,int j);
    public native void setField(int i,int j,int colorNum);


    public void pause() {
        isPlaying = false;

        try {
            gameThread.join();
        } catch(InterruptedException e) {
            Log.e("ERR", "Joining Thread");
        }

    }


    public void resume() {
        if(isReady) {
            isPlaying = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    public void finish() {

        if (bitmapRunningMan != null) {
             bitmapRunningMan.recycle();
             bitmapRunningMan = null;}
        if (bitmapBackground != null) {
            bitmapBackground.recycle();
            bitmapBackground = null;}
        if (bitmapFiledOfGame != null) {
            bitmapFiledOfGame.recycle();
            bitmapFiledOfGame = null;}
        if (bitmapMagicStar != null) {
            bitmapMagicStar.recycle();
            bitmapMagicStar = null;}
        if (bitmapMagicStarCount != null) {
            bitmapMagicStarCount.recycle();
            bitmapMagicStarCount = null;}
        if (bitmapAskForHelp != null) {
            bitmapAskForHelp.recycle();
            bitmapAskForHelp = null;}
        if (bitmapColorBomb != null) {
            bitmapColorBomb.recycle();
            bitmapColorBomb = null;}
        if (bitmapMemoire != null) {
            bitmapMemoire.recycle();
            bitmapMemoire = null;}
        if (bitmapRoyaleGift != null) {
            bitmapRoyaleGift.recycle();
            bitmapRoyaleGift = null;}
        if (bitmapJokerCards != null) {
            bitmapJokerCards.recycle();
            bitmapJokerCards = null;}
    }

    public void setSpeed(int speed){delay=speed;}

    private int nbreOfCards=3;
    boolean isNotClickedOnRoulette=true;
    public int Roulette(){

        if(nbreOfCards<0)return -1;

        if(isNotClickedOnRoulette) {
            nbreOfCards--;
            isNotClickedOnRoulette=false;
            if(nbreOfCards<0)return -1;
        }
        n++;
        n=n%7;
        for(int i=0;i<4;i++) {
            a[i].x=figures[n][i]%2+N/2-1+positionX;
            a[i].y=figures[n][i]/2+positionY;
        }
        return nbreOfCards;
    }

    public boolean getIsShowRewardVideoBt(){
        return isTestVideoReward;
    }
    public void setIsShowRewardVideoBt(boolean b){
        isTestVideoReward=b;
    }

    public int getNbreOfCards(){
        return nbreOfCards;
    }

    public void addNbreOfCards(){
      nbreOfCards+=6;
        v1=0;
        // Load New puzzel
        a[0]= new Point();a[1]= new Point();a[2]= new Point();a[3]= new Point();
        b[0]= new Point();b[1]= new Point();b[2]= new Point();b[3]= new Point();
        m[0]= new Point();m[1]= new Point();m[2]= new Point();m[3]= new Point();

        n=abs(rand.nextInt())%7;
        colorNum=1+abs(rand.nextInt())%7;
        if(a[0].x==0)
            for(int i=0;i<4;i++){
                a[i].x=figures[n][i]%2+N/2-1;
                a[i].y=figures[n][i]/2;
            }

        memoire=abs(rand.nextInt())%7;
        colorNumMemoire=1+abs(rand.nextInt())%7;
        for(int j=0;j<4;j++){
            m[j].x=figures[memoire][j]%2;
            m[j].y=figures[memoire][j]/2;
        }

        positionX=0;
        positionY=0;
    }

    private boolean isAskedForHelp=false;
    public void MagicHelp(){

        s.x=figures[n][0]%2+N/2-1;
        s.y=figures[n][0]/2;
        isAskedForHelp=true;
        delay=100;
    }


    public boolean getMagicHelpSate(){
        return isNeedMagicHelp;
    }
    private int nbrMagicHelp=3;
    private boolean endHelp=false;
    public void NbreOfMagicHelp(){
        nbrMagicHelp--;
        endHelp=nbrMagicHelp<=0;
    }

    //  Start the Game Dialog
    private boolean isReady = false;


    private void LaunchGameDialog(){
        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.launch_game_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        TextView hightScoreTxt=(TextView)dialog.findViewById(R.id.idHightScoreText);
        hightScoreTxt.setText(getScoreDB());
        dialog.show();
        readySound=MediaPlayer.create(mContext,R.raw.ready);
        readySound.setVolume(Float.parseFloat(getSoundVolumeDB()),Float.parseFloat(getSoundVolumeDB()));
        readySound.start();

        dialog.findViewById(R.id.idGo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                isReady = true;
                resume();
                readySound.stop();
                readySound.release();
                goSound.setVolume(Float.parseFloat(getSoundVolumeDB()),Float.parseFloat(getSoundVolumeDB()));
                goSound.start();
            }

        });
    }


    // Game Over
    public boolean GameOver(){
        return isGameOver;
    }


    // Launch New Game
    public void RestartNewGame(){
        for(int i=0;i<M;i++)
            for (int j=0;j<N;j++)
                setField(i,j,0);

        nbrMagicHelp=3;
        nbreOfCards=3;
        Score=0;
        isStratDetect=false;
        isReady=false;
        delay=500;
        addDelay=500;

        // Load New puzzel
        a[0]= new Point();a[1]= new Point();a[2]= new Point();a[3]= new Point();
        b[0]= new Point();b[1]= new Point();b[2]= new Point();b[3]= new Point();
        m[0]= new Point();m[1]= new Point();m[2]= new Point();m[3]= new Point();

        n=abs(rand.nextInt())%7;
        colorNum=1+abs(rand.nextInt())%7;
        if(a[0].x==0)
            for(int i=0;i<4;i++){
                a[i].x=figures[n][i]%2+N/2-1;
                a[i].y=figures[n][i]/2;
            }

        memoire=abs(rand.nextInt())%7;
        colorNumMemoire=1+abs(rand.nextInt())%7;
        for(int j=0;j<4;j++){
            m[j].x=figures[memoire][j]%2;
            m[j].y=figures[memoire][j]/2;
        }

        positionX=0;
        positionY=0;

        LaunchGameDialog();
    }


    // Clean game and Quit
    public void CleanGame(){
        for(int i=0;i<M;i++)
            for (int j=0;j<N;j++)
                setField(i,j,0);

        nbrMagicHelp=3;
        nbreOfCards=3;
        Score=0;
        isStratDetect=false;
        isReady=false;
        delay=500;
        addDelay=500;

        // Load New puzzle
        a[0]= new Point();a[1]= new Point();a[2]= new Point();a[3]= new Point();
        b[0]= new Point();b[1]= new Point();b[2]= new Point();b[3]= new Point();
        m[0]= new Point();m[1]= new Point();m[2]= new Point();m[3]= new Point();

        n=abs(rand.nextInt())%7;
        colorNum=1+abs(rand.nextInt())%7;
        if(a[0].x==0)
            for(int i=0;i<4;i++){
                a[i].x=figures[n][i]%2+N/2-1;
                a[i].y=figures[n][i]/2;
            }

        memoire=abs(rand.nextInt())%7;
        colorNumMemoire=1+abs(rand.nextInt())%7;
        for(int j=0;j<4;j++){
            m[j].x=figures[memoire][j]%2;
            m[j].y=figures[memoire][j]/2;
        }
        positionX=0;
        positionY=0;

    }


    // Load Score
    private String getScoreDB(){
        SharedPreferences perfers=getContext().getSharedPreferences("Settings", Activity.MODE_PRIVATE);

        if(!perfers.contains("High_Score")) {
            return "0";
        }
        return perfers.getString("High_Score","0");
    }
    // Load Sound Volume from Database
    private String getSoundVolumeDB(){
        SharedPreferences perfers=getContext().getSharedPreferences("Settings", Activity.MODE_PRIVATE);

        if(!perfers.contains("MuteSound")) {
            return "1";
        }
        return perfers.getString("MuteSound","1");
    }


}
