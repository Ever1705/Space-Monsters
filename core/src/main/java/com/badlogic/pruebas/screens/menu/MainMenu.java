package com.badlogic.pruebas.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.pruebas.entities.mainmenu.CometActor;
import com.badlogic.pruebas.entities.mainmenu.PlanetActor;
import com.badlogic.pruebas.ayudas.models.ScreenModel;
import com.badlogic.pruebas.Main;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.pruebas.screens.game.GameScreen;
import com.badlogic.pruebas.screens.game.MiniGameScreen;

public class MainMenu extends ScreenModel {

    private SpriteBatch batch;
    private Stage stage;
    private Skin skin;

    // Música de fondo
    private Music backgroundMusic;
    // Sonidos de botones
    private Sound buttonSound;
    private Sound closeSound;
    private Sound startSound;


    // Fondo de menu
    private Texture background;
    // Logo
    private Texture logoTexture;

    // Textura de planetas
    private Texture planetTexture1;
    private Texture planetTexture2;
    private Texture planetTexture3;

    // Textura de cometa
    private Texture cometTexture;


    // Listas para almacenar los planetas y cometas
    private List<PlanetActor> planets;
    private List<CometActor> comets;

    // Variables para la generación de cometas
    private float cometSpawnTimer;
    private float timeToNextComet;
    private final float MIN_COMET_SPAWN_TIME = 1.0f;
    private final float MAX_COMET_SPAWN_TIME = 3.0f;

    // Boton de creditos/info
    private TextButton infoExtraButton;

    // Para la ventana de diálogo
    private Dialog infoExtraDialog;
    private Dialog helpDialog;

    // Nuevos diálogos para las instrucciones de juego
    private Dialog gameInstructionsDialog;
    private Dialog miniGameInstructionsDialog;


    // Fuente aparte
    private BitmapFont otherFont;


    public MainMenu(Main main) {
        super(main);

        // Cargar música de fondo y sonidos
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/Sci-Fi 7 Loop.ogg"));
        backgroundMusic.setLooping(true);
        buttonSound = Gdx.audio.newSound(Gdx.files.internal("sounds/JDSherbert - Pixel UI SFX Pack - Cursor 3 (Square).ogg"));
        startSound = Gdx.audio.newSound(Gdx.files.internal("sounds/JDSherbert - Pixel UI SFX Pack - Select 2 (Square).ogg"));
        closeSound =Gdx.audio.newSound(com.badlogic.gdx.Gdx.files.internal("sounds/JDSherbert - Pixel UI SFX Pack - Cancel 2 (Saw).ogg"));

        // Tipografía aparte
        otherFont = new BitmapFont(Gdx.files.internal("fonts/PixelifySans-VariableFont_wght.fnt"));
        otherFont.getData().markupEnabled = true;
        otherFont.getLineHeight();
        otherFont.getData().setLineHeight(25f);
        otherFont.getData().scale(0.5f);


        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());

