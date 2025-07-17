package com.badlogic.pruebas.entities.slimes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.pruebas.ayudas.hitboxes.RectangleHitBox;
import com.badlogic.pruebas.ayudas.loadTexture.LoadAnimationPng;
import com.badlogic.pruebas.entities.Player; // Importar la clase Player
import com.badlogic.pruebas.entities.SlimeModel;

import java.util.EnumMap;

public class SlimeN extends SlimeModel {

    // Salud del Slime
    private float salud; // Cambiado de 'vida' a 'salud' para consistencia con SlimeA

    // Hitbox de ataque
    public RectangleHitBox attackHitBox;
    // Hitbox para la inteligencia
    // Área para detectar al jugador y empezar a perseguirlo
    public RectangleHitBox detectionArea;
    // Área para activar la secuencia de ataque
    public RectangleHitBox attackTriggerArea;

    // Variables para el Cooldown del ataque (adaptado de SlimeA)
    private final float attackCooldown; // Duración total del cooldown en segundos.
    private float currentCooldown;      // Temporizador actual.

    // Variables para manejar la invulnerabilidad a la hora de recibir daño (adaptado de SlimeA)
    private boolean isInvulnerableSlime = false;
    private float invulnerabilityDurationSlime = 0.5f; // Duración de invulnerabilidad del slime en segundos
    private float invulnerabilityTimerSlime = 0f;      // Temporizador para la invulnerabilidad del slime

    private boolean alive;

    public SlimeN(float posX, float posY) {
        this.soundAttack = Gdx.audio.newSound(Gdx.files.internal("sounds/Retro Blop StereoUP 09.wav"));

        this.salud = 7;
        this.alive = true;
        // Velocidad y coordenadas
        this.speed = 30f;
        this.position = new Vector2(posX, posY);
        this.velocity = new Vector2();

        this.hitBox = new RectangleHitBox(position.x,position.y,10,10);

        this.attackHitBox = new RectangleHitBox(position.x,position.y,42,22);
        // El hitbox de daño empieza desactivado.
        attackHitBox.active(false);

        this.detectionArea = new RectangleHitBox(position.x, position.y,180,180);

        this.attackTriggerArea = new RectangleHitBox(position.x, position.y,42,22);

        this.slimeState = SlimeState.IDLE;
        this.slimeDirection = SlimeDirection.FRONT;

        // Dirección inicial y estado inicial
        this.attackCooldown = 1.2f; // El slime esperará 1.2 segundos para atacar de nuevo
        this.currentCooldown = 0f;
    }

