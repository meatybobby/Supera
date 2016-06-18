package nthu.bobby.supera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class EffectsRenderer implements GLSurfaceView.Renderer {

    private Bitmap photo;
    private int photoWidth, photoHeight;

    private EffectContext effectContext;
    private Effect effect;

    private TextureRenderer square;
    private int textures[] = new int[2];

    public EffectsRenderer(Context context) {
        super();
        photo = BitmapFactory.decodeResource(context.getResources(), R.drawable.forest);
        photoWidth = photo.getWidth();
        photoHeight = photo.getHeight();
        square = new TextureRenderer();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }

    private void generateSquare() {
        GLES20.glGenTextures(2, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, photo, 0);
        square.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        GLES20.glClearColor(0, 0, 0, 1);
        generateSquare();
        square.updateViewSize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (effectContext == null) {
            effectContext = EffectContext.createWithCurrentGlContext();
        }
        if (effect != null) {
            effect.release();
        }
        EffectFactory factory = effectContext.getFactory();
        grainEffect(factory);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
        square.updateTextureSize(photoWidth, photoHeight);
        square.renderTexture(textures[1]);
    }

    private void documentaryEffect(EffectFactory factory) {
        effect = factory.createEffect(EffectFactory.EFFECT_DOCUMENTARY);
    }

    private void grayScaleEffect(EffectFactory factory) {
        effect = factory.createEffect(EffectFactory.EFFECT_GRAYSCALE);
    }

    private void brightnessEffect(EffectFactory factory) {
        effect = factory.createEffect(EffectFactory.EFFECT_BRIGHTNESS);
        effect.setParameter("brightness", 2f);
    }

    private void fisheyeEffect(EffectFactory factory) {
        effect = factory.createEffect(EffectFactory.EFFECT_FISHEYE);
        effect.setParameter("scale", .5f);
    }

    private void lomoEffect(EffectFactory factory) {
        effect = factory.createEffect(EffectFactory.EFFECT_LOMOISH);
    }

    private void filllightEffect(EffectFactory factory) {
        effect = factory.createEffect(EffectFactory.EFFECT_FILLLIGHT);
        effect.setParameter("strength", .8f);
    }

    private void speciaEffect(EffectFactory factory) {
        effect = factory.createEffect(EffectFactory.EFFECT_SEPIA);
    }

    private void grainEffect(EffectFactory factory) {
        effect = factory.createEffect(EffectFactory.EFFECT_GRAIN);
        effect.setParameter("strength", 1.0f);
    }

    public void setPhoto(Bitmap bmp) {
        photo = bmp;
        photoWidth = photo.getWidth();
        photoHeight = photo.getHeight();
    }
}
