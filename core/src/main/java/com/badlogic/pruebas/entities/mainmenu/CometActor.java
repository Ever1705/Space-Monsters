package com.badlogic.pruebas.entities.mainmenu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;

// Clase para los cometas del menu principal
public class CometActor extends Actor implements Disposable {

    private Texture cometTexture;
    private float speed;


    public CometActor(Texture texture, float initialX, float initialY,
                      float width, float height, float speed) {

        this.cometTexture = texture;
        setBounds(initialX, initialY, width, height);
        setOrigin(width / 2, height / 2);
        this.speed = speed;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Se mueve el cometa hacia abajo-izquierda en diagonal
        setX(getX() - speed * delta * 0.5f);
        setY(getY() - speed * delta);


    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(cometTexture, getX(), getY(), getOriginX(), getOriginY(),
            getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation(),
            0, 0, cometTexture.getWidth(), cometTexture.getHeight(), false, false);
    }

    // Para verificar si el cometa está fuera de la pantalla
    public boolean isOffscreen() {
        return getY() + getHeight() < -100 || getX() + getWidth() < -100;
    }

    @Override
    public void dispose() {
    }
}
