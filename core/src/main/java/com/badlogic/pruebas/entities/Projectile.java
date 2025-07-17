package com.badlogic.pruebas.entities; // O donde prefieras ubicarla, quizás en un paquete 'projectiles'

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.pruebas.ayudas.hitboxes.RectangleHitBox;
import com.badlogic.pruebas.ayudas.loadTexture.LoadAnimationPng;

public class Projectile {
    public Vector2 position;
    public Vector2 velocity;
    public RectangleHitBox hitBox;
    private float speed;
    private LoadAnimationPng spriteAnimation;
    private float stateTime;
    private boolean active;
    private float damage;

    public Projectile(float startX, float startY, Vector2 target, float speed, float scale, float damage) {
        this.position = new Vector2(startX, startY);
        this.speed = speed;
        this.damage = damage;
        this.stateTime = 0f;
        this.active = true;

        // Cargar el sprite
        this.spriteAnimation = new LoadAnimationPng("ASSETS/slimes/slimeJefe/fireBall.png", 1, 5, 0.1f, true);
        this.spriteAnimation.getSprite().setScale(0.8f);

        // Inicializa la hitbox del proyectil
        this.hitBox = new RectangleHitBox(position.x, position.y,
            10,
            10);

        // Calcular la dirección hacia el objetivo y normalizar la velocidad
        this.velocity = new Vector2(target.x - startX, target.y - startY).nor().scl(this.speed);
    }

    public void update(float dt) {
        if (!active) return;

        position.add(velocity.x * dt, velocity.y * dt);
        hitBox.getRectangle().setPosition(position.x +11 , position.y+9);
        stateTime += dt;

        // Desactivar el proyectil si sale de los límites de la pantalla
        if (position.x < 0 || position.x > 1200 || position.y < -30 || position.y > 140 ) {
            active = false;
        }
    }

    public void draw(SpriteBatch batch) {
        if (!active) return;
        batch.draw(spriteAnimation.getAnimation().getKeyFrame(stateTime, true), position.x, position.y,
            spriteAnimation.getSprite().getWidth() * spriteAnimation.getSprite().getScaleX(),
            spriteAnimation.getSprite().getHeight() * spriteAnimation.getSprite().getScaleY());
    }

    public void drawHitbox(ShapeRenderer shapeRenderer, Color color) {
        if (!active) return;
        hitBox.drawRectangle(shapeRenderer, color);
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }

    public float getDamage() {
        return damage;
    }
}