    // Para cargar todas las texturas
    @Override
    public void loadAllAnimation(float scale) {
        super.loadAllAnimation(scale);
        animations = new EnumMap<>(SlimeState.class);

        // Animaciones idle
        EnumMap<SlimeDirection, LoadAnimationPng> idleAnimations = new EnumMap<>(SlimeDirection.class);
        idleAnimations.put(SlimeDirection.LEFT, new LoadAnimationPng("ASSETS/slimes/slimeN/SlimeN_Idle_Left.png", 1, 6, 0.1f, true));
        idleAnimations.get(SlimeDirection.LEFT).getSprite().setScale(scale);

        idleAnimations.put(SlimeDirection.RIGHT, new LoadAnimationPng("ASSETS/slimes/slimeN/SlimeN_Idle_Right.png", 1, 6, 0.1f, true));
        idleAnimations.get(SlimeDirection.RIGHT).getSprite().setScale(scale);

        idleAnimations.put(SlimeDirection.FRONT, new LoadAnimationPng("ASSETS/slimes/slimeN/SlimeN_Idle_Front.png", 1, 6, 0.1f, true));
        idleAnimations.get(SlimeDirection.FRONT).getSprite().setScale(scale);
        // Se agrega las animaciones a la lista principal
        animations.put(SlimeState.IDLE, idleAnimations);

        // Animaciones de caminar
        EnumMap<SlimeDirection, LoadAnimationPng> walkAnimations = new EnumMap<>(SlimeDirection.class);

        walkAnimations.put(SlimeDirection.LEFT, new LoadAnimationPng("ASSETS/slimes/slimeN/SlimeN_Walk_Left.png", 1, 8, 0.1f, true));
        walkAnimations.get(SlimeDirection.LEFT).getSprite().setScale(scale);

        walkAnimations.put(SlimeDirection.RIGHT, new LoadAnimationPng("ASSETS/slimes/slimeN/SlimeN_Walk_Right.png", 1, 8, 0.1f, true));
        walkAnimations.get(SlimeDirection.RIGHT).getSprite().setScale(scale);

        walkAnimations.put(SlimeDirection.FRONT, new LoadAnimationPng("ASSETS/slimes/slimeN/SlimeN_Walk_Front.png", 1, 8, 0.1f, true));
        walkAnimations.get(SlimeDirection.FRONT).getSprite().setScale(scale);

        animations.put(SlimeState.WALK, walkAnimations);

        // Animaciones de herida
        EnumMap<SlimeDirection, LoadAnimationPng> hurtAnimations = new EnumMap<>(SlimeDirection.class);

        hurtAnimations.put(SlimeDirection.LEFT, new LoadAnimationPng("ASSETS/slimes/slimeN/SlimeN_Hurt_Left.png", 1, 5, 0.1f, false));
        hurtAnimations.get(SlimeDirection.LEFT).getSprite().setScale(scale);

        hurtAnimations.put(SlimeDirection.RIGHT, new LoadAnimationPng("ASSETS/slimes/slimeN/SlimeN_Hurt_Right.png", 1, 5, 0.1f, false));
        hurtAnimations.get(SlimeDirection.RIGHT).getSprite().setScale(scale);

        hurtAnimations.put(SlimeDirection.FRONT, new LoadAnimationPng("ASSETS/slimes/slimeN/SlimeN_Hurt_Front.png", 1, 5, 0.1f, false));
        hurtAnimations.get(SlimeDirection.FRONT).getSprite().setScale(scale);

        animations.put(SlimeState.HURT, hurtAnimations);

        //Animaciones de ataque
        EnumMap<SlimeDirection, LoadAnimationPng> attackAnimation = new EnumMap<>(SlimeDirection.class);

        attackAnimation.put(SlimeDirection.LEFT, new LoadAnimationPng("ASSETS/slimes/slimeN/SlimeN_Attack_Left.png", 1, 10, 0.08f, false));
        attackAnimation.get(SlimeDirection.LEFT).getSprite().setScale(scale);

        attackAnimation.put(SlimeDirection.RIGHT, new LoadAnimationPng("ASSETS/slimes/slimeN/SlimeN_Attack_Right.png", 1, 10, 0.08f, false));
        attackAnimation.get(SlimeDirection.RIGHT).getSprite().setScale(scale);

        attackAnimation.put(SlimeDirection.FRONT, new LoadAnimationPng("ASSETS/slimes/slimeN/SlimeN_Attack_Front.png", 1, 10, 0.08f, false));
        attackAnimation.get(SlimeDirection.FRONT).getSprite().setScale(scale);

        animations.put(SlimeState.ATTACK, attackAnimation);

        // Animación al morir
        EnumMap<SlimeDirection, LoadAnimationPng> dieAnimations = new EnumMap<>(SlimeDirection.class);

        dieAnimations.put(SlimeDirection.FRONT, new LoadAnimationPng("ASSETS/slimes/slimeN/SlimeN_Death.png", 1, 10, 0.1f, false));
        dieAnimations.get(SlimeDirection.FRONT).getSprite().setScale(scale);
        dieAnimations.put(SlimeDirection.LEFT, new LoadAnimationPng("ASSETS/slimes/slimeN/SlimeN_Death.png", 1, 10, 0.1f, false));
        dieAnimations.get(SlimeDirection.LEFT).getSprite().setScale(scale);
        dieAnimations.put(SlimeDirection.RIGHT, new LoadAnimationPng("ASSETS/slimes/slimeN/SlimeN_Death.png", 1, 10, 0.1f, false));
        dieAnimations.get(SlimeDirection.RIGHT).getSprite().setScale(scale);

        animations.put(SlimeState.DEATH, dieAnimations);

        // Se establece la animación inicial
        setAnimation(slimeState, slimeDirection);
    }

