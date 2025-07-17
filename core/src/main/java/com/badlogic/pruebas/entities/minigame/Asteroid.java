package com.badlogic.pruebas.entities.minigame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.pruebas.ayudas.hitboxes.RectangleHitBox;

public class Asteroid implements Disposable {
    private Texture texture;
    private Sprite sprite;
    private RectangleHitBox hitBox;
    private float speed;
    private boolean active;

    public Asteroid(float x, float y, float width, float height, float speed) {
        texture = new Texture("ASSETS/minijuego/meteoro.png");
        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        sprite.setPosition(x, y);
        hitBox = new RectangleHitBox(x, y, width, height);
        this.speed = speed;
        this.active = true;
    }

    public void update(float delta) {
        if (active) {
            sprite.translateX(-speed * delta); // Se mueve de derecha a izquierda
            hitBox.getRectangle().x = sprite.getX();
        }
    }

    public void draw(SpriteBatch batch) {
        if (active) {
            sprite.draw(batch);
        }
    }

    public void drawHitbox(ShapeRenderer shapeRenderer) {
        if (active) {
            hitBox.drawRectangle(shapeRenderer, com.badlogic.gdx.graphics.Color.BLUE);
        }
    }

    public Rectangle getRectangle() {
        return hitBox.getRectangle();
    }

    // Para saber si está fuera de los límites
    public boolean isOffscreen(float worldWidth) {
        return sprite.getX() + sprite.getWidth() < 0;
    }

    public void deactivate() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
