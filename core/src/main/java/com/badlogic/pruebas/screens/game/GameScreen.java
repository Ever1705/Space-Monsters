package com.badlogic.pruebas.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
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
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.pruebas.ayudas.hitboxes.RectangleHitBox;
import com.badlogic.pruebas.ayudas.loadTexture.LoadAnimationPng;
import com.badlogic.pruebas.ayudas.map.LoadTileMap;
import com.badlogic.pruebas.ayudas.models.ScreenModel;
import com.badlogic.pruebas.ayudas.utils.WorldCamara;
import com.badlogic.pruebas.Main;
import com.badlogic.pruebas.entities.Player;
import com.badlogic.pruebas.entities.Projectile;
import com.badlogic.pruebas.entities.SlimeModel;
import com.badlogic.pruebas.entities.slimes.BossSlime;
import com.badlogic.pruebas.entities.slimes.SlimeA;
import com.badlogic.pruebas.entities.slimes.SlimeN;
import com.badlogic.pruebas.screens.menu.MainMenu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameScreen extends ScreenModel {

    // Para la impresión/dibujo en pantalla
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    // Para crear el mundo
    private OrthogonalTiledMapRenderer mapRenderer;
    private LoadTileMap tileMap;

    // Camara del mundo
    private WorldCamara worldCamara;

    // Viewport
    private Viewport viewport;

    // Hitboxes de objetos solidos y paredes
    private List<Rectangle> solidHitbox;
    private boolean caminoBloqueado;

    //Sonido y musica
    private Music backgroundMusic;




    // Jugador
    private Player player;

    //Slimes
    private SlimeA slimeA_a;
    private SlimeN slimeN_a;

    private SlimeA slimeA_b;
    private SlimeN slimeN_b;

    private BossSlime bossSlime;


    // Listas para gestionar los Slimes generados por el método spawnSlimeWave
    private List<SlimeN> spawnedSlimeNList;
    private List<SlimeA> spawnedSlimeAList;

    // Acumuladores de cada lista de slimes
    private int slimesNParaSpawn;
    private int slimesAParaSpawn;

    // Contador de slimes eliminados para desbloquear el camino
    private int slimesEliminadosCount;


    //Pared de slimes
    private LoadAnimationPng paredSlimes;
    private LoadAnimationPng paredSlimesB;
    private LoadAnimationPng paredSlimesC;
    private LoadAnimationPng paredSlimesD;
    private LoadAnimationPng paredSlimesE;


    // Para la interfaz de juego:

    // Para texturas y sprites para la interfaz de juego:
    private Texture uiPlayerWindow;
    private Sprite sUiPlayerWindow;

    private Texture uiPlayer;
    private Sprite sUiPlayer;

    private Texture uiPlayerRoto;
    private Texture uiKit;
    private Sprite sUiKit;

    private Texture uiJuice;
    private Sprite sUiJuice;

    private Texture uiJuiceEmpty;

    private Texture uiCorazon;
    private Sprite uiVida;

    private Texture uiCorazon2;
    private Texture uiCorazon3;
    private Texture uiCorazonMuerto;

    // Para la impresión de la interfaz
    private SpriteBatch uiBatch;
    private Viewport uiViewport;

    // Menu de pausa
    private boolean paused;
    private Stage pauseStage;
    private Skin skin; // Reusing the skin from MainMenu for consistency
    private Dialog pauseDialog;
    private BitmapFont pauseFont;

    // Menu de gameOver
    private boolean isGameOver;
    private Stage gameOverStage;
    private Dialog gameOverDialog;
    private BitmapFont gameOverFont;

    // Para cuando se derrote al jefe y se gane la partida
    private boolean isBossDefeated;
    private Stage bossDefeatedStage;
    private Dialog bossDefeatedDialog;
    private BitmapFont bossDefeatedFont;

    // Lista para el manejo de projectiles
    List<Projectile> gameProjectiles;


    public GameScreen(Main main) {
        super(main);

//------------------------------------------------------------------------------------------//
        // Control de impresión/dibujo en pantalla
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        // Se carga el tilemap
        tileMap = new LoadTileMap("ASSETS/mundo/mundo.tmx", 1f);
        mapRenderer = new OrthogonalTiledMapRenderer(tileMap.getTiledMap(), tileMap.getRenderScale());
        // Camara para el mundo y el viewport
        worldCamara = new WorldCamara();
        // Viewport para el mundo
        viewport = new FitViewport(200, 120, worldCamara.getCamera());

        // música y efectos de sonido
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/future.ogg"));
        backgroundMusic.setLooping(true);


//------------------------------------------------------------------------------------------//
        // Jugador
        player = new Player();
        //16*1.85f
        player.getPosition().set(16*1.85f ,70);
        player.loadAllAnimations(0.001f);


//------------------------------------------------------------------------------------------//

        //SlimesA
        slimeA_a = new SlimeA(16*15.5f, 16*4.5f);
        slimeA_a.loadAllAnimation(0.7f);

        slimeA_b = new SlimeA(16*33.5f,8);
        slimeA_b.loadAllAnimation(0.7f);

        // Le reduzco su área inicial para una primera impresión después se normaliza
        slimeA_a.detectionArea.getRectangle().setSize(80,180);
        slimeA_b.detectionArea.getRectangle().setSize(80,180);

        // SlimesN
        slimeN_a = new SlimeN(16*23.5f, 8);
        slimeN_a.loadAllAnimation(0.7f);

        slimeN_b = new SlimeN(16*34.5f, 8);
        slimeN_b.loadAllAnimation(0.7f);
        // Le reduzco su área inicial para una primera impresión después se normaliza
        slimeN_a.detectionArea.getRectangle().setSize(80,180);
        slimeN_b.detectionArea.getRectangle().setSize(112,180);


        // Lista de proyectiles para el mundo
        gameProjectiles = new ArrayList<>();

        // Inicialización del jefe slime
        bossSlime = new BossSlime(1108,40,gameProjectiles);
        bossSlime.loadAllAnimation(2);


//------------------------------------------------------------------------------------------//

        // Inicialización de lo requerido para la pared de slimes
        spawnedSlimeNList = new ArrayList<>();
        spawnedSlimeAList = new ArrayList<>();
        // Contador de slimes derrotados
        slimesEliminadosCount = 0;
        // Contadores para la aparición de los slimes
        slimesNParaSpawn = 0;
        slimesAParaSpawn = 0;

        // Texturas que conforman la pared de slimes
        paredSlimes = new LoadAnimationPng("ASSETS/mundo/paredSlime.png",1,6,0.1f,true);
        paredSlimes.getSprite().setPosition(870,-10);
        paredSlimesB = new LoadAnimationPng("ASSETS/mundo/paredSlime.png",1,6,0.1f,true);
        paredSlimesB.getSprite().setPosition(870,10);
        paredSlimesC = new LoadAnimationPng("ASSETS/mundo/paredSlime.png",1,6,0.1f,true);
        paredSlimesC.getSprite().setPosition(870,30);
        paredSlimesD = new LoadAnimationPng("ASSETS/mundo/paredSlime.png",1,6,0.1f,true);
        paredSlimesD.getSprite().setPosition(870,50);
        paredSlimesE = new LoadAnimationPng("ASSETS/mundo/paredSlime.png",1,6,0.1f,true);
        paredSlimesE.getSprite().setPosition(870,70);

//------------------------------------------------------------------------------------------//

        // Para definir las hitbox de objetos solidos y paredes
        solidHitbox = new ArrayList<>();
        caminoBloqueado = true;

        // Se agregan correspondiente a lo requerido
        // Pared Superior
        solidHitbox.add(new RectangleHitBox(0, 16 * 6 + 7, tileMap.getMapWidth(), 16).getRectangle());

        // Pared inferior
        solidHitbox.add(new RectangleHitBox(0, -15, tileMap.getMapWidth(), 16).getRectangle());

        //Pared izquierda
        solidHitbox.add(new RectangleHitBox(-15, 0, 16, 110).getRectangle());

        // Pared derecha
        solidHitbox.add(new RectangleHitBox(tileMap.getMapWidth() - 14, 0, 16, 110).getRectangle());

        // Cohete
        solidHitbox.add(new RectangleHitBox(16 + 3, 16 * 5, 34, 16).getRectangle());

        // Bloquer el paso
        solidHitbox.add(new RectangleHitBox(895,0,10,105).getRectangle());

        // Hago aparecer unos slimes en el muro de slimes
        spawnSlime(1,1);
//------------------------------------------------------------------------------------------//
                // Interfaz
        uiPlayerWindow = new Texture("ASSETS/player_ui/playerWindow.png");
        uiPlayer = new Texture("ASSETS/player_ui/ui_front.png");
        uiPlayerRoto = new Texture("ASSETS/player_ui/ui_front_D.png");
        uiKit = new Texture("ASSETS/player_ui/Botiquin.png");
        uiJuice = new Texture("ASSETS/player_ui/posion_full.png");
        uiJuiceEmpty = new Texture("ASSETS/player_ui/posion_vacio.png");


        uiCorazon = new Texture("ASSETS/player_ui/Vida1.png");
        uiCorazon2 = new Texture("ASSETS/player_ui/Vida2.png");
        uiCorazon3 = new Texture("ASSETS/player_ui/Vida3.png");
        uiCorazonMuerto = new Texture("ASSETS/player_ui/VidaMuerto.png");

        uiVida = new Sprite(uiCorazon);
        uiVida.setScale(3);

        sUiPlayerWindow = new Sprite(uiPlayerWindow);
        sUiPlayerWindow.setScale(4);

        sUiPlayer = new Sprite(uiPlayer);
        sUiPlayer.setScale(4);

        sUiKit = new Sprite(uiKit);
        sUiKit.setScale(0.8f);

        sUiJuice = new Sprite(uiJuice);
        sUiJuice.setScale(2.5f);

        // Inicialización del SpriteBatch y Viewport para la UI
        uiBatch = new SpriteBatch();
        uiViewport = new ScreenViewport();
//------------------------------------------------------------------------------------------//


        // Inicializa lo necesario para la pantalla de pausa
        paused = false;
        pauseStage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("UI/a.json"));
        pauseFont = new BitmapFont(Gdx.files.internal("fonts/PixelifySans-VariableFont_wght.fnt"));
        pauseFont.getData().markupEnabled = true;
        pauseFont.getData().setLineHeight(25f);
        pauseFont.getData().scale(0.5f);

        setupPauseDialog();

        // Inicializa lo necesario para la pantalla de GameOver
        isGameOver = false;
        gameOverStage = new Stage(new ScreenViewport());
        // Para cuando se quiera usar otro tipo de fuente para el GameOver
        gameOverFont = pauseFont;

        setupGameOverDialog();

        // --- Inicializa lo necesario para la pantalla de Jefe Derrotado ---
        isBossDefeated = false;
        bossDefeatedStage = new Stage(new ScreenViewport());
        // Se reutiliza la fuente, pero también se puede usar otra diferente
        bossDefeatedFont = pauseFont;
        setupBossDefeatedDialog();
//------------------------------------------------------------------------------------------//



    }


    @Override
    public void show() {
        backgroundMusic.play();
    }

    // Para actualizar la todo lo que tenga que ver con el mundo y la vista
    public void update() {
//----------------------------------- DEVTOOLS -------------------------------------------------------//

        // Para controlar la camara libremente
//        worldCamara.controlCamara();
        // Para que siga al jugador en este caso
//------------------------------------------------------------------------------------------//

        worldCamara.camaraEntityFollow(player.getPosition().x + 30, player.getPosition().y);
        worldCamara.limitMoveCamara(tileMap.getMapWidth(), tileMap.getMapHeight());
        worldCamara.update();

        mapRenderer.setView(worldCamara.getCamera());
        batch.setProjectionMatrix(worldCamara.getCamera().combined);
        shapeRenderer.setProjectionMatrix(worldCamara.getCamera().combined);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // Botón de pausa
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            togglePause();
        }

        // Solo actualiza y renderiza todo cuando no este en pausa, en GameOver o con Jefe Derrotado
        if (!paused && !isGameOver && !isBossDefeated) {
            update();

            //Slimes update
            slimeA_a.update(delta,player);
            slimeN_a.update(delta,player);
            slimeA_b.update(delta,player);
            slimeN_b.update(delta,player);

            bossSlime.update(delta,player);


            // Player update
            player.update(delta,solidHitbox);
            uiStatesUpdate();


            // Actualización para las animaciones de la pared de slimes
            if (caminoBloqueado) {
                paredSlimes.update(delta);
                paredSlimesB.update(delta);
                paredSlimesC.update(delta);
                paredSlimesD.update(delta);
                paredSlimesE.update(delta);
            }

            // Se empieza con la lógica de los slimes iníciales
            logicInitialSlimes();

            // Realiza todo lo referente con los slimes que aparecen
            logicSpawnSlimes(delta);

            // Lógica de los ataques del jefe
            if (bossSlime.attackHitBox.isActive() && player.getHitBox().overlaps(bossSlime.attackHitBox.getRectangle())) {
                if (!player.isInvulnerable()) {
                    player.takeDamage();
                }
            }

            // Manejo de lógica de aparición
            handlePendingSpawns();


            // Cuando se elimine una cierta cantidad de slimes se abre el camino
            if (slimesEliminadosCount >= 5 && caminoBloqueado) {
                caminoBloqueado = false;
                // se elimina la hitbox de objetos sólidos
                solidHitbox.remove(5);
                Gdx.app.log("GameScreen", "Camino desbloqueado!");
            }

            // Para cuando el jugador pierda
            if (!player.isAlive && player.getCurrentAnimation().isAnimationFinished()){
                System.out.println("GAME OVER");
                isGameOver = true;
                Gdx.input.setInputProcessor(gameOverStage);
                gameOverDialog.show(gameOverStage);
            }

            // Lógica para cuando el jefe es derrotado y su animación de muerte termina
            if (!bossSlime.isAlive() && bossSlime.getSlimeState() == SlimeModel.SlimeState.DEATH &&
                bossSlime.getCurrentAnimation().isAnimationFinished() && !isBossDefeated) {
                Gdx.app.log("GameScreen", "¡Jefe Derrotado!");
                isBossDefeated = true;
                Gdx.input.setInputProcessor(bossDefeatedStage);
                bossDefeatedDialog.show(bossDefeatedStage);
            }


            // Esta es la capa del fondo
            mapRenderer.render(new int[]{tileMap.getTiledMap().getLayers().getIndex("Capa de patrones 1")});
            // Esta es la capa de decoraciones
            mapRenderer.render(new int[]{tileMap.getTiledMap().getLayers().getIndex("Capa de patrones 1.5")});


// ---------------------------- DEVTOOLS -------------------------------------------//
//            if(Gdx.input.isKeyPressed(Input.Keys.Q))
//                if ( !player.isInvulnerable()) {
//                    player.takeDamage();
////                caminoBloqueado = false;
////                // El índice coincide con el agregado que en este caso es la pared de bloqueo
////                solidHitbox.remove(5);
//                }
//
//            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
//                spawnSlime(0, 1); // Genera 1 SlimeN y 1 SlimeA
//            }
//------------------------------------------------------------------------------------------//


            Iterator<Projectile> projectileIterator = gameProjectiles.iterator();
            while (projectileIterator.hasNext()) {
                Projectile projectile = projectileIterator.next();
                projectile.update(delta);

                // Comprobar colisiones del proyectil con el jugador
                if (projectile.isActive() && player.isAlive && projectile.hitBox.overlaps(player.getHitBox().getRectangle())) {
                    player.takeDamage();
                    // Desactiva el proyectil al impactar
                    projectile.deactivate();
                }
                if (!projectile.isActive()) {
                    // Elimina los proyectiles inactivos
                    projectileIterator.remove();
                }
            }

//------------------------------------------------------------------------------------------//
            // Impresión de cada entidad y objeto
            batch.begin();

            // Para la pared de slimes
            if (caminoBloqueado) {
                paredSlimes.draw(batch);
                paredSlimesB.draw(batch);
                paredSlimesC.draw(batch);
                paredSlimesD.draw(batch);
                paredSlimesE.draw(batch);
            }

            //Slimes iniciales
            slimeA_a.draw(batch);
            slimeN_a.draw(batch);
            slimeA_b.draw(batch);
            slimeN_b.draw(batch);
            bossSlime.draw(batch);

            // Slimes de spawn
            for (SlimeN slimeN : spawnedSlimeNList) {
                slimeN.draw(batch);
            }
            for (SlimeA slimeA : spawnedSlimeAList) {
                slimeA.draw(batch);
            }

            // Jugador
            player.draw(batch);


            for (Projectile projectile : gameProjectiles) {
                projectile.draw(batch);
            }

            batch.end();

//------------------------------------------------------------------------------------------//

            // Esta capa contiene los objetos se superponen al jugador
            mapRenderer.render(new int[]{tileMap.getTiledMap().getLayers().getIndex("Capa de patrones 2")});


//------------------------------------------------------------------------------------------//
            // Se dibuja la interfaz
            uiBatch.setProjectionMatrix(uiViewport.getCamera().combined); // Establecer la matriz de proyección del UI
            uiBatch.begin();

            // Obtener las dimensiones del Viewport de la UI
            float uiWidth = uiViewport.getWorldWidth();
            float uiHeight = uiViewport.getWorldHeight();


            float padding = 10; // Puedes ajustar este valor
            float heartWidth = uiCorazon.getWidth();
            float heartHeight = uiCorazon.getHeight();


            sUiPlayerWindow.setPosition(padding+50, uiHeight - heartHeight - padding-50);
            sUiPlayerWindow.draw(uiBatch);

            sUiPlayer.setPosition(padding + 55, uiHeight - heartHeight - padding - 45);
            sUiPlayer.draw(uiBatch);

            sUiKit.setPosition(padding + 160, uiHeight - heartHeight - padding - 90);
            sUiKit.draw(uiBatch);

            sUiJuice.setPosition(padding + 135, uiHeight - heartHeight - padding - 72);
            sUiJuice.draw(uiBatch);

            uiVida.setPosition(padding + 135, uiHeight - heartHeight - padding - 20);
            uiVida.draw(uiBatch);


            uiBatch.end();

//------------------------------ DEVTOOLS ------------------------------------------------------------//

//       Muestra las hitboxes que se definan dentro de aquí
//            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//
//            //Slimes
//            slimeA_a.drawHitbox(shapeRenderer, Color.BLUE);
//            slimeN_a.drawHitbox(shapeRenderer, Color.BLUE);
//
//            slimeA_b.drawHitbox(shapeRenderer, Color.BLUE);
//            slimeN_b.drawHitbox(shapeRenderer, Color.BLUE);
//
//            bossSlime.drawHitbox(shapeRenderer,Color.RED);
//
//            for (SlimeN slimeN : spawnedSlimeNList) {
//                slimeN.drawHitbox(shapeRenderer, Color.BLUE);
//            }
//            for (SlimeA slimeA : spawnedSlimeAList) {
//                slimeA.drawHitbox(shapeRenderer, Color.BLUE);
//            }
//
//            // Player
//            player.drawHitBox(shapeRenderer, Color.CYAN);
//

//            for (Rectangle hitBoxes : solidHitbox) {
//                shapeRenderer.setColor(Color.CORAL);
//                shapeRenderer.rect(hitBoxes.x, hitBoxes.y, hitBoxes.width, hitBoxes.height);
//            }
//
//            for (Projectile projectile : gameProjectiles) {
//                projectile.drawHitbox(shapeRenderer, Color.CYAN); // O el color que quieras
//            }
//
//            shapeRenderer.end();
//------------------------------------------------------------------------------------------//

        }
        // Cuando la pantalla esta en pausa
        else if (paused) {
            pauseStage.act(delta);
            pauseStage.draw();
        }
        // Cuando la pantalla esta en GameOver
        else if (isGameOver) {
            gameOverStage.act(delta);
            gameOverStage.draw();
            backgroundMusic.pause();

        }
        // Cuando la pantalla de Jefe Derrotado está activa
        else if (isBossDefeated) {
            bossDefeatedStage.act(delta);
            bossDefeatedStage.draw();
            backgroundMusic.pause();

        }

    }

    // Se actualiza el estado de la interfaz dinamicamente
    private void uiStatesUpdate() {
        if(player.getSalud() >=3){
            uiVida.setTexture(uiCorazon);
        }
        else if (player.getSalud() == 2){
            uiVida.setTexture(uiCorazon2);
        } else if (player.getSalud() == 1){
            uiVida.setTexture(uiCorazon3);
        } else if (player.getSalud() <= 0) {
            uiVida.setTexture(uiCorazonMuerto);
        }

        // Para cuando se use el botiquin
        if (player.isUsingKit()){
            sUiKit.setAlpha(.5f);
        }
        // Para cuando se use el jugo
        if (player.isUsingJuice()){
            sUiJuice.setTexture(uiJuiceEmpty);
            sUiJuice.setAlpha(.5f);
        }

        // Para mostrar de manera dinámica el estado del personaje
        if (player.getSalud() < 2){
            sUiPlayer.setTexture(uiPlayerRoto);
        }else{
            sUiPlayer.setTexture(uiPlayer);
        }
    }


    // Lógica de los slimes iniciales
    private void logicInitialSlimes() {
        // Esto es para cuando el jugador esté cerca de lo slimes estos vuelvan a tener sus hitbox de detección normal
        if (slimeA_a.detectionArea.overlaps(player.hitBox.getRectangle())){
            slimeA_a.detectionArea.getRectangle().setSize(180,180);
        }
        if (slimeA_b.detectionArea.overlaps(player.hitBox.getRectangle())){
            slimeA_b.detectionArea.getRectangle().setSize(180,180);
        }
        if(slimeN_a.detectionArea.overlaps(player.hitBox.getRectangle())){
            slimeN_a.detectionArea.getRectangle().setSize(180,180);
        }
        if(slimeN_b.detectionArea.overlaps(player.hitBox.getRectangle())){
            slimeN_b.detectionArea.getRectangle().setSize(180,180);
        }


        // Para los SlimeA a y b
        if (slimeN_a.attackHitBox.isActive() && player.getHitBox().overlaps(slimeN_a.attackHitBox.getRectangle())) {
            if (!player.isInvulnerable()) {
                player.takeDamage();
            }
        }
        if (slimeN_b.attackHitBox.isActive() && player.getHitBox().overlaps(slimeN_b.attackHitBox.getRectangle())) {
            if (!player.isInvulnerable()) {
                player.takeDamage();
            }
        }

        // Para los SlimeA a y b
        if (slimeA_a.attackHitBox.isActive() && player.getHitBox().overlaps(slimeA_a.attackHitBox.getRectangle())) {
            if (!player.isInvulnerable()) {
                player.takeDamage();
            }
        }
        if (slimeA_b.attackHitBox.isActive() && player.getHitBox().overlaps(slimeA_b.attackHitBox.getRectangle())) {
            if (!player.isInvulnerable()) {
                player.takeDamage();
            }
        }
    }


    // Método para ir generando los Slimes
    public void spawnSlime(int numSlimeN, int numSlimeA) {

        float minY = 8f;
        float maxY = 16 * 4.4f;

        Gdx.app.log("GameScreen", "Generando Slimes");

        // Generar SlimeN
        for (int i = 0; i < numSlimeN; i++) {
            float spawnX = 880;
            float spawnY = minY + (float) (Math.random() * (maxY - minY));
            SlimeN newSlimeN = new SlimeN(spawnX, spawnY);
            newSlimeN.loadAllAnimation(0.7f);
            spawnedSlimeNList.add(newSlimeN);
        }

        // Generar SlimeA
        for (int i = 0; i < numSlimeA; i++) {
            float spawnX = 880;
            float spawnY = minY + (float) (Math.random() * (maxY - minY));
            SlimeA newSlimeA = new SlimeA(spawnX, spawnY);
            newSlimeA.loadAllAnimation(0.7f);
            spawnedSlimeAList.add(newSlimeA);

        }
    }

    // Lógica para los slimes que aparecen
    private void logicSpawnSlimes(float delta) {
        // Para los SlimesN
        Iterator<SlimeN> spawnedSlimeNIterator = spawnedSlimeNList.iterator();
        while (spawnedSlimeNIterator.hasNext()) {
            SlimeN slimeN = spawnedSlimeNIterator.next();
            slimeN.update(delta, player);
            if (!slimeN.isAlive()) {
                if (slimeN.getSlimeState() == SlimeModel.SlimeState.DEATH &&slimeN.getCurrentAnimation().isAnimationFinished()) {
                    spawnedSlimeNIterator.remove();
                    slimesEliminadosCount++;
                    // Se agrega un slime para aparecer
                    slimesNParaSpawn++;
                }
            }
            if (slimeN.attackHitBox.isActive() && player.getHitBox().overlaps(slimeN.attackHitBox.getRectangle())) {
                if (!player.isInvulnerable()) {
                    player.takeDamage();
                }
            }
        }

        // Para los SlimesA
        Iterator<SlimeA> spawnedSlimeAIterator = spawnedSlimeAList.iterator();
        while (spawnedSlimeAIterator.hasNext()) {
            SlimeA slimeA = spawnedSlimeAIterator.next();
            slimeA.update(delta, player);
            if (!slimeA.isAlive()) {
                if (slimeA.getSlimeState() == SlimeModel.SlimeState.DEATH && slimeA.getCurrentAnimation().isAnimationFinished()) {
                    spawnedSlimeAIterator.remove();
                    slimesEliminadosCount++;

                    // Se agrega un slime para aparecer
                    slimesAParaSpawn++;

                }
            }
            if (slimeA.attackHitBox.isActive() && player.getHitBox().overlaps(slimeA.attackHitBox.getRectangle())) {
                if (!player.isInvulnerable()) {
                    player.takeDamage();
                }
            }
        }
    }


    // Maneja la lógica de aparición de los slimes
    private void handlePendingSpawns() {

        if (slimesEliminadosCount < 4) {

            //Se manejan las logicas para ambas listas

            if (slimesNParaSpawn > 0) {
                orderSpawnSlime();
                // Resetea el contador
                slimesNParaSpawn = 0;
            }

            if (slimesAParaSpawn > 0) {
                orderSpawnSlime();
                // Resetea el contador
                slimesAParaSpawn = 0;
            }
        }
    }

    // Método para el orden para que aparezcan los slimes al momento que un slime muera
    private void orderSpawnSlime(){
        if (slimesEliminadosCount == 1){
            spawnSlime(0, 1);
        }else if (slimesEliminadosCount == 2){
            spawnSlime(1, 0);
        }else if (slimesEliminadosCount == 3){
            spawnSlime(0, 1);
        }else if (slimesEliminadosCount == 4){
            spawnSlime(0, 1);
        }
    }

    // Lógica de pausa del juego
    private void togglePause() {
        paused = !paused;
        // Para cuando se pausa el juego
        if (paused) {
            Gdx.app.log("GameScreen", "Game Paused");
            Gdx.input.setInputProcessor(pauseStage);
            pauseDialog.show(pauseStage);
            backgroundMusic.pause();
        }
        // Para continuar el juego
        else {
            Gdx.app.log("GameScreen", "Game Resumed");
            Gdx.input.setInputProcessor(null);
            pauseDialog.hide();
            backgroundMusic.play();

        }
    }

    // Muestra la pantalla de pausa
    private void setupPauseDialog() {
        Label.LabelStyle customLabelStyle = new Label.LabelStyle();
        customLabelStyle.font = pauseFont;
        customLabelStyle.fontColor = Color.WHITE;

        // Pantalla de pausa
        pauseDialog = new Dialog("Juego Pausado", skin) {
            @Override
            protected void result(Object object) {
            }
        };

        // Mensaje de pausa
        Label pauseText = new Label("El juego está en pausa.", customLabelStyle);
        pauseText.setWrap(true);
        pauseText.setAlignment(com.badlogic.gdx.utils.Align.center);

        pauseDialog.getContentTable().pad(20).add(pauseText).width(Gdx.graphics.getWidth() * 0.4f).row();

        TextButton resumeButton = new TextButton("Reanudar", skin);
        TextButton exitToMenuButton = new TextButton("Salir", skin);

        pauseDialog.getButtonTable().padBottom(10).add(resumeButton).width(150).height(50).pad(5).row();
        pauseDialog.getButtonTable().add(exitToMenuButton).width(150).height(50).pad(5);

        // Acción para continuar
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                togglePause();
            }
        });

        // Acción de salida
        exitToMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                main.setScreen(new MainMenu(main));
            }
        });

        pauseDialog.pack();
        // Centra la pantalla pausa
        pauseDialog.setPosition(
            (Gdx.graphics.getWidth() - pauseDialog.getWidth()) / 2,
            (Gdx.graphics.getHeight() - pauseDialog.getHeight()) / 2
        );
        pauseDialog.setModal(true);
        pauseDialog.setKeepWithinStage(true);
    }

    // Muestra la pantalla de Game Over
    private void setupGameOverDialog() {
        Label.LabelStyle customLabelStyle = new Label.LabelStyle();
        customLabelStyle.font = gameOverFont;
        customLabelStyle.fontColor = Color.WHITE;

        // Ventana de ganeOver
        gameOverDialog = new Dialog("GAME OVER", skin) {
            @Override
            protected void result(Object object) {
            }
        };

        // Mensaje de gameOver
        Label gameOverText = new Label("¡Misión fallida! ¿Quieres intentarlo de nuevo?", customLabelStyle);
        gameOverText.setWrap(true);
        gameOverText.setAlignment(com.badlogic.gdx.utils.Align.center);

        gameOverDialog.getContentTable().pad(20).add(gameOverText).width(Gdx.graphics.getWidth() * 0.4f).row();

        TextButton retryButton = new TextButton("Reintentar", skin);
        TextButton exitToMenuButton = new TextButton("Salir", skin);

        gameOverDialog.getButtonTable().padBottom(10).add(retryButton).width(150).height(50).pad(5).row();
        gameOverDialog.getButtonTable().add(exitToMenuButton).width(150).height(50).pad(5);


        // Acción del botón para reiniciar
        retryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                main.setScreen(new GameScreen(main));
                backgroundMusic.stop();
            }
        });

        // Acción del botón para salir
        exitToMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                main.setScreen(new MainMenu(main));
            }
        });

        gameOverDialog.pack();
        // Centra la pantalla de gameOver
        gameOverDialog.setPosition(
            (Gdx.graphics.getWidth() - gameOverDialog.getWidth()) / 2,
            (Gdx.graphics.getHeight() - gameOverDialog.getHeight()) / 2
        );
        gameOverDialog.setModal(true);
        gameOverDialog.setKeepWithinStage(true);
    }

    // Pantalla a la hora de derrotar al jefe y ganar
    private void setupBossDefeatedDialog() {
        Label.LabelStyle customLabelStyle = new Label.LabelStyle();
        customLabelStyle.font = bossDefeatedFont;
        customLabelStyle.fontColor = Color.GREEN;

        bossDefeatedDialog = new Dialog("¡VICTORIA!", skin) {
            @Override
            protected void result(Object object) {
            }
        };

        Label defeatedText = new Label("¡Felicidades! \n¡Has completado la misión con éxito!", customLabelStyle);
        defeatedText.setWrap(true);
        defeatedText.setAlignment(com.badlogic.gdx.utils.Align.center);

        bossDefeatedDialog.getContentTable().pad(20).add(defeatedText).width(Gdx.graphics.getWidth() * 0.4f).row();

        TextButton exitToMenuButton = new TextButton("Salir", skin);

        bossDefeatedDialog.getButtonTable().padBottom(10).add(exitToMenuButton).width(250).height(50).pad(5);

        exitToMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                main.setScreen(new MainMenu(main)); // Vuelve al menú principal
            }
        });

        bossDefeatedDialog.pack();
        bossDefeatedDialog.setPosition(
            (Gdx.graphics.getWidth() - bossDefeatedDialog.getWidth()) / 2,
            (Gdx.graphics.getHeight() - bossDefeatedDialog.getHeight()) / 2
        );
        bossDefeatedDialog.setModal(true);
        bossDefeatedDialog.setKeepWithinStage(true);
    }


    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        viewport.update(width, height, true);
        uiViewport.update(width, height, true);
        pauseStage.getViewport().update(width, height, true);
        gameOverStage.getViewport().update(width, height, true);
        bossDefeatedStage.getViewport().update(width, height, true);

        // Centra en todo momento la pantalla de pausa
        if (pauseDialog != null) {
            pauseDialog.setPosition(
                (width - pauseDialog.getWidth()) / 2,
                (height - pauseDialog.getHeight()) / 2
            );
        }
        // Centra en todo momento la pantalla de game over
        if (gameOverDialog != null) {
            gameOverDialog.setPosition(
                (width - gameOverDialog.getWidth()) / 2,
                (height - gameOverDialog.getHeight()) / 2
            );
        }
        // Centra en todo momento la pantalla de jefe derrotado
        if (bossDefeatedDialog != null) {
            bossDefeatedDialog.setPosition(
                (width - bossDefeatedDialog.getWidth()) / 2,
                (height - bossDefeatedDialog.getHeight()) / 2
            );
        }
    }

    @Override
    public void pause() {
        super.pause();
        if (!paused && !isGameOver && !isBossDefeated) {
            togglePause();
        }
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void hide() {
        super.hide();
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        mapRenderer.dispose();
        tileMap.dispose();

        backgroundMusic.dispose();

        paredSlimes.dispose();
        paredSlimesB.dispose();
        paredSlimesC.dispose();
        paredSlimesD.dispose();
        paredSlimesE.dispose();


        slimeA_a.dispose();
        slimeN_a.dispose();
        slimeA_b.dispose();
        slimeN_b.dispose();


        for (SlimeN slimeN : spawnedSlimeNList) {
            slimeN.dispose();
        }
        for (SlimeA slimeA : spawnedSlimeAList) {
            slimeA.dispose();
        }


        if (pauseStage != null) {
            pauseStage.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
        if (pauseFont != null) {
            pauseFont.dispose();
        }

        if (gameOverStage != null) {
            gameOverStage.dispose();
        }
        if (gameOverFont != null) {
            gameOverFont.dispose();
        }

        if (bossDefeatedStage != null) {
            bossDefeatedStage.dispose();
        }
        if (bossDefeatedFont != null) {
            bossDefeatedFont.dispose();
        }
    }
}
