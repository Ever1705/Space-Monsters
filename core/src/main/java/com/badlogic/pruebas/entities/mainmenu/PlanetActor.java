package com.badlogic.pruebas.entities.mainmenu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;


// Clase para los planetas del menu principal
public class PlanetActor extends Actor implements Disposable {

    private Texture planetTexture;
    private float rotationSpeed;
    private float orbitRadius;
    private float orbitSpeed;
    private float startAngle;
    private float centerX, centerY;


    private float wobbleTimer;
    private float wobbleMagnitudeX;
    private float wobbleMagnitudeY;
    private float wobbleSpeedX;
    private float wobbleSpeedY;

    public PlanetActor(Texture texture, float initialX, float initialY, float width, float height,
                       float rotationSpeed, float orbitRadius, float orbitSpeed) {
        this.planetTexture = texture;
        setBounds(initialX, initialY, width, height);
        setOrigin(width / 2, height / 2);

        this.rotationSpeed = rotationSpeed;

        this.orbitRadius = orbitRadius;
        this.orbitSpeed = orbitSpeed;
        this.startAngle = MathUtils.random(360f);
        this.centerX = initialX;
        this.centerY = initialY;

        this.wobbleTimer = MathUtils.random(0f, 100f);
        this.wobbleMagnitudeX = MathUtils.random(5f, 15f);
        this.wobbleMagnitudeY = MathUtils.random(5f, 15f);
        this.wobbleSpeedX = MathUtils.random(0.5f, 1.5f);
        this.wobbleSpeedY = MathUtils.random(0.5f, 1.5f);

    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Rotación del planeta
        rotateBy(rotationSpeed * delta);


        float angle = startAngle + (orbitSpeed * delta);
        float currentX = centerX + MathUtils.cosDeg(angle) * orbitRadius;
        float currentY = centerY + MathUtils.sinDeg(angle) * orbitRadius;
        startAngle = angle;


        wobbleTimer += delta;
        float wobbleOffsetX = MathUtils.sin(wobbleTimer * wobbleSpeedX) * wobbleMagnitudeX;
        float wobbleOffsetY = MathUtils.cos(wobbleTimer * wobbleSpeedY) * wobbleMagnitudeY;


        setX(currentX + wobbleOffsetX);
        setY(currentY + wobbleOffsetY);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(planetTexture, getX(), getY(), getOriginX(), getOriginY(),
            getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation(),
            0, 0, planetTexture.getWidth(), planetTexture.getHeight(), false, false);
    }

    @Override
    public void dispose() {

    }
}
