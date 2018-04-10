package com.example.admin.pintu.activity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.pintu.R;
import com.example.admin.pintu.engine.RecordDao;
import com.example.admin.pintu.model.Record;
import com.example.admin.pintu.utils.DialogHelper;
import com.example.admin.pintu.utils.MyLog;
import com.example.admin.pintu.utils.SPUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.preview_photo)
    Button preView;
    @BindView(R.id.tv_time)
    TextView time;
    @BindView(R.id.tv_tip)
    TextView tip;


    private ImageView[][] iv_game_arr;
    private GridLayout gl_main_geme;
    /**
     * 空方块
     */
    private ImageView iv_null_imageView;
    //手势监听
    private GestureDetector detector;
    /**
     * 记录游戏是否开始
     */
    private boolean isGameStar = false;
    /**
     * 动画是否在执行
     */
    private boolean isAnimaRun = false;
    /**
     * 是否结束
     */
    private boolean isOver = false;

    private int step;

    private SoundPool pool;
    private MediaPlayer mp_player;
    private Bitmap bitmap;
    private PopupWindow popupWindow;
    private Realm realm;
    private String difficulty;//游戏难度
    private boolean isFrist;//是否是第一次进入游戏


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //定义手势操作必须放在setContentView之前
        initDetector();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("hello");

        realm = Realm.getDefaultInstance();

    }

    @Override
    protected void onResume() {
        super.onResume();
        selectDifficulty();
        initData();
        initView();
        initSound();
        //设置一个方块为空
        setNullImageView(iv_game_arr[0][0]);
        //随机大乱方块
        randomMove();
        isGameStar = true;

    }

    /**
     * 游戏难度
     */
    private void selectDifficulty() {
        difficulty = SPUtils.getString("SELECT_GAME_DIFFICULTY", "简单");
        if ("困难".equals(difficulty)) {
            iv_game_arr = new ImageView[5][5];
        } else if ("炼狱".equals(difficulty)) {
            iv_game_arr = new ImageView[6][6];
        } else if ("深渊".equals(difficulty)) {
            iv_game_arr = new ImageView[7][7];
        } else {
            iv_game_arr = new ImageView[4][4];
        }
        isFrist = SPUtils.getBool("FRIST_INTO_GAME", true);
        if (isFrist) {
            boolean init = new RecordDao(realm).init();
            if (init) {
                SPUtils.putValue("FRIST_INTO_GAME", false);
                MyLog.e("插入数据库成功");
                tip.setText("插入数据库成功");
            }
        }

    }

    /**
     * 初始化UI
     */
    private void initView() {
        gl_main_geme = (GridLayout) findViewById(R.id.gl);
        gl_main_geme.setColumnCount(iv_game_arr[0].length);
        gl_main_geme.setRowCount(iv_game_arr.length);
        for (int i = 0; i < iv_game_arr.length; i++) {
            for (int j = 0; j < iv_game_arr[0].length; j++) {
                gl_main_geme.addView(iv_game_arr[i][j]);
            }
        }
        initPopWindow();


        preView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 弹出popwindow 显示拼图照片
                        popupWindow.showAtLocation(gl_main_geme, Gravity.TOP, 0, 0);
                        break;
                    case MotionEvent.ACTION_UP:
                        //关闭popwindow
                        popupWindow.dismiss();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }

    /**
     * 初始化popwindow
     */
    private void initPopWindow() {
        View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popuplayout, null);
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(contentView);
        ImageView image = (ImageView) contentView.findViewById(R.id.iv_bitmap);
        image.setImageBitmap(bitmap);
    }

    /**
     * 定义手势操作
     */
    private void initDetector() {
        detector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                changeByDir(getDirByGes(motionEvent.getX(), motionEvent.getY(), motionEvent1.getX(), motionEvent1.getY()));
                return false;
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        detector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings://重新开始

                isGameStar = false;
                isOver = false;

                //设置一个方块为空
//            setNullImageView(iv_game_arr[0][0]);
                //随机大乱方块
                randomMove();
                isGameStar = true;
                if (!mp_player.isPlaying()) {

                    try {
                        mp_player.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mp_player.start();

                }
                break;
            case R.id.action_exit://退出
                DialogHelper.getCoonfirmDialog(this, "温馨提示", "即将退出游戏", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
                break;
            default:
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化声音池
     */
    private void initSound() {
        //当前系统的SDK版本大于等于21(Android 5.0)时
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入音频数量
            builder.setMaxStreams(5);
            //AudioAttributes是一个封装音频各种属性的方法
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适的属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            //加载一个AudioAttributes
            builder.setAudioAttributes(attrBuilder.build());
            pool = builder.build();
        }
        //当系统的SDK版本小于21时
        else {//设置最多可容纳5个音频流，音频的品质为5
            pool = new SoundPool(5, AudioManager.STREAM_SYSTEM, 5);
        }

        pool.load(this, R.raw.a, 1);
        pool.load(this, R.raw.b, 2);
        pool.load(this, R.raw.c, 3);


        mp_player = MediaPlayer.create(this, R.raw.abc);
        mp_player.setLooping(true);
        mp_player.start();

    }

    /**
     * 初始化数据
     */
    private void initData() {
        String bitmapPath = getIntent().getStringExtra("bitmap_path");

        if (TextUtils.isEmpty(bitmapPath)) {
            bitmap = ((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.nvsheng)).getBitmap();
        } else {
            // 取得图片旋转角度
            int angle = readPictureDegree(bitmapPath);
            MyLog.e("旋转角度是:" + angle);
            // 把原图压缩后得到Bitmap对象     bitmap = getCompressPhoto(bitmapPath);
            bitmap = BitmapFactory.decodeFile(bitmapPath);
            // 修复图片被旋转的角度
            bitmap = rotaingImageView(angle, bitmap);

        }
        //初始化小图片
        int tuWandH = bitmap.getWidth() / iv_game_arr[0].length;//4
        int ivWandH = getWindowManager().getDefaultDisplay().getWidth() / iv_game_arr[0].length;
        MyLog.e("bitmap.getWidth()--->>" + bitmap.getWidth());
        MyLog.e("bitmap.getHeight()--->>" + bitmap.getHeight());
        MyLog.e("pingmuw--->>" + getWindowManager().getDefaultDisplay().getWidth());
        MyLog.e("pingmuh--->>" + getWindowManager().getDefaultDisplay().getHeight());
        MyLog.e("tuWandH--->>" + tuWandH);
        MyLog.e("ivWandH--->>" + ivWandH);
        for (int i = 0; i < iv_game_arr.length; i++) {
            for (int j = 0; j < iv_game_arr[0].length; j++) {
                Bitmap bm = Bitmap.createBitmap(bitmap, j * tuWandH, i * tuWandH, tuWandH, tuWandH);
                iv_game_arr[i][j] = new ImageView(this);
                iv_game_arr[i][j].setImageBitmap(bm);
                iv_game_arr[i][j].setLayoutParams(new RelativeLayout.LayoutParams(ivWandH, ivWandH));
                iv_game_arr[i][j].setPadding(2, 2, 2, 2);
                iv_game_arr[i][j].setTag(new GameData(i, j, bm));
                iv_game_arr[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean flag = isHasByNullImageView((ImageView) view);
                        if (flag) {
                            changeDateByImageView((ImageView) view);
                        }
                    }
                });
            }
        }
    }

    /**
     * 读取照片旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    break;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 把原图按1/10的比例压缩
     *
     * @param path 原图的路径
     * @return 压缩后的图片
     */
    public static Bitmap getCompressPhoto(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 10;  // 图片的大小设置为原来的十分之一
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        options = null;
        return bmp;
    }

    /**
     * 旋转图片
     *
     * @param angle  被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }

    /**
     * 设置空方块
     */
    private void setNullImageView(ImageView imageView) {
        imageView.setImageBitmap(null);
        iv_null_imageView = imageView;
    }


    /**
     * 判断当前方块与空房快是否是相邻位置
     */
    private boolean isHasByNullImageView(ImageView imageView) {
        //获取空方块和点击的方块的位置
        GameData nullGameData = (GameData) iv_null_imageView.getTag();
        GameData gameData = (GameData) imageView.getTag();
        if (nullGameData.y == gameData.y && nullGameData.x == gameData.x + 1) {//上
            return true;
        } else if (nullGameData.y == gameData.y && nullGameData.x + 1 == gameData.x) {//下
            return true;
        } else if (nullGameData.y == gameData.y + 1 && nullGameData.x == gameData.x) {//左边
            return true;
        } else if (nullGameData.y + 1 == gameData.y && nullGameData.x == gameData.x) {//右边
            return true;
        }

        return false;
    }

    /**
     * 根据手势交换空方块
     *
     * @param type 上下左右
     */
    private void changeByDir(int type) {
        changeByDir(type, true);
    }


    /**
     * 根据手势交换空方块
     *
     * @param type 上下左右
     * @param flag
     */
    private void changeByDir(int type, boolean flag) {
        //获取空方块位置
        GameData gameData = (GameData) iv_null_imageView.getTag();
        int new_x = gameData.x;
        int new_y = gameData.y;
        //根据手势设置相邻的位置
        if (type == 1) {
            new_x++;
        } else if (type == 2) {
            new_x--;
        } else if (type == 3) {
            new_y++;
        } else if (type == 4) {
            new_y--;
        }
        //判断位置是否存在
        if (new_x >= 0 && new_x <= iv_game_arr.length - 1 && new_y >= 0 && new_y <= iv_game_arr[0].length - 1) {
            changeDateByImageView(iv_game_arr[new_x][new_y], flag);
            if (!isOver) {
                time.setText(++step + "");
                pool.play(1, 2, 2, 0, 0, 1);
            }

        } else {
            if (!isOver) {
                pool.play(2, 2, 2, 0, 0, 1);
            }
        }
    }

    /**
     * 随机打乱图片位置
     */
    private void randomMove() {

        //打乱的次数
        for (int i = 0; i < 100; i++) {
            //交换位置  无动画
            int type = (int) (Math.random() * 4) + 1;
            changeByDir(type, false);
        }
        //重置计步器
        time.setText((step = 0) + "");
    }

    //判断游戏结束
    private void isGameOver() {
        boolean isGameOver = true;

        for (int i = 0; i < iv_game_arr.length; i++) {
            for (int j = 0; j < iv_game_arr[0].length; j++) {
                //为空的图片不判断
                if (iv_game_arr[i][j] == iv_null_imageView) {
                    continue;
                }
                //
                GameData gameData = (GameData) iv_game_arr[i][j].getTag();
                if (!gameData.isTrue()) {
                    isGameOver = false;
                    break;
                }
            }
        }
        System.out.print("" + isGameOver);
        if (isGameOver) {
            isOver = true;
            mp_player.stop();
            pool.play(3, 2, 2, 0, 0, 1);
            Toast.makeText(MainActivity.this, "游戏结束", Toast.LENGTH_SHORT).show();
            insertDB();
        }


    }

    private void insertDB() {
        //判断数据
        Record record = new Record();
        record.setType(difficulty);
        record.setTime(step);
        Record time = realm.where(Record.class).equalTo("type", difficulty).greaterThan("time", step).findFirst();
        if (time != null) {
            // TODO: 2017/9/20 弹出对话框提示破纪录了要求填写姓名 确定后存入数据库
            tip.setText(time.toString());
//            time.setTime(step);  不能执行会报错
//            time.setTime(step);  不能执行会报错
//            time.setTime(step);  不能执行会报错
//            time.setTime(step);  不能执行会报错


//            time.setDate(TimeUtil.getCurrentDate());
            Toast.makeText(this, "恭喜打破记录了!!!", Toast.LENGTH_SHORT).show();


        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!isOver) {
            mp_player.start();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isOver) {
            mp_player.pause();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        pool.release();
        mp_player.release();
    }

    /**
     * 通过手势改变方块的位置
     *
     * @param imageView
     */
    private void changeDateByImageView(final ImageView imageView) {
        changeDateByImageView(imageView, true);
    }

    /**
     * @param imageView
     * @param flag
     */
    private void changeDateByImageView(final ImageView imageView, boolean flag) {
        if (isOver) {
            return;
        }
        if (isAnimaRun) {
            return;
        }

        if (!flag) {
            GameData gameData = (GameData) imageView.getTag();
            iv_null_imageView.setImageBitmap(gameData.bm);
            GameData nullImageView = (GameData) iv_null_imageView.getTag();
            nullImageView.bm = gameData.bm;
            nullImageView.p_x = gameData.p_x;
            nullImageView.p_y = gameData.p_y;
            //设置当前方块位空方块
            setNullImageView(imageView);
            if (isGameStar) {
                isGameOver();

            }

            return;
        }


        TranslateAnimation translateAnimation = null;
        if (imageView.getX() > iv_null_imageView.getX()) {//当点击的方块在空方块的下边
            //往上移动
            translateAnimation = new TranslateAnimation(0.1f, -imageView.getWidth(), 0.1f, 0.1f);
        } else if (imageView.getX() < iv_null_imageView.getX()) {//当点击的方块在空方块的上边
            //往下移动
            translateAnimation = new TranslateAnimation(0.1f, imageView.getWidth(), 0.1f, 0.1f);
        } else if (imageView.getY() > iv_null_imageView.getY()) {//当点击的方块在空方块的右边
            //往左移动
            translateAnimation = new TranslateAnimation(0.1f, 0.1f, 0.1f, -imageView.getWidth());
        } else if (imageView.getY() < iv_null_imageView.getY()) {//当点击的方块在空方块的左边
            //往右移动
            translateAnimation = new TranslateAnimation(0.1f, 0.1f, 0.1f, imageView.getWidth());
        }
        //设置动画时长

        translateAnimation.setDuration(100);
        translateAnimation.setFillAfter(true);
        //交换数据
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimaRun = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.clearAnimation();
                GameData gameData = (GameData) imageView.getTag();
                iv_null_imageView.setImageBitmap(gameData.bm);
                GameData nullImageView = (GameData) iv_null_imageView.getTag();
                nullImageView.bm = gameData.bm;
                nullImageView.p_x = gameData.p_x;
                nullImageView.p_y = gameData.p_y;
                //设置当前方块位空方块
                setNullImageView(imageView);
                if (isGameStar) {
                    isGameOver();
                }
                isAnimaRun = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //执行动画
        imageView.startAnimation(translateAnimation);

    }


    /**
     * 通过手势判断滑动方向
     */

    private int getDirByGes(float start_x, float start_y, float end_x, float end_y) {
        boolean isLeftOrRight = (Math.abs(start_x - end_x) > Math.abs(start_y - end_y)) ? true : false;
        if (isLeftOrRight) {
            boolean isLeft = start_x - end_x > 0 ? true : false;
            if (isLeft) {
                return 3;
            } else {
                return 4;
            }
        } else {
            boolean isUp = start_y - end_y > 0 ? true : false;
            if (isUp) {
                return 1;
            } else {
                return 2;
            }
        }
    }


    class GameData {
        //每个小方块实际位置xy
        private int x = 0;
        private int y = 0;
        //每个想方块的图片
        private Bitmap bm;
        //每个小方块图片的位置
        private int p_x = 0;
        private int p_y = 0;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public Bitmap getBm() {
            return bm;
        }

        public void setBm(Bitmap bm) {
            this.bm = bm;
        }

        public int getP_x() {
            return p_x;
        }

        public void setP_x(int p_x) {
            this.p_x = p_x;
        }

        public int getP_y() {
            return p_y;
        }

        public void setP_y(int p_y) {
            this.p_y = p_y;
        }

        public GameData(int x, int y, Bitmap bm) {
            this.x = x;
            this.y = y;
            this.bm = bm;
            this.p_x = x;
            this.p_y = y;
        }

        public boolean isTrue() {
            if (x == p_x && y == p_y) {
                return true;
            }
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
