package com.badlogic.pruebas.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

// Disposable es para liberar de la memoria elementos como las texturas
public class Player implements Disposable {

    // --- Enums para los estados y direcciones ---
    public enum PlayerState {
        IDLE, WALK, ATTACK, HURT, DEATH
    }

    public enum PlayerDirection {
        LEFT, RIGHT, FRONT
    }


    // Animación principal de las animaciones
    private LoadAnimationPng currentAnimation;


    private Sound soundKit;
    private Sound soundJuice;
    private Sound soundAttack;
    private Sound soundHurt;

    // Para la hitbox
    public RectangleHitBox hitBox;
    public RectangleHitBox moveHitbox;
    public RectangleHitBox hitBoxAttackLeft;
    public RectangleHitBox hitBoxAttackRight;
    public RectangleHitBox hitBoxAttackFront;

    // Salud del personaje
    private float salud;

    // Para la velocidad
    private float speed;
    public Vector2 position;
    public Vector2 velocity;

    // Para el ataque
    private boolean isAttacking = false;

    // Para saber si sigue vivo
    public boolean isAlive;
    private boolean usingKit;
    private boolean usingJuice;

    // Para los estados
    private PlayerDirection playerDirection;
    private PlayerState playerState;

    // Almacenamiento de animaciones usando EnumMap
    private EnumMap<PlayerState, EnumMap<PlayerDirection, LoadAnimationPng>> animations;

    // --- Variables para la invulnerabilidad ---
    private boolean isInvulnerable = false;
    private float invulnerabilityDuration = 1f; // Duración en segundos de la invulnerabilidad
    private float invulnerabilityTimer = 0f; // Temporizador para la invulnerabilidad


    public Player() {

        this.salud = 3;
        this.isAlive = true;
        this.usingKit = false;
        this.usingJuice = false;


        // Efectos de sonido
        this.soundKit = Gdx.audio.newSound(Gdx.files.internal("sounds/Retro Magic Protection 25.wav"));
        this.soundJuice = Gdx.audio.newSound(Gdx.files.internal("sounds/Retro Magic Protection 01.wav"));
        this.soundAttack = Gdx.audio.newSound(Gdx.files.internal("sounds/Retro Swooosh 07.wav"));
        this.soundHurt = Gdx.audio.newSound(Gdx.files.internal("sounds/RetroImpactPunchHurt 01.wav"));

        // Velocidad y coordenadas
        this.speed = 65f;
        this.position = new Vector2(0, 0);
        this.velocity = new Vector2();

        // Hitboxes
        this.hitBox = new RectangleHitBox(position.x, position.y, 11, 20);

        this.moveHitbox = new RectangleHitBox(position.x, position.y, 11, 5);

        this.hitBoxAttackLeft = new RectangleHitBox(position.x, position.y, 25, 20);
        this.hitBoxAttackLeft.active(false);

        this.hitBoxAttackRight = new RectangleHitBox(position.x, position.y, 26, 20);
        this.hitBoxAttackRight.active(false);

        this.hitBoxAttackFront = new RectangleHitBox(position.x, position.y, 27, 20);
        this.hitBoxAttackFront.active(false);


        // Dirección inicial y estado inicial
        this.playerDirection = PlayerDirection.FRONT;
        this.playerState = PlayerState.IDLE;
    }