    // Metodo principal para dibujar en la pantalla (debe estar en un SpriteBath)
    @Override
    public void draw(SpriteBatch batch) {
        // Para crear un efecto de parpadeo
        if (isInvulnerableSlime && (int)(invulnerabilityTimerSlime * 10) % 2 == 0) {
        } else {
            super.draw(batch);
        }
    }

    // Método principal para actualización
    public void update(float dt, Player player) {
        super.update(dt);

        updateInvulnerability(dt);

        // Para saber si el Slime sigue vivo
        if (salud > 0) {
            // Para saber si el slime es atacado por el jugador
            if (!isInvulnerableSlime) {
                if (player.hitBoxAttackLeft.isActive() && player.hitBoxAttackLeft.overlaps(this.hitBox.getRectangle())) {
                    takeDamage(player.hitBoxAttackLeft); // Pasa la hitbox que causó el daño
                    position.x-=12;
                } else if (player.hitBoxAttackRight.isActive() && player.hitBoxAttackRight.overlaps(this.hitBox.getRectangle())) {
                    takeDamage(player.hitBoxAttackRight);
                    position.x+=12;
                } else if (player.hitBoxAttackFront.isActive() && player.hitBoxAttackFront.overlaps(this.hitBox.getRectangle())) {
                    takeDamage(player.hitBoxAttackFront);
                    position.y-=16;
                }
            }
            // Para actualizar las acciones
            logicMoveAttack(dt, player);
            updateDirectionFromVelocity();
        }

        // Cuando muere
        else {
            hitBox.active(false);
            attackHitBox.active(false);
            attackTriggerArea.active(false);
            detectionArea.active(false);
            setSlimeState(SlimeState.DEATH);
        }

        setAnimation(slimeState, slimeDirection);
        currentAnimation.getSprite().setPosition(position.x, position.y);

        updateHitbox();
    }

    private void logicMoveAttack(float dt, Player player) {


        // Para cuando el slime se recupera del ataque realizado
        if (currentCooldown > 0) {
            // Reduce el temporizador.
            currentCooldown -= dt;
            // Hacemos que parezca inactivo.
            slimeState = SlimeState.IDLE;
            // No se puede mover.
            velocity.set(0, 0);

            // Para saber si el slime esta atacando
        } else if (slimeState == SlimeState.ATTACK) {
            // No se puede mover mientras ataca.
            velocity.set(0, 0);

            // Ataca y se activa la hitbox de ataque en un momento determinado de la animación
            if (currentAnimation != null && !attackHitBox.isActive() &&
                currentAnimation.getAnimation().getKeyFrameIndex(currentAnimation.getStateTime()) >= 5) {
                attackHitBox.active(true);
            }

            // Si la animación de ataque ha terminado
            if (currentAnimation.isAnimationFinished()) {
                // Vuelve al estado IDLE.
                slimeState = SlimeState.IDLE;
                // Desactiva la hitbox de daño.
                attackHitBox.active(false);
                // Se inicia una recuperación tras el ataque
                currentCooldown = attackCooldown;
            }

            // Para cuando no está en ninguna de las acciones anteriores
        } else {
            // Se obtiene la hitbox principal del jugador
            RectangleHitBox playerHitBox = player.getHitBox();
            // Ataca cuando el personaje esta en el rango de ataque
            if (attackTriggerArea.overlaps(playerHitBox.getRectangle()) && player.isAlive) {
                startAttack();
            } else if (detectionArea.overlaps(playerHitBox.getRectangle()) && player.isAlive) {
                slimeState = SlimeState.WALK;
                Vector2 directionToPlayer = new Vector2(player.position.x - this.position.x, player.position.y - this.position.y).nor();
                velocity.set(directionToPlayer.scl(speed));
            } else {
                slimeState = SlimeState.IDLE;
                velocity.set(0, 0);
            }
        }
        position.add(velocity.x * dt, velocity.y * dt);
    }

