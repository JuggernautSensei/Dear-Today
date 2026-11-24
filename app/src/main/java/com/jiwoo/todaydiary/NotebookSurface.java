package com.jiwoo.todaydiary;

import android.content.Context;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.widget.FrameLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

@SuppressLint("ViewConstructor") // This surface is constructed by the Activity, not inflated from XML.
final class NotebookSurface extends FrameLayout {
    static final int INK = Color.rgb(48, 43, 38);
    static final int MUTED = Color.rgb(137, 127, 113);
    static final int PAPER = Color.rgb(246, 241, 231);
    static final int LEATHER = Color.rgb(48, 41, 35);
    static final int RULE = Color.rgb(216, 207, 192);

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final BitmapShader leather;
    private final BitmapShader paper;
    private final boolean cover;
    private final float density;
    private final DashPathEffect stitching;
    private final LinearGradient pageShadow;
    private final LinearGradient spine;

    NotebookSurface(Context context, boolean cover, Bitmap leatherBitmap, Bitmap paperBitmap) {
        super(context);
        this.cover = cover;
        density = getResources().getDisplayMetrics().density;
        stitching = new DashPathEffect(new float[]{dp(3), dp(3)}, 0);
        pageShadow = new LinearGradient(dp(13), 0, dp(27), 0, new int[]{0x44332A21, 0x00FFFFFF}, null, Shader.TileMode.CLAMP);
        spine = new LinearGradient(0, 0, dp(cover ? 27 : 13), 0, new int[]{0x99000000, 0x00332B23, 0x77000000}, null, Shader.TileMode.CLAMP);
        leather = new BitmapShader(leatherBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        Matrix scale = new Matrix();
        scale.setScale(density * .45f, density * .45f);
        leather.setLocalMatrix(scale);
        paper = new BitmapShader(paperBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        setWillNotDraw(false);
    }

    static Bitmap loadLeather(Context context) {
        try (InputStream stream = context.getAssets().open("textures/espresso-leather.png")) {
            return BitmapFactory.decodeStream(stream);
        } catch (IOException error) {
            throw new AssertionError("Bundled leather texture is missing.", error);
        }
    }

    static Bitmap makePaper() {
        Bitmap bitmap = Bitmap.createBitmap(192, 192, Bitmap.Config.ARGB_8888);
        Random random = new Random(17);
        int[] pixels = new int[192 * 192];
        for (int i = 0; i < pixels.length; ++i) {
            int grain = random.nextInt(9) - 4;
            pixels[i] = Color.rgb(246 + grain, 241 + grain, 231 + grain);
        }
        bitmap.setPixels(pixels, 0, 192, 0, 0, 192, 192);
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(cover ? leather : paper);
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);
        if (cover) {
            paint.setColor(0x25000000);
            canvas.drawRect(0, 0, w, h, paint);
            paint.setColor(0x55443A30);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(1));
            canvas.drawRoundRect(dp(12), dp(8), w - dp(10), h - dp(8), dp(10), dp(10), paint);
            paint.setColor(0xFF8C7C65);
            paint.setStrokeWidth(dp(.65f));
            paint.setPathEffect(stitching);
            canvas.drawRoundRect(dp(17), dp(13), w - dp(15), h - dp(13), dp(8), dp(8), paint);
            paint.setPathEffect(null);
        } else {
            paint.setShader(leather);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0, 0, dp(13), h, paint);
            paint.setShader(pageShadow);
            canvas.drawRect(dp(13), 0, dp(27), h, paint);
            paint.setShader(null);
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(spine);
        canvas.drawRect(0, 0, dp(cover ? 27 : 13), h, paint);
        paint.setShader(null);
    }

    private float dp(float value) { return value * density; }
}