    // Aquí cargo todas las animaciones y las guardo en un EnumMap (prácticamente una listo pero con Enums)
    public void loadAllAnimations(float scale) {
        animations = new EnumMap<>(PlayerState.class);

        // --- IDLE Animations ---
        EnumMap<PlayerDirection, LoadAnimationPng> idleAnims = new EnumMap<>(PlayerDirection.class);
        idleAnims.put(PlayerDirection.LEFT, new LoadAnimationPng("ASSETS/personaje/player_Idle_Left.png", 1, 7, 0.1f, true));
        idleAnims.get(PlayerDirection.LEFT).getSprite().scale(scale);
        idleAnims.put(PlayerDirection.RIGHT, new LoadAnimationPng("ASSETS/personaje/player_Idle_Right.png", 1, 7, 0.1f, true));
        idleAnims.get(PlayerDirection.RIGHT).getSprite().scale(scale);
        idleAnims.put(PlayerDirection.FRONT, new LoadAnimationPng("ASSETS/personaje/player_Idle_front.png", 1, 7, 0.1f, true));
        idleAnims.get(PlayerDirection.FRONT).getSprite().scale(scale);

        animations.put(PlayerState.IDLE, idleAnims);

        // --- WALK (RUN) Animations ---
        EnumMap<PlayerDirection, LoadAnimationPng> walkAnims = new EnumMap<>(PlayerDirection.class);
        walkAnims.put(PlayerDirection.LEFT, new LoadAnimationPng("ASSETS/personaje/player_Walk_Left.png", 1, 5, 0.1f, true));
        walkAnims.get(PlayerDirection.LEFT).getSprite().scale(scale);

        walkAnims.put(PlayerDirection.RIGHT, new LoadAnimationPng("ASSETS/personaje/player_Walk_Right.png", 1, 5, 0.1f, true));
        walkAnims.get(PlayerDirection.RIGHT).getSprite().scale(scale);

        walkAnims.put(PlayerDirection.FRONT, new LoadAnimationPng("ASSETS/personaje/player_Walk_Front.png", 1, 5, 0.1f, true));
        walkAnims.get(PlayerDirection.FRONT).getSprite().scale(scale);

        animations.put(PlayerState.WALK, walkAnims);

        // --- ATTACK animations ---
        // Como un araque no se repite de manera secuencial se deja en --false--
        EnumMap<PlayerDirection, LoadAnimationPng> attackAnims = new EnumMap<>(PlayerDirection.class);
        attackAnims.put(PlayerDirection.LEFT, new LoadAnimationPng("ASSETS/personaje/player_Attack_Left.png", 1, 6, 0.05f, true));
        attackAnims.get(PlayerDirection.LEFT).getSprite().scale(scale);

        attackAnims.put(PlayerDirection.RIGHT, new LoadAnimationPng("ASSETS/personaje/player_Attack_Right.png", 1, 6, 0.05f, true));
        attackAnims.get(PlayerDirection.RIGHT).getSprite().scale(scale);

        attackAnims.put(PlayerDirection.FRONT, new LoadAnimationPng("ASSETS/personaje/player_Attack_Front.png", 1, 6, 0.05f, true));
        attackAnims.get(PlayerDirection.FRONT).getSprite().scale(scale);
        animations.put(PlayerState.ATTACK, attackAnims);

        // --- HURT animations ---

        EnumMap<PlayerDirection, LoadAnimationPng> hurtAnims = new EnumMap<>(PlayerDirection.class);


        hurtAnims.put(PlayerDirection.LEFT, new LoadAnimationPng("ASSETS/personaje/player_Hurt_Left.png", 1, 5, 0.01f, false));
        hurtAnims.get(PlayerDirection.LEFT).getSprite().scale(scale); // Aplica la escala a la animación


        hurtAnims.put(PlayerDirection.RIGHT, new LoadAnimationPng("ASSETS/personaje/player_Hurt_Right.png", 1, 5, 0.01f, false));
        hurtAnims.get(PlayerDirection.RIGHT).getSprite().scale(scale); // Aplica la escala a la animación


        hurtAnims.put(PlayerDirection.FRONT, new LoadAnimationPng("ASSETS/personaje/player_Hurt_Front.png", 1, 5, 0.01f, false));
        hurtAnims.get(PlayerDirection.FRONT).getSprite().scale(scale); // Aplica la escala a la animación

        animations.put(PlayerState.HURT, hurtAnims);

        // Animación al morir
        EnumMap<PlayerDirection, LoadAnimationPng> dieAnimation = new EnumMap<>(PlayerDirection.class);


        dieAnimation.put(PlayerDirection.LEFT, new LoadAnimationPng("ASSETS/personaje/player_Die_Front.png", 1, 7, 0.2f, false));
        dieAnimation.get(PlayerDirection.LEFT).getSprite().scale(scale); // Aplica la escala a la animación


        dieAnimation.put(PlayerDirection.RIGHT, new LoadAnimationPng("ASSETS/personaje/player_Die_Front.png", 1, 7, 0.2f, false));
        dieAnimation.get(PlayerDirection.RIGHT).getSprite().scale(scale); // Aplica la escala a la animación


        dieAnimation.put(PlayerDirection.FRONT, new LoadAnimationPng("ASSETS/personaje/player_Die_Front.png", 1, 7, 0.2f, false));
        dieAnimation.get(PlayerDirection.FRONT).getSprite().scale(scale); // Aplica la escala a la animación

        animations.put(PlayerState.DEATH, dieAnimation);


        // Establecer la animación inicial
        setAnimation(playerState, playerDirection);
    }

