package com.badlogic.pruebas.entities.minigame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.pruebas.ayudas.hitboxes.RectangleHitBox;

public class Spaceship implements Disposable {
    private Texture texture;
    private Sprite sprite;
    private Sound soundHurt;
    private Vector2 position;
    private float speed = 150.0f;
    private RectangleHitBox hitBox;
    private int lives;
    private float invulnerabilityTimer;
    private final float INVULNERABILITY_DURATION = 1.0f;

    public Spaceship(float x, float y) {
        this.texture = new Texture("ASSETS/minijuego/Cohete.png");
        this.soundHurt = Gdx.audio.newSound(Gdx.files.internal("sounds/Retro Impact Punch 07.wav"));
        this.sprite = new Sprite(texture);
        this.sprite.setSize(44, 32);
        this.position = new Vector2(x, y);
        this.sprite.setPosition(position.x, position.y);
        this.hitBox = new RectangleHitBox(position.x, position.y, sprite.getWidth(), 24);
        this.lives = 3;
        this.invulnerabilityTimer = 0;
    }

    public void update(float delta, float worldHeight) {

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            position.y += speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            position.y -= speed * delta;
        }

        // Limita el movimiento de la nave dentro de la pantalla
        if (position.y < 0) {
            position.y = 0;
        }
        if (position.y > worldHeight - sprite.getHeight()) {
            position.y = worldHeight - sprite.getHeight();
        }

        sprite.setPosition(position.x, position.y);
        hitBox.getRectangle().setPosition(position.x, position.y+4);

        // Actualiza el temporizador de invulnerabilidad
        if (invulnerabilityTimer > 0) {
            invulnerabilityTimer -= delta;
            // Parpadeo para indicar que recibio daño
            if ((int)(invulnerabilityTimer * 10) % 2 == 0) {
                sprite.setColor(1, 1, 1, 0.5f);
            } else {
                sprite.setColor(1, 1, 1, 1);
            }
        } else {
            sprite.setColor(1, 1, 1, 1);
        }
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public void drawHitbox(ShapeRenderer shapeRenderer) {
        hitBox.drawRectangle(shapeRenderer, com.badlogic.gdx.graphics.Color.CYAN);
    }

    public RectangleHitBox getHitBox() {
        return hitBox;
    }

    public void takeDamage() {
        if (invulnerabilityTimer <= 0) {
            lives--;
            soundHurt.play(0.5f);
            invulnerabilityTimer = INVULNERABILITY_DURATION;
        }
    }

    public int getLives() {
        return lives;
    }

    public boolean isAlive() {
        return lives > 0;
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
