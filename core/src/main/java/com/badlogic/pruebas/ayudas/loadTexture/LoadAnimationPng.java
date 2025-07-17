package com.badlogic.pruebas.ayudas.loadTexture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class LoadAnimationPng implements Disposable {

    private Texture texture;
    private Animation<TextureRegion> animation;
    private Sprite sprite;

    private float stateTime;

    public LoadAnimationPng(String fileName, int rows,
                            int cols, float frameDuration, boolean loop) {


        loadAnimationFromSheet(fileName, rows, cols, frameDuration, loop);
        sprite = new Sprite(animation.getKeyFrame(0));

        stateTime = 0f;
    }

    private void loadAnimationFromSheet(String fileName, int rows,
                                        int cols, float frameDuration, boolean loop) {

        texture = new Texture(Gdx.files.internal(fileName));
        int frameWidth = texture.getWidth() / cols;
        int frameHeight = texture.getHeight() / rows;

        TextureRegion[][] temp = TextureRegion.split(texture, frameWidth, frameHeight);

        Array<TextureRegion> textureArray = new Array<>();

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                textureArray.add(temp[r][c]);

//        Se pasan los frames a la animación
        animation = new Animation<>(frameDuration, textureArray);

        if (loop)
            animation.setPlayMode(Animation.PlayMode.LOOP);
        else
            animation.setPlayMode(Animation.PlayMode.NORMAL);
    }


    public void update(float deltaTime) {
        stateTime += deltaTime;
        TextureRegion region = animation.getKeyFrame(stateTime);
        sprite.setRegion(region);
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public void resetAnimationTime() {
        stateTime = 0;
    }

    public boolean isAnimationFinished() {
        return animation.isAnimationFinished(stateTime);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Animation<TextureRegion> getAnimation() {
        return animation;
    }

    public float getStateTime() {
        return stateTime;
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

}
