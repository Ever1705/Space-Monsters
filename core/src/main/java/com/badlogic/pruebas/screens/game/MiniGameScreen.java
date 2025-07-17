package com.badlogic.pruebas.screens.game; // Nuevo paquete para minijuego

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.pruebas.ayudas.loadTexture.LoadAnimationPng;
import com.badlogic.pruebas.ayudas.models.ScreenModel;
import com.badlogic.pruebas.Main;
import com.badlogic.pruebas.entities.minigame.Asteroid;
import com.badlogic.pruebas.entities.minigame.Spaceship;
import com.badlogic.pruebas.screens.menu.MainMenu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MiniGameScreen extends ScreenModel {

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private FitViewport gameViewport; // Para el mundo del juego
    private ScreenViewport uiViewport; // Para la interfaz de usuario

    private Texture backgroundTexture;
    private Spaceship spaceship;
    private List<Asteroid> asteroids;

    // Musica y sonidos
    private Music backgroundMusic;

    // Generación de asteroides
    private float asteroidSpawnTimer;
    private float timeToNextAsteroid;
    private final float MIN_ASTEROID_SPAWN_TIME = 0.5f;
    private final float MAX_ASTEROID_SPAWN_TIME = 0.5f;

    // UI y estado del juego
    private Stage uiStage;
    private Skin skin;
    private BitmapFont font;

    // Para saber si gano o no
    private boolean isGameOver;
    private boolean isGameWon;

    // Diálogo para victoria o derrota
    private Dialog resultDialog;

    // Menu de pausa
    private boolean isPaused;
    private Dialog pauseDialog;

    // Animación de progreso
    private LoadAnimationPng progressBarAnimation;
    // Duración del minijuego en segundos
    private float gameDuration = 30.0f;
    private float gameTimeElapsed;

    // Vidas
    private Texture heartTexture;
    private List<Rectangle> heartIcons;

    public MiniGameScreen(Main main) {
        super(main);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        gameViewport = new FitViewport(400, 240);
        uiViewport = new ScreenViewport();

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/16_bit_space.ogg"));
        backgroundMusic.setLooping(true);

        // Fondo del mundo
        backgroundTexture = new Texture("MenuAssets/Starfield_.png");

        // Posición de la nave del jugador
        spaceship = new Spaceship(50, gameViewport.getWorldHeight() / 2 - 16);
        asteroids = new ArrayList<>();

        // Inicialización de la interfaz
        uiStage = new Stage(uiViewport);
        skin = new Skin(Gdx.files.internal("UI/a.json")); // Reutiliza el skin del MainMenu
        font = new BitmapFont(Gdx.files.internal("fonts/PixelifySans-VariableFont_wght.fnt"));
        font.getData().markupEnabled = true;
        font.getData().setLineHeight(20f);
        font.getData().scale(0.3f);

        isGameOver = false;
        isGameWon = false;


        isPaused = false;
        // Se inicializa el menu de pausa
        setupPauseDialog();

        // La duración de esta animación es igual o similar al tiempo de juego para ganar
        progressBarAnimation = new LoadAnimationPng("ASSETS/screen_animation/carga_13FX12C.png", 13, 12, 2.5f, false);
        progressBarAnimation.getSprite().setScale(4);
        progressBarAnimation.getSprite().setPosition(gameViewport.getWorldWidth() / 2 - progressBarAnimation.getSprite().getWidth() / 2, gameViewport.getWorldHeight() - 30);
        gameTimeElapsed = 0;

        // Vidas del jugador
        heartTexture = new Texture("ASSETS/player_ui/Vida1.png");
        heartIcons = new ArrayList<>();
        updateHeartIcons();

        // Inicializa el timer para hacer aparecer los asteroides
        resetAsteroidSpawnTimer();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(uiStage);
        backgroundMusic.play();
    }

    // Para actualizar los iconos de corazones
    private void updateHeartIcons() {
        heartIcons.clear();
        float heartSize = 16;
        float heartPadding = 5;
        for (int i = 0; i < spaceship.getLives(); i++) {
            heartIcons.add(new Rectangle(uiViewport.getWorldWidth() - (heartSize + heartPadding) * (i + 1) - 10,
                uiViewport.getWorldHeight() - heartSize - 10, heartSize, heartSize));
        }
    }

    private void resetAsteroidSpawnTimer() {
        asteroidSpawnTimer = 0;
        timeToNextAsteroid = MathUtils.random(MIN_ASTEROID_SPAWN_TIME, MAX_ASTEROID_SPAWN_TIME);
    }

    private void spawnAsteroid() {
        // Tamaño aleatorio del asteroide
        float size = MathUtils.random(20f, 60f);
        // Aparece fuera de la pantalla a la derecha
        float startX = gameViewport.getWorldWidth() + size;
        // Posición Y aleatoria
        float startY = MathUtils.random(0, gameViewport.getWorldHeight() - size);
        // Velocidad de movimiento aleatoria hacia la izquierda
        float speed = MathUtils.random(100f, 200f);
        // Se instancia el nuevo asteroide
        Asteroid newAsteroid = new Asteroid(startX, startY, size, size, speed);
        asteroids.add(newAsteroid);
        Gdx.app.log("MiniGameScreen", "Asteroide generado en: (" + startX + ", " + startY + ")");
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // Para pausar el juego durante la ejecución o quitar dicha pausa
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !isGameOver && !isGameWon) {
            // Alterna el estado de pausa para saber mejor el estado de la pausa
            isPaused = !isPaused;
            if (isPaused) {
                // Se pasa el control de interacciones al menu de pausa
                Gdx.input.setInputProcessor(uiStage);
                // Muestra el diálogo de pausa
                pauseDialog.show(uiStage);
                backgroundMusic.pause();
            } else {
                Gdx.input.setInputProcessor(uiStage);
                // Esconde el diálogo de pausa
                pauseDialog.hide();
            }
        }

        // Lógica de actualización del juego
        if (!isGameOver && !isGameWon && !isPaused ) {

            // Para ir aumentando el tiempo del juego
            gameTimeElapsed += delta;

            // Actualiza la barra de progresión
            progressBarAnimation.update(delta);

            // Actualiza la nave
            spaceship.update(delta, gameViewport.getWorldHeight());

            // Logica para los obstaculos
            asteroidSpawnTimer += delta;
            if (asteroidSpawnTimer >= timeToNextAsteroid) {
                spawnAsteroid();
                resetAsteroidSpawnTimer();
            }

            // Actualización de cada uno de los obstaculos
            Iterator<Asteroid> asteroidIterator = asteroids.iterator();
            while (asteroidIterator.hasNext()) {
                Asteroid asteroid = asteroidIterator.next();
                asteroid.update(delta);

                // Para cuando alguno choque con el jugador
                if (asteroid.isActive() && spaceship.getHitBox().overlaps(asteroid.getRectangle())) {
                    spaceship.takeDamage();
                    asteroid.deactivate();
                    updateHeartIcons();

                    // Para cuando el jugador no le quede vidas
                    if (spaceship.getLives() <= 0) {
                        isGameOver = true;
                        Gdx.input.setInputProcessor(uiStage);
                        setupResultDialog("GAME OVER", "¡Has perdido! Los asteroides te alcanzaron.");
                        resultDialog.show(uiStage);
                        backgroundMusic.pause();
                    }
                }

                // Cuando alguno salga de los límites de la pantalla se remueve
                if (!asteroid.isActive() || asteroid.isOffscreen(gameViewport.getWorldWidth())) {
                    asteroidIterator.remove();
                }
            }

            // Comprueba la condición de victoria
            if (gameTimeElapsed >= gameDuration && !isGameOver) {
                isGameWon = true;
                Gdx.input.setInputProcessor(uiStage);
                setupResultDialog("¡VICTORIA!", "¡Has sobrevivido a la lluvia de asteroides!");
                resultDialog.show(uiStage);
                backgroundMusic.pause();
            }

            // Renderizado del mundo del juego y sus elementos
            batch.setProjectionMatrix(gameViewport.getCamera().combined);
            batch.begin();
            batch.draw(backgroundTexture, 0, 0, gameViewport.getWorldWidth(), gameViewport.getWorldHeight());
            spaceship.draw(batch);
            for (Asteroid asteroid : asteroids) {
                asteroid.draw(batch);
            }
            batch.end();

//------------------------------------------------------------------------------------------//
            // Renderizado de las hitboxes
//            shapeRenderer.setProjectionMatrix(gameViewport.getCamera().combined);
//            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//            shapeRenderer.setColor(Color.RED);
//            spaceship.drawHitbox(shapeRenderer);
//            for (Asteroid asteroid : asteroids) {
//                asteroid.drawHitbox(shapeRenderer);
//            }
//            shapeRenderer.end();
//------------------------------------------------------------------------------------------//

            // Renderizado de la UI del juego (vidas, barra de progreso)
            batch.setProjectionMatrix(uiViewport.getCamera().combined);
            batch.begin();
            progressBarAnimation.draw(batch);
            for (Rectangle heart : heartIcons) {
                batch.draw(heartTexture, heart.x, heart.y, heart.width, heart.height);
            }
            batch.end();

        } else {
            // Si el juego está terminado (ganado/perdido) o pausado solo actualiza el stage
            uiStage.act(delta);
            uiStage.draw();
        }
    }

    // Mensaje para cuando el jugador gane
    private void setupResultDialog(String title, String message) {
        Label.LabelStyle customLabelStyle = new Label.LabelStyle();
        customLabelStyle.font = font;
        customLabelStyle.fontColor = (title.equals("¡VICTORIA!")) ? Color.GREEN : Color.RED;

        resultDialog = new Dialog(title, skin) {
            @Override
            protected void result(Object object) {
                Gdx.app.log("MiniGameResultDialog", "Dialog closed with result: " + object);
            }
        };

        Label resultText = new Label(message, customLabelStyle);
        resultText.setWrap(true);
        resultText.setAlignment(com.badlogic.gdx.utils.Align.center);

        resultDialog.getContentTable().pad(20).add(resultText).width(Gdx.graphics.getWidth() * 0.5f).row();

        TextButton backToMenuButton = new TextButton("Salir", skin);
        resultDialog.getButtonTable().padBottom(10).add(backToMenuButton).width(200).height(50).pad(5);

        backToMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("MiniGameScreen", "Exiting MiniGame to Main Menu");
                dispose();
                main.setScreen(new MainMenu(main));
            }
        });

        resultDialog.pack();
        resultDialog.setPosition(
            (Gdx.graphics.getWidth() - resultDialog.getWidth()) / 2,
            (Gdx.graphics.getHeight() - resultDialog.getHeight()) / 2
        );
        resultDialog.setModal(true);
        resultDialog.setKeepWithinStage(true);
    }


    // Dialogo del menu de pausa
    private void setupPauseDialog() {
        Label.LabelStyle customLabelStyle = new Label.LabelStyle();
        customLabelStyle.font = font;
        customLabelStyle.fontColor = Color.WHITE;

        pauseDialog = new Dialog("PAUSA", skin) {
            @Override
            protected void result(Object object) {
                // Cuando se reanuda el juego
                if (object.equals("resume")) {
                    isPaused = false;
                    Gdx.input.setInputProcessor(uiStage);

                    Gdx.app.log("MiniGameScreen", "Juego reanudado desde diálogo.");
                } else if (object.equals("mainMenu")) {
                    dispose();
                    main.setScreen(new MainMenu(main));
                } else if (object.equals("exit")) {
                    Gdx.app.exit();
                }
            }
        };

        // Texto del menu de pausa
        Label pauseText = new Label("Minijuego en pausa", customLabelStyle);
        pauseText.setAlignment(com.badlogic.gdx.utils.Align.center);
        pauseDialog.getContentTable().pad(20).add(pauseText).row();

        // Boton para reanudar
        TextButton resumeButton = new TextButton("Reanudar", skin);

        // Acción al reanudar
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                pauseDialog.hide();
                isPaused = false;
                Gdx.input.setInputProcessor(uiStage);
                backgroundMusic.play();
                Gdx.app.log("MiniGameScreen", "Juego reanudado desde botón 'Reanudar'.");
            }
        });

        // Acción para volver al menu
        TextButton mainMenuButton = new TextButton("Salir", skin);
        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                pauseDialog.hide();
                dispose();
                main.setScreen(new MainMenu(main));
            }
        });

        // Posicionamiento de los botones
        pauseDialog.getButtonTable().padBottom(10).add(resumeButton).width(200).height(50).pad(5).row();
        pauseDialog.getButtonTable().add(mainMenuButton).width(200).height(50).pad(5).row();


        pauseDialog.pack();
        pauseDialog.setPosition(
            (Gdx.graphics.getWidth() - pauseDialog.getWidth()) / 2,
            (Gdx.graphics.getHeight() - pauseDialog.getHeight()) / 2
        );
        pauseDialog.setModal(true);
        pauseDialog.setKeepWithinStage(true);
    }
    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        uiViewport.update(width, height, true);
        uiStage.getViewport().update(width, height, true);

        progressBarAnimation.getSprite().setPosition(uiViewport.getWorldWidth() / 2 - progressBarAnimation.getSprite().getWidth() / 2, uiViewport.getWorldHeight() - 30);
        updateHeartIcons();
        if (resultDialog != null) {
            resultDialog.setPosition(
                (width - resultDialog.getWidth()) / 2,
                (height - resultDialog.getHeight()) / 2
            );
        }
        if (pauseDialog != null) {
            pauseDialog.setPosition(
                (width - pauseDialog.getWidth()) / 2,
                (height - pauseDialog.getHeight()) / 2
            );
        }
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        backgroundTexture.dispose();
        backgroundMusic.dispose();
        spaceship.dispose();
        for (Asteroid asteroid : asteroids) {
            asteroid.dispose();
        }
        uiStage.dispose();
        skin.dispose();
        font.dispose();
        progressBarAnimation.dispose();
        heartTexture.dispose();
    }
}