        // Tabla para la interfaz
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);

        // Carga de archivos
        skin = new Skin(Gdx.files.internal("UI/a.json"));
        background = new Texture("MenuAssets/Starfield_.png");
        logoTexture = new Texture("MenuAssets/icono_text.png");
        planetTexture1 = new Texture("MenuAssets/PlanetaMo.png");
        planetTexture2 = new Texture("MenuAssets/PlanetaAma.png");
        planetTexture3 = new Texture("MenuAssets/PlanetaNa.png");
        cometTexture = new Texture("MenuAssets/cometa.png");
        Image logo = new Image(logoTexture);

        // Inicalizacion de listas
        planets = new ArrayList<>();
        comets = new ArrayList<>();


        // Botones de la interfaz
        TextButton playButton = new TextButton("Iniciar", skin);
        TextButton miniGameButton = new TextButton("Minijuego", skin);
        TextButton helpButton = new TextButton("Ayuda", skin);
        TextButton exitButton = new TextButton("Salir", skin);

        // Planetas
        PlanetActor planet1 = new PlanetActor(planetTexture1, 200,
            Gdx.graphics.getHeight() - 150, 100, 100,
            30, 15, 60);
        planets.add(planet1);
        stage.addActor(planet1);
        planet1.setZIndex(0);

        PlanetActor planet2 = new PlanetActor(planetTexture2, Gdx.graphics.getWidth() - 250,
            Gdx.graphics.getHeight() / 2, 100, 100, -45,
            25, 40);
        planets.add(planet2);
        stage.addActor(planet2);
        planet2.setZIndex(0);

        PlanetActor planet3 = new PlanetActor(planetTexture3, 100, 100,
            60, 60, 60, 20, 80);
        planets.add(planet3);
        stage.addActor(planet3);
        planet3.setZIndex(0);


        // Se agrega el logo a la tabla
        table.add(logo).width(350).height(330).padBottom(40).colspan(3).row();
        logo.setZIndex(1);

        // Boton fuera del Table
        infoExtraButton = new TextButton("?", skin);
        infoExtraButton.setSize(50, 50);
        infoExtraButton.setPosition(Gdx.graphics.getWidth() - infoExtraButton.getWidth() - 20, 20);
        stage.addActor(infoExtraButton);

        // Botones de la tabla
        table.add(playButton).width(200).height(50).pad(10);
        table.add(helpButton).width(200).height(50).pad(10);
        table.add(miniGameButton).width(200).height(50).pad(10);

        table.row();
        table.add(exitButton).width(200).height(50).pad(10).colspan(3);

        // Cuando se presiona el boton de "Iniciar"
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("MainMenuScreen", "Botón 'Play' presionado. Mostrando instrucciones...");
                showGameInstructionsDialog();
                buttonSound.play();
            }
        });

        // Cuando se presiona el boton de "Ayuda"
        helpButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("MainMenuScreen", "Botón 'Ayuda' presionado");
                showHelpDialog(
                    "JUEGO PRINCIPAL:\n" +
                        "Movimiento : \nA (Izquierda) ; D (Derecha)\n ; W (Arriba) ; S(Abajo)\n" +
                        "Ataque: Barra espaciadora\n Curarse: E (Usar botiquín) ; R (Usar jugo)\n" +
                        "\nMINIJUEGO:\n" +
                        "Movimiento: ; W (Arriba) ; S(Abajo)");
                buttonSound.play();

            }
        });

        // Cuando se presiona el boton de "Minijuego"
        miniGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("MainMenuScreen", "Botón 'Minijuego' presionado. Mostrando instrucciones...");
                showMiniGameInstructionsDialog();
                buttonSound.play();

            }
        });

        // Cuando se presiona el boton de "Salir"
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("MainMenuScreen", "Botón 'Salir' presionado");
                Gdx.app.exit();
            }
        });

        // Cuando se presiona el boton de "?"
        infoExtraButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("MainMenuScreen", "Botón 'Información Extra' presionado");
                showExtraDialog("Creditos:\n" +
                    "Dibujo y diseño : Marian Castillo\n Programación y desarrollo : Ever Alonzo\n\nGracias por jugar nuestro Juego :)");
                buttonSound.play();
            }
        }

        );


        // Inicializa el temporizador de cometas
        resetCometSpawnTimer();

   }


    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(stage);
        System.out.println("Main Menu is shown.");
        // Reproducir música de fondo al mostrar el menú
        backgroundMusic.play();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // Para imprimir en pantalla con "Bath"
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        //Lógica de generación y eliminación de cometas
        cometSpawnTimer += delta;
        if (cometSpawnTimer >= timeToNextComet) {
            spawnComet();
            resetCometSpawnTimer();
        }

        // Se eliminan los cometas fuera de pantalla
        Iterator<CometActor> iterator = comets.iterator();
        while (iterator.hasNext()) {
            CometActor comet = iterator.next();
            if (comet.isOffscreen()) {
                comet.remove(); // Elimina el cometa del Stage
                iterator.remove(); // Elimina el cometa de la lista
            }
        }

        // Para iniciar los procesos del stage
        stage.act(delta);
        stage.draw();

    }



    // Para resetear el temporizador de aparición de cometas
    private void resetCometSpawnTimer() {
        cometSpawnTimer = 0;
        // Tiempo entre el minimo y maximo donde sale el siguiente cometa
        timeToNextComet = MathUtils.random(MIN_COMET_SPAWN_TIME, MAX_COMET_SPAWN_TIME);
    }

    // Para generar un nuevo cometa
    private void spawnComet() {
        // Posición X aleatoria en la parte superior de la pantalla (un poco fuera)
        float startX = MathUtils.random(-50, Gdx.graphics.getWidth() + 50);
        // Posición Y un poco por encima de la pantalla
        float startY = Gdx.graphics.getHeight() + MathUtils.random(50, 200);

        // Valores aleatorios para los cometas
        float size = MathUtils.random(30f, 80f); // Tamaño del cometa
        float speed = MathUtils.random(150f, 300f); // Velocidad de caída

        // Se instancia el cometa a la lista de cometas y al stage
        CometActor newComet = new CometActor(cometTexture, startX, startY, size, size, speed);
        comets.add(newComet);
        stage.addActor(newComet);
        newComet.setZIndex(2);
    }


    // Función para mostrar el diálogo de ayuda
    private void showHelpDialog(String text) {

        // Configuración para la tipografía personalizada
        Label.LabelStyle customLabelStyle = new Label.LabelStyle();
        customLabelStyle.font = otherFont;

        // Crea el diálogo solo una vez
        if (helpDialog == null) {
            helpDialog = new Dialog("Ayuda del Juego", skin) {
                @Override
                protected void result(Object object) {
                    Gdx.app.log("HelpDialog", "Diálogo de ayuda cerrado con resultado: " + object);
                }
            };

            // Contenido del diálogo: un Label con el texto de ayuda
            Label helpText = new Label(text, customLabelStyle);
            helpText.setWrap(true);
            helpText.setAlignment(com.badlogic.gdx.utils.Align.center);

            // Posiciona el boton dentro de la tabla
            helpDialog.getContentTable().pad(20).add(helpText).width(Gdx.graphics.getWidth() * 0.6f).row();

            // Botón para cerrar el diálogo
            TextButton closeButton = new TextButton("Cerrar", skin);
            helpDialog.getButtonTable().padBottom(10).add(closeButton).width(150).height(50);

            // Añade el evento al botón de cerrar dentro del diálogo
            closeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    helpDialog.hide();
                    closeSound.play();
                }
            });

            // Ajusta el tamaño del diálogo al contenido
            helpDialog.pack();
            helpDialog.setPosition(
                (Gdx.graphics.getWidth() - helpDialog.getWidth()) / 2,
                (Gdx.graphics.getHeight() - helpDialog.getHeight()) / 2
            );
            //esta parte bloquea lo de detrás para enfocarse en la pantalla
            helpDialog.setModal(true);
            helpDialog.setKeepWithinStage(true);
        }
        stage.addActor(helpDialog);
        helpDialog.show(stage);
    }

    // Función para mostrar el diálogo de info-extra
    private void showExtraDialog(String text) {

        // Configuración para la tipografía personalizada
        Label.LabelStyle customLabelStyle = new Label.LabelStyle();
        customLabelStyle.font = otherFont;
        customLabelStyle.fontColor = Color.YELLOW;

        // Crea el diálogo solo una vez
        if (infoExtraDialog == null) {
            infoExtraDialog = new Dialog("Información Extra", skin) {
                @Override
                protected void result(Object object) {
                    Gdx.app.log("InfoExtraDialog", "Diálogo de información extra cerrado con resultado: " + object);
                }
            };

            // Contenido del diálogo: un Label con el texto de ayuda
            Label helpText = new Label(text, customLabelStyle);
            helpText.setWrap(true);
            helpText.setAlignment(com.badlogic.gdx.utils.Align.center);

            infoExtraDialog.getContentTable().pad(20).add(helpText).width(Gdx.graphics.getWidth() * 0.6f).row();

            // Botón para cerrar el diálogo
            TextButton closeButton = new TextButton("Cerrar", skin);
            infoExtraDialog.getButtonTable().padBottom(10).add(closeButton).width(150).height(50);

            // Añade un listener al botón de cerrar dentro del diálogo
            closeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    infoExtraDialog.hide();
                    closeSound.play();

                }
            });

            infoExtraDialog.pack(); // Ajusta el tamaño del diálogo al contenido
            infoExtraDialog.setPosition(
                (Gdx.graphics.getWidth() - infoExtraDialog.getWidth()) / 2,
                (Gdx.graphics.getHeight() - infoExtraDialog.getHeight()) / 2
            );
            //esta parte bloquea lo de detrás para enfocarse en la pantalla
            infoExtraDialog.setModal(true);
            infoExtraDialog.setKeepWithinStage(true);
        }
        stage.addActor(infoExtraDialog);
        infoExtraDialog.show(stage);
    }

    // Muestra el diálogo para iniciar el juego principal
    private void showGameInstructionsDialog() {
        Label.LabelStyle customLabelStyle = new Label.LabelStyle();
        customLabelStyle.font = otherFont;
        customLabelStyle.fontColor = Color.WHITE;

        if (gameInstructionsDialog == null) {
            gameInstructionsDialog = new Dialog("Instrucciones del Juego Principal", skin) {
                @Override
                protected void result(Object object) {
                    Gdx.app.log("GameInstructionsDialog", "Diálogo de instrucciones de juego principal cerrado con resultado: " + object);
                }
            };

            // Contenido del dialogo
            Label gameInstructions = new Label(
                "¡Bienvenido al Juego Principal!\n\n" +
                    "Objetivo: Debes interceptar el meteoroide el cual alberga minerales valiosos y " +
                    "despejar la zona de los slimes invasivos para permitir la extracción de sus recursos.\n" +
                    "Controles : \nA (Izquierda) ; D (Derecha)\n ; W (Arriba) ; S(Abajo)\n" +
                    "Ataque: Barra espaciadora\n Curarse: E (Usar botiquín) ; R (Usar jugo)\n", customLabelStyle);
            gameInstructions.setWrap(true);
            gameInstructions.setAlignment(com.badlogic.gdx.utils.Align.center);

            gameInstructionsDialog.getContentTable().pad(20).add(gameInstructions).width(Gdx.graphics.getWidth() * 0.7f).row();

            TextButton startButton = new TextButton("Iniciar", skin);
            TextButton backButton = new TextButton("Volver", skin);

            gameInstructionsDialog.getButtonTable().padBottom(10).add(startButton).width(150).height(50).pad(10);
            gameInstructionsDialog.getButtonTable().add(backButton).width(150).height(50).pad(10);

            // Para inciar el juego
            startButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    gameInstructionsDialog.hide();
                    main.setScreen(new GameScreen(main)); // Va a GameScreen
                }
            });

            // Para volver al menu
            backButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    gameInstructionsDialog.hide();
                    closeSound.play();

                }
            });

            gameInstructionsDialog.pack();
            gameInstructionsDialog.setPosition(
                (Gdx.graphics.getWidth() - gameInstructionsDialog.getWidth()) / 2,
                (Gdx.graphics.getHeight() - gameInstructionsDialog.getHeight()) / 2
            );
            gameInstructionsDialog.setModal(true);
            gameInstructionsDialog.setKeepWithinStage(true);
        }
        stage.addActor(gameInstructionsDialog);
        gameInstructionsDialog.show(stage);
    }

    // Muestra el diálogo para iniciar el minijuego (sique la misma logica que el principal)
    private void showMiniGameInstructionsDialog() {
        Label.LabelStyle customLabelStyle = new Label.LabelStyle();
        customLabelStyle.font = otherFont;
        customLabelStyle.fontColor = Color.WHITE;

        if (miniGameInstructionsDialog == null) {
            miniGameInstructionsDialog = new Dialog("Instrucciones del Minijuego", skin) {
                @Override
                protected void result(Object object) {
                    Gdx.app.log("MiniGameInstructionsDialog", "Diálogo de instrucciones de minijuego cerrado con resultado: " + object);
                }
            };

            Label miniGameInstructions = new Label(
                "¡Bienvenido al Minijuego!\n\n" +
                    "Objetivo: Esquiva los obstáculos del espacio. ¡Resiste hasta el final!\n\n" +
                    "Movimiento:\n" +
                    "  [W] (Arriba)\n" +
                    "  [S] (Abajo)\n\n" +
                    "¡No dejes que te golpeen!", customLabelStyle);
            miniGameInstructions.setWrap(true);
            miniGameInstructions.setAlignment(com.badlogic.gdx.utils.Align.center);

            miniGameInstructionsDialog.getContentTable().pad(20).add(miniGameInstructions).width(Gdx.graphics.getWidth() * 0.7f).row();

            TextButton startButton = new TextButton("Iniciar", skin);
            TextButton backButton = new TextButton("Volver", skin);

            miniGameInstructionsDialog.getButtonTable().padBottom(10).add(startButton).width(150).height(50).pad(10);
            miniGameInstructionsDialog.getButtonTable().add(backButton).width(150).height(50).pad(10);

            startButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    miniGameInstructionsDialog.hide();
                    main.setScreen(new MiniGameScreen(main));
                }
            });

            backButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    miniGameInstructionsDialog.hide();
                    closeSound.play();

                }
            });

            miniGameInstructionsDialog.pack();
            miniGameInstructionsDialog.setPosition(
                (Gdx.graphics.getWidth() - miniGameInstructionsDialog.getWidth()) / 2,
                (Gdx.graphics.getHeight() - miniGameInstructionsDialog.getHeight()) / 2
            );
            miniGameInstructionsDialog.setModal(true);
            miniGameInstructionsDialog.setKeepWithinStage(true);
        }
        stage.addActor(miniGameInstructionsDialog);
        miniGameInstructionsDialog.show(stage);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true);

        // Esto ayuda a mantener el botón "?", en la esquina
        float margin = 20;
        infoExtraButton.setPosition(width - infoExtraButton.getWidth() - margin, margin);

        // Ajusto la posición de las ventanas de diálogo
        if(infoExtraDialog != null) {
            infoExtraDialog.setPosition(
                (Gdx.graphics.getWidth() - infoExtraDialog.getWidth()) / 2,
                (Gdx.graphics.getHeight() - infoExtraDialog.getHeight()) / 2
            );
        }

        // mantener la posición del boton "Ayuda"
        if(helpDialog !=null){
            helpDialog.setPosition(
                (Gdx.graphics.getWidth() - helpDialog.getWidth()) / 2,
                (Gdx.graphics.getHeight() - helpDialog.getHeight()) / 2
            );
        }
        // Ajustar posición de los diálogos de instrucciones tanto del juego como del minijuego
        if(gameInstructionsDialog != null) {
            gameInstructionsDialog.setPosition(
                (Gdx.graphics.getWidth() - gameInstructionsDialog.getWidth()) / 2,
                (Gdx.graphics.getHeight() - gameInstructionsDialog.getHeight()) / 2
            );
        }
        if(miniGameInstructionsDialog != null) {
            miniGameInstructionsDialog.setPosition(
                (Gdx.graphics.getWidth() - miniGameInstructionsDialog.getWidth()) / 2,
                (Gdx.graphics.getHeight() - miniGameInstructionsDialog.getHeight()) / 2
            );
        }

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
    public void hide() {
        super.hide();
        Gdx.input.setInputProcessor(null);
        // Detener la música de fondo al salir del menú
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        stage.dispose();
        skin.dispose();
        background.dispose();
        logoTexture.dispose();
        planetTexture1.dispose();
        planetTexture2.dispose();
        planetTexture3.dispose();
        cometTexture.dispose();
        otherFont.dispose();
        backgroundMusic.dispose();
        buttonSound.dispose();
        startSound.dispose();

        if (infoExtraDialog != null) {
            infoExtraDialog.remove();
        }
        if (helpDialog != null) {
            helpDialog.remove();
        }
        if (gameInstructionsDialog != null) {
            gameInstructionsDialog.remove();
        }
        if (miniGameInstructionsDialog != null) {
            miniGameInstructionsDialog.remove();
        }
    }
}
