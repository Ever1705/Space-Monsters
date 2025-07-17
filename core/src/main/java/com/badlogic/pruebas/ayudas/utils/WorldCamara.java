package com.badlogic.pruebas.ayudas.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

public class WorldCamara {

    private OrthographicCamera camera;


    public WorldCamara() {
        camera = new OrthographicCamera();
    }


    public void update() {
        camera.update();
    }

    public void limitZoomCamara(float min, float max) {
        camera.zoom = MathUtils.clamp(camera.zoom, min, max);
    }

    public void limitMoveCamara(float mapWidth, float mapHeight) {
        // --- LÓGICA DE CLAMPING DE LA CÁMARA ---
        // Obtener el ancho y alto visible actual de la cámara, afectados por el zoom
        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        // Calcular la mitad del ancho y alto visible de la cámara
        float halfCameraWidth = effectiveViewportWidth / 2;
        float halfCameraHeight = effectiveViewportHeight / 2;

        // Calcular los límites mínimos y máximos para el centro de la cámara
        // Estos límites aseguran que el borde de la cámara nunca se salga del borde del mapa.
        float minCameraX = halfCameraWidth;
        float maxCameraX = mapWidth - halfCameraWidth;
        float minCameraY = halfCameraHeight;
        float maxCameraY = mapHeight - halfCameraHeight;

        // Clamp para X
        // Si el mapa es más pequeño que el ancho visible de la cámara,
        // simplemente centra la cámara en X. De lo contrario, aplica el clamping.
        if (mapWidth < effectiveViewportWidth) {
            camera.position.x = mapWidth / 2f;
        } else {
            camera.position.x = MathUtils.clamp(camera.position.x, minCameraX, maxCameraX);
        }

        // Clamp para Y
        // Si el mapa es más pequeño que el alto visible de la cámara,
        // simplemente centra la cámara en Y. De lo contrario, aplica el clamping.
        if (mapHeight < effectiveViewportHeight) {
            camera.position.y = mapHeight / 2f;
        } else {
            camera.position.y = MathUtils.clamp(camera.position.y, minCameraY, maxCameraY);
        }
    }

    public void controlCamara() {
        // El movimiento de la cámara manual (con teclas) debe ser solo para depuración
        // o si el juego tiene un control de cámara manual.
        // Si la cámara sigue al jugador, esta parte es menos prioritaria.
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-5f, 0, 0); // Ajusta la velocidad si es necesario
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(5f, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0, -5f, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0, 5f, 0);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.O)) {
            camera.zoom += 0.01f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            camera.zoom -= 0.01f;
        }
    }


    public void camaraEntityFollow(float entityX, float entityY){
        camera.position.set(entityX,entityY,0);

    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }
}
