package com.badlogic.pruebas;

import com.badlogic.gdx.Game;
import com.badlogic.pruebas.screens.game.GameScreen;
import com.badlogic.pruebas.screens.menu.LoadingScreen;
import com.badlogic.pruebas.screens.menu.MainMenu;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {





    @Override
    public void create() {
        this.setScreen(new LoadingScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }
}
