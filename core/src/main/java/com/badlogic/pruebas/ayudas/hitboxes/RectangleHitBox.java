package com.badlogic.pruebas.ayudas.hitboxes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;


public class RectangleHitBox {

    private Rectangle rectangle;
    private boolean isActive;

    public RectangleHitBox(float x, float y, float with, float height) {
        rectangle = new Rectangle(x, y, with, height);
        isActive = true;
    }


    public void drawRectangle(ShapeRenderer shapeRenderer, Color color) {
        shapeRenderer.setColor(color);
        shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }


    public void active(boolean active) {
        isActive = active;
    }

    public boolean overlaps(Rectangle r) {
        if (isActive) {
            return rectangle.overlaps(r);
        }
        return false;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public boolean isActive() {
        return isActive;
    }

}
