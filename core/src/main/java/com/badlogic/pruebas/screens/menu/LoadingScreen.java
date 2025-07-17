// package com.badlogic.pruebas.screens.menu;
// Ajusta el paquete según tu estructura de carpetas
package com.badlogic.pruebas.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.pruebas.ayudas.loadTexture.LoadAnimationPng;
import com.badlogic.pruebas.Main;

// Pantalla de animación de inicio
public class LoadingScreen implements Screen {

    private Main game;
    private SpriteBatch batch;
    private LoadAnimationPng loadAnimation;


    public LoadingScreen(Main game) {
        this.game = game;
        batch = new SpriteBatch();
    }

    @Override
    public void show() {
        loadAnimation = new LoadAnimationPng("ASSETS/screen_animation/Anima_inicio.png", 1, 11, 0.1f, false);
        loadAnimation.getSprite().setSize(Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        loadAnimation.update(delta);


        batch.begin();
        loadAnimation.draw(batch);
        batch.end();


        if (loadAnimation.isAnimationFinished()) {
            Gdx.app.log("LoadingScreen", "Animación de carga terminada. Cambiando a MainMenu.");
            game.setScreen(new MainMenu(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (loadAnimation != null) {
            loadAnimation.getSprite().setPosition(
                (width - loadAnimation.getSprite().getWidth()) / 2,
                (height - loadAnimation.getSprite().getHeight()) / 2
            );
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (loadAnimation != null) {
            loadAnimation.dispose();
        }
    }
}