    // Update General
    public void update(float dt, List<Rectangle> solidObjects) {
        move(dt, solidObjects);
        // Se actualizan tanto la animación como la posición
        currentAnimation.update(dt); //
        currentAnimation.getSprite().setPosition(position.x, position.y);

        // Solo actualiza el ataque, invulnerabilidad e hitboxes si el jugador NO está muerto
        if (playerState != PlayerState.DEATH) {

            updateAttack(dt); //

            updateInvulnerability(dt); //

            updateHitBox(); //
            updateHitBoxesAttack(); //

            curar();

        } else {
            // Para cuando la animación de morir acabe
            if (currentAnimation.isAnimationFinished()) {
                // Hace desaparecer al jugador
                isAlive = false;
                currentAnimation.getSprite().setAlpha(0);
            }

        }
    }

    private void curar() {
        // Para usar el botiquín
        if (Gdx.input.isKeyJustPressed(Input.Keys.E) && salud < 3 && !usingKit) {
            if (salud + 2 > 3) {
                salud = 3;
            } else {
                salud += 2;
            }
            usingKit = true;
            soundKit.play(0.5f);
        }
        // Para usar el jugo
        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && salud < 3 && !usingJuice) {
            if (salud + 1 > 3) {
                salud = 3;
            } else {
                salud += 1;
            }
            usingJuice = true;
            soundJuice.play(0.7f);
        }
    }

    // Para dibujar los frames de la animación
    public void draw(SpriteBatch batch) {

        // Cuando el personaje recibe daño parpadea
        if (isInvulnerable && (int) (invulnerabilityTimer * 10) % 2 == 0) {

        } else {
            currentAnimation.draw(batch);
        }
    }


    // Esto ayuda a establecer la animación dependiendo del estado y dirección
    private void setAnimation(PlayerState state, PlayerDirection direction) {
        LoadAnimationPng newAnimation = animations.get(state).get(direction);
        if (newAnimation != null && newAnimation != currentAnimation) {
            if (currentAnimation != null) {
                currentAnimation.resetAnimationTime(); // Reinicia la animación anterior
            }
            currentAnimation = newAnimation;
        }
    }

    // Para mover al personaje
    public void move(float dt, List<Rectangle> solidObjects) {
        // Si el jugador está muerto no permitir el movimiento y ataques

        if (playerState == PlayerState.DEATH) {
            velocity.set(0, 0);
            // Para no permitir que siga el método se retorna y evita la lógica del movimiento
            return;
        }

        // Se reinicia la velocidad o va a correr siempre
        velocity.set(0, 0); //

        // Mientras que ataque este no se mueve
        if (!isAttacking) {
            // Se define la dirección a donde se mueve en el vector

            if (Gdx.input.isKeyPressed(Input.Keys.D)) { //
                velocity.x += 1; //
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) { //
                velocity.x -= 1; //
            }

            if (Gdx.input.isKeyPressed(Input.Keys.W)) { //
                velocity.y += 1; //
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) { //
                velocity.y -= 1; //
            }

            // Ataca si se presiona la tecla de ataque y no estamos atacando
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) { //
                // Inicia la lógica para el ataque
                startAttack(); //
            }
        }

        // Lógica para mover el personaje y tener en cuenta otros aspectos
        if (!isAttacking && velocity.len() > 0) { //
            walkLogic(dt, solidObjects); //

        }
        // Si no hay movimiento y no está atacando cambia el estado a IDLE
        else if (!isAttacking) { //
            setPlayerState(PlayerState.IDLE); //
        }

        // Se actualiza la posición del currentAnimation
        currentAnimation.getSprite().setPosition(position.x, position.y); //
    }

    private void walkLogic(float dt, List<Rectangle> solidObjects) {
        // Se normaliza el vector de la velocidad
        velocity.nor();
        // Aquí se determina en donde mira según la velocidad vectorial correspondiente
        // Math.abs() solo hace que el valor siempre sea positivo, ya que tanto uno como el otro pueden ser - (negativo)

        if (Math.abs(velocity.x) > Math.abs(velocity.y)) {
            if (velocity.x > 0) {
                playerDirection = PlayerDirection.RIGHT;
            } else {
                playerDirection = PlayerDirection.LEFT;
            }
        }

        // En este caso hace que el personaje solo mire a izquierda, derecha o abajo dependiendo a donde se mueva
        else {
            if (velocity.y > 0) {
                if (velocity.x < 0) {
                    playerDirection = PlayerDirection.LEFT;
                } else {
                    playerDirection = PlayerDirection.RIGHT;
                }
            } else {
                playerDirection = PlayerDirection.FRONT;
            }
        }


        // Cambia el estado a WALK
        setPlayerState(PlayerState.WALK);


//      ----------------- Para detectar las colisiones de un objeto sólido de la lista y repeler la hitbox y mantener al personaje fuera -----------------------

        // --- Movimiento eje X ---
        float nextX = position.x + velocity.x * speed * dt;
        Vector2 trialPosition = new Vector2(nextX, position.y);
        moveHitbox.getRectangle().setPosition(
            trialPosition.x + getSprite().getWidth() / 2f - moveHitbox.getRectangle().width / 2f,
            position.y + getSprite().getHeight() / 2f - moveHitbox.getRectangle().height / 2f - 10
        );

        // Permite el movimiento completo o parcial dependiendo si está chocando o no
        boolean collisionX = collidesWithAny(solidObjects);
        if (!collisionX) {
            position.x = nextX;
        }

        // --- Movimiento eje Y ---
        float nextY = position.y + velocity.y * speed * dt;
        trialPosition.set(position.x, nextY);

        moveHitbox.getRectangle().setPosition(
            position.x + getSprite().getWidth() / 2f - moveHitbox.getRectangle().width / 2f,
            trialPosition.y + getSprite().getHeight() / 2f - moveHitbox.getRectangle().height / 2f - 10
        );

        // Permite el movimiento completo o parcial dependiendo si está chocando o no
        boolean collisionY = collidesWithAny(solidObjects);
        if (!collisionY) {
            position.y = nextY;
        }

    }

    // Este es para detectar colisiones de una lista de hitbox que funcionan como objetos sólidos o paredes
    private boolean collidesWithAny(List<Rectangle> solidObjects) {
        for (Rectangle solid : solidObjects) {
            if (moveHitbox.overlaps(solid)) return true;
        }
        return false;
    }


    // Para cambiar el estado del personaje junto con su animación correspondiente a la dirección y su estado
    private void setPlayerState(PlayerState newState) {
        // Solo se cambia cuando el estado es diferente al actual
        if (playerState != newState) {
            playerState = newState;
            // Actualiza la animación al cambiar de estado
            setAnimation(playerState, playerDirection);
        } else {
            // Si el estado es el mismo, aún se necesita asegurar que la animación es la correcta
            // para la dirección actual, especialmente al pasar de diagonal a cardinal
            setAnimation(playerState, playerDirection);
        }
    }

    //                                                  ATAQUE
    private void startAttack() {
        // Solo se activa cuando no se esta atacando
        if (!isAttacking) {
            isAttacking = true;
            soundAttack.play(0.3f);
            // Cambia el estado a ATTACK
            setPlayerState(PlayerState.ATTACK);

            // Se activa la hitbox correspondiente
            switch (playerDirection) {
                case RIGHT:
                    hitBoxAttackRight.active(true);
                    break;
                case LEFT:
                    hitBoxAttackLeft.active(true);
                    break;
                case FRONT:
                    hitBoxAttackFront.active(true);
                    break;
            }

            // Para asegurar que la animación siempre esté en su lugar
            currentAnimation.getSprite().setPosition(position.x, position.y);
        }
    }

    // Ayuda a verificar cuando se ataque y cuando su animación se termine para terminar dicha acción
    private void updateAttack(float dt) {
        // La acción termina cuando la animación lo haga
        if (isAttacking && currentAnimation.isAnimationFinished()) {
            isAttacking = false;

            // Se desactiva la hitbox correspondiente
            switch (playerDirection) {
                case RIGHT:
                    hitBoxAttackRight.active(false);
                    break;
                case LEFT:
                    hitBoxAttackLeft.active(false);
                    break;
                case FRONT:
                    hitBoxAttackFront.active(false);
                    break;
            }
        }
    }

    // Este metodo ayuda con la lógica al momento de recibir daño y saber si murió
    public void takeDamage() {
        if (!isInvulnerable) {
            // Le resta una candtidad a la vida del jugador
            salud -= 1;
            soundHurt.play(2);
            Gdx.app.log("Player", "¡Jugador recibió daño! Invulnerable por " + invulnerabilityDuration + " segundos.");
            Gdx.app.log("Player", "Salud: " + salud);

            if (salud <= 0) {
                // Cambia el estado a DEATH
                setPlayerState(PlayerState.DEATH);
                // Desactiva todas las hitboxes del jugador para que no interactúe más
                hitBox.active(false);
                moveHitbox.active(false);
                hitBoxAttackLeft.active(false);
                hitBoxAttackRight.active(false);
                hitBoxAttackFront.active(false);

                Gdx.app.log("Player", "¡El jugador ha muerto!");
            } else {
                // Si no ha muerto, activa la animación de HURT y la invulnerabilidad
                setPlayerState(PlayerState.HURT); //
                currentAnimation.resetAnimationTime(); //

                // Se vuelve inmune a los ataques por un momento
                isInvulnerable = true;
                invulnerabilityTimer = 0f;
                // Desactiva la hitbox principal para que no reciba más daño
                hitBox.active(false);
            }
        }
        currentAnimation.getSprite().setPosition(position.x, position.y);
    }

    // Este metodo ayuda con el estado de innumerabilidad
    private void updateInvulnerability(float dt) {
        if (isInvulnerable) {
            invulnerabilityTimer += dt;
            // Cuando la animación de HURT termina o el temporizador de invulnerabilidad se acaba se desactiva la innumerabilidad
            if (invulnerabilityTimer >= invulnerabilityDuration || (playerState == PlayerState.HURT && currentAnimation.isAnimationFinished())) {
                isInvulnerable = false;
                invulnerabilityTimer = 0f;
                // Reactiva la hitbox
                hitBox.active(true);
                // Volver al estado IDLE o WALK si no hay otras acciones pendientes
                if (!isAttacking) {
                    setPlayerState(PlayerState.IDLE);
                }
                Gdx.app.log("Player", "Invulnerabilidad del jugador terminada.");

            }
            currentAnimation.getSprite().setPosition(position.x, position.y);
        }
    }