    // Logica para recibir daño
    public void takeDamage(RectangleHitBox playerAttackHitBox) {
        if (!isInvulnerableSlime) {
            salud -= 1; // Reducimos la salud del slime. Puedes ajustar este valor.
            soundHurt.play(0.4f);

            Gdx.app.log("SlimeN", "Slime recibió daño! Salud restante: " + salud);

            // Cambiar al estado de HURT
            slimeState = SlimeState.HURT;
            // Reiniciar la animación de HURT para que siempre se vea completa
            if (animations.get(SlimeState.HURT).get(slimeDirection) != null) {
                animations.get(SlimeState.HURT).get(slimeDirection).resetAnimationTime();
            }

            // Activar la invulnerabilidad
            isInvulnerableSlime = true;
            invulnerabilityTimerSlime = 0f;
            hitBox.active(false);

            if (salud <= 0) {
                slimeState = SlimeState.DEATH;
                alive = false;
                soundDeath.play(0.2f);
                Gdx.app.log("SlimeN", "Slime ha muerto!");
            }
        }
    }

    // Para actualizar el estado y no recibir daño
    private void updateInvulnerability(float dt) {
        if (isInvulnerableSlime) {
            invulnerabilityTimerSlime += dt;
            // Si la animación de HURT terminó Y el temporizador de invulnerabilidad se acabó,
            boolean hurtAnimationFinished = (slimeState == SlimeState.HURT && currentAnimation.isAnimationFinished());

            if (invulnerabilityTimerSlime >= invulnerabilityDurationSlime || hurtAnimationFinished) {
                isInvulnerableSlime = false;
                invulnerabilityTimerSlime = 0f;
                hitBox.active(true); // Reactiva la hitbox principal
                Gdx.app.log("SlimeN", "Invulnerabilidad del Slime terminada.");

                // Si el slime estaba en estado HURT y la invulnerabilidad ha terminado
                if (slimeState == SlimeState.HURT && salud > 0) {
                    slimeState = SlimeState.IDLE; // Vuelve a IDLE o la lógica correspondiente
                }
            }
        }
    }

    // Cambia la dirección de movimiento y ataque
    private void updateDirectionFromVelocity() {
        if (velocity.isZero()) return; // Si no hay movimiento, no cambia la dirección

        if (Math.abs(velocity.x) > Math.abs(velocity.y)) {
            if (velocity.x > 0) {
                slimeDirection = SlimeDirection.RIGHT;
            } else {
                slimeDirection = SlimeDirection.LEFT;
            }
        } else {
            slimeDirection = SlimeDirection.FRONT;
        }
    }

    @Override
    public void startAttack() {
        // Solo puede atacar si no esta atacando o en cooldown.
        if (slimeState != SlimeState.ATTACK && currentCooldown <= 0) {
            slimeState = SlimeState.ATTACK;
            soundAttack.play();

            if (animations.get(SlimeState.ATTACK).get(slimeDirection) != null) {
                animations.get(SlimeState.ATTACK).get(slimeDirection).resetAnimationTime();
            }
        }
    }


    // Modifica este método para incluir las nuevas hitboxes (adaptado de SlimeA)
    @Override
    public void updateHitbox() {
        if (getSprite() != null) {
            float centerX = getSprite().getX() + getSprite().getWidth() / 2;
            float centerY = getSprite().getY() + getSprite().getHeight() / 2;

            float rectX = centerX - getHitBox().getRectangle().width / 2;
            float rectY = centerY - getHitBox().getRectangle().height / 2;

            getHitBox().getRectangle().setPosition(rectX, rectY-2);

            detectionArea.getRectangle().setCenter(centerX, centerY);

            attackHitBox.getRectangle().setPosition(rectX-15,rectY-2);
            attackTriggerArea.getRectangle().setPosition(rectX-15,rectY-2);

        }

    }

    @Override
    public void drawHitbox(ShapeRenderer shapeRenderer, Color color) {
        hitBox.drawRectangle(shapeRenderer, color);

        detectionArea.drawRectangle(shapeRenderer, Color.YELLOW);
        attackTriggerArea.drawRectangle(shapeRenderer, Color.ORANGE);

        if (attackHitBox.isActive()) {
            attackHitBox.drawRectangle(shapeRenderer, Color.RED);
        }
    }

    public boolean isAlive() {
        return alive;
    }
}
