package com.badlogic.pruebas.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.pruebas.ayudas.hitboxes.RectangleHitBox;
import com.badlogic.pruebas.ayudas.loadTexture.LoadAnimationPng;

import java.util.EnumMap;
import java.util.List;

public class SlimeModel implements Disposable {

    // Estados del slime
    public enum SlimeState{
        IDLE,WALK,ATTACK,HURT,DEATH
    }
    // Direcciones del slime
    public enum SlimeDirection {
        LEFT,RIGHT,FRONT
    }

    protected Sound soundHurt;
    protected Sound soundDeath;
    protected Sound soundAttack;


    // Animación actual
    protected LoadAnimationPng currentAnimation;

    // Variables para el movimiento
    protected float speed;
    protected Vector2 position;
    protected Vector2 velocity;


    // Para saber si ataca
    protected boolean isAttacking;

    // Estados
    protected SlimeState slimeState;
    // Dirección de movimiento
    protected SlimeDirection slimeDirection;

    // Hitboxes principales
    public RectangleHitBox hitBox;
    public RectangleHitBox viewHitBox;

    protected EnumMap<SlimeState,EnumMap<SlimeDirection,LoadAnimationPng>> animations;

    public SlimeModel() {
        this.isAttacking = false;
        this.slimeState = SlimeState.IDLE;
        this.slimeDirection = SlimeDirection.FRONT;
        this.soundHurt = Gdx.audio.newSound(Gdx.files.internal("sounds/Retro Impact Punch 07.wav"));

        this.soundDeath = Gdx.audio.newSound(Gdx.files.internal("sounds/Retro Blop 18.wav"));

    }


    // Para cargar todas las animaciones
    protected void loadAllAnimation(float scale){


    }

    // Funciom principal para actualizar las propiedades del objeto
    public void update(float delta){
        currentAnimation.update(delta);

        // Para actualizar la posición del sprite
        currentAnimation.getSprite().setPosition(position.x,position.y);

        updateAttack();

        updateHitbox();
        updateHitBoxesAttack();
    }

    // Dibuja la animacion actual
    public void draw(SpriteBatch batch){
        currentAnimation.draw(batch);
    }

    // Metodo para cambiar la animación
    public void setAnimation(SlimeState state, SlimeDirection direction){
        LoadAnimationPng newAnimation = animations.get(state).get(direction);
        if (newAnimation != null && newAnimation != currentAnimation) {
            if (currentAnimation != null) {
                currentAnimation.resetAnimationTime(); // Reinicia la animación anterior
            }
            currentAnimation = newAnimation;
        }
    }

    // Para controlar el movimiento del personaje
    public void move(float delta){}


    // Para saber si esta colisionando con algo como una pared
    public boolean collidesWithAny(List<Rectangle> solidObjects){
        for (Rectangle solidObject: solidObjects){
            if (hitBox.overlaps(solidObject)) return true;
        }
        return false;
    }


    // Cambiar el estado
    public void setSlimeState(SlimeState newState){
        if (slimeState != newState) {
            slimeState = newState;
            // Actualiza la animación al cambiar de estado
            setAnimation(slimeState, slimeDirection);
        } else {
            // Si el estado es el mismo, aún se necesita asegurar que la animación es la correcta
            // para la dirección actual, especialmente al pasar de diagonal a cardinal
            setAnimation(slimeState, slimeDirection);
        }
    }


    // Logica para iniciar el ataque
    public void startAttack(){

    }

    // Para actualizar el ataque
    public void updateAttack(){

    }


    // Actualiza la hitbox para mantenerse cerca de la hitbox
    public void updateHitbox(){
        if (getSprite() != null) {
            float centerX = getSprite().getX() + getSprite().getWidth() / 2;
            float centerY = getSprite().getY() + getSprite().getHeight() / 2;

            float rectX = centerX - getHitBox().getRectangle().width / 2;
            float rectY = centerY - getHitBox().getRectangle().height / 2;

            getHitBox().getRectangle().setPosition(rectX, rectY);
        }
    }

    // Actualiza la hitbox del ataque
    public void updateHitBoxesAttack() {
    }

    // Dibujas las hitboxes existentes
    public void drawHitbox(ShapeRenderer shapeRenderer, Color color){}

    // Para liberar de la memoria las texturas
    @Override
    public void dispose() {
    }


    //    ------------------- GETTERS y SETTERS GENERALES ------------------------


    public Sprite getSprite() {
        return currentAnimation.getSprite();
    }

    public Rectangle getHitbox(){
        return hitBox.getRectangle();
    }


    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public LoadAnimationPng getCurrentAnimation() {
        return currentAnimation;
    }

    public void setCurrentAnimation(LoadAnimationPng currentAnimation) {
        this.currentAnimation = currentAnimation;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public SlimeDirection getSlimeDirection() {
        return slimeDirection;
    }

    public void setSlimeDirection(SlimeDirection slimeDirection) {
        this.slimeDirection = slimeDirection;
    }

    public SlimeState getSlimeState() {
        return slimeState;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public void setAttacking(boolean attacking) {
        isAttacking = attacking;
    }

    public RectangleHitBox getViewHitBox() {
        return viewHitBox;
    }

    public void setViewHitBox(RectangleHitBox viewHitBox) {
        this.viewHitBox = viewHitBox;
    }

    public RectangleHitBox getHitBox() {
        return hitBox;
    }

    public void setHitBox(RectangleHitBox hitBox) {
        this.hitBox = hitBox;
    }
}