//                                                  HITBOX

    //  Se actualiza la position de las hitboxes
    private void updateHitBox() {
        if (getSprite() != null) {
            // Para obtener el centro del sprite
            float centerX = getSprite().getX() + getSprite().getWidth() / 2;
            float centerY = getSprite().getY() + getSprite().getHeight() / 2;

            // Para obtener una posición de referencia según el personaje
            float rectX = centerX - getHitBox().getRectangle().width / 2;
            float rectY = centerY - getHitBox().getRectangle().height / 2;

            // Se mueven las hitboxes
            if (playerDirection == PlayerDirection.RIGHT) {
                getHitBox().getRectangle().setPosition(rectX + 2, rectY - 2);
                moveHitbox.getRectangle().setPosition(rectX + 2, rectY - 2);

            } else {
                getHitBox().getRectangle().setPosition(rectX - 1, rectY - 2);
                moveHitbox.getRectangle().setPosition(rectX - 1, rectY - 2);

            }
        }
    }

    // Para actualizar las hitboxes del ataque al momento de atacar
    private void updateHitBoxesAttack() {
        if (getSprite() != null) {
            float centerX = getSprite().getX() + getSprite().getWidth() / 2;
            float centerY = getSprite().getY() + getSprite().getHeight() / 2;

            float rectX = centerX - getHitBox().getRectangle().width / 2;
            float rectY = centerY - getHitBox().getRectangle().height / 2;

            hitBoxAttackLeft.getRectangle().setPosition(rectX - 17, rectY - 5);
            hitBoxAttackRight.getRectangle().setPosition(rectX + 2, rectY - 5);
            hitBoxAttackFront.getRectangle().setPosition(rectX - 11, rectY - 12);

        }
    }

    // Asi puedo dibujar la hitBox del personaje (debe haber un ShapeRenderer en la clase principal)
    public void drawHitBox(ShapeRenderer shapeRenderer, Color color) {

        // Puede usarse el color principal o uno definido directamente

        moveHitbox.drawRectangle(shapeRenderer, Color.LIME);
        if (hitBoxAttackLeft.isActive()) {
            hitBoxAttackLeft.drawRectangle(shapeRenderer, Color.RED);
        } else if (hitBoxAttackRight.isActive()) {
            hitBoxAttackRight.drawRectangle(shapeRenderer, Color.RED);
        } else if (hitBoxAttackFront.isActive()) {
            hitBoxAttackFront.drawRectangle(shapeRenderer, Color.RED);
        } else {
            hitBoxAttackLeft.drawRectangle(shapeRenderer, Color.BLUE);
            hitBoxAttackRight.drawRectangle(shapeRenderer, Color.BLUE);
            hitBoxAttackFront.drawRectangle(shapeRenderer, Color.BLUE);
        }

        // Dibuja la hitbox principal del jugador solo si está activa
        if (hitBox.isActive()) { //
            hitBox.drawRectangle(shapeRenderer, color);
        } else {
            // Dibuja la hitbox de otro color o con menos opacidad cuando es invulnerable
            hitBox.drawRectangle(shapeRenderer, Color.GRAY); //
        }
    }


//                                              Getters y Setters

    public Sprite getSprite() {
        return currentAnimation.getSprite();
    }

    public RectangleHitBox getHitBox() {
        return hitBox;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isInvulnerable() { //
        return isInvulnerable;
    }

    public boolean isUsingJuice() {
        return usingJuice;
    }

    public boolean isUsingKit() {
        return usingKit;
    }

    public float getSalud() {
        return salud;
    }

    public LoadAnimationPng getCurrentAnimation() {
        return currentAnimation;
    }

    // Para liberar la memoria
    @Override
    public void dispose() {
        currentAnimation.dispose();
        soundJuice.dispose();
        soundHurt.dispose();
        soundAttack.dispose();
        soundKit.dispose();
    }
}
