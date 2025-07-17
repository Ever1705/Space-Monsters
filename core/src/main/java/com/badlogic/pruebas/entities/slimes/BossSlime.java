package com.badlogic.pruebas.entities.slimes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.pruebas.ayudas.hitboxes.RectangleHitBox;
import com.badlogic.pruebas.ayudas.loadTexture.LoadAnimationPng;
import com.badlogic.pruebas.entities.Player;
import com.badlogic.pruebas.entities.Projectile;
import com.badlogic.pruebas.entities.SlimeModel;

import java.util.EnumMap;
import java.util.List;

public class BossSlime extends SlimeModel {

    // Salud del Slime
    private float salud;

    private Sound soundFireBall;

    // Hitbox de ataque
    public RectangleHitBox attackHitBox;
    // Hitbox para la inteligencia
    public RectangleHitBox detectionArea;
    public RectangleHitBox attackTriggerArea;

    // Hitbox del area para lanzar los proyectiles
    public RectangleHitBox projectileLaunchArea;

    // Variables para el Cooldown del ataque
    private final float attackCooldown;
    private float currentCooldown;

    // Variables para manejar la invulnerabilidad a la hora de recibir daño
    private boolean isInvulnerableSlime = false;
    private float invulnerabilityDurationSlime = 0.5f;
    private float invulnerabilityTimerSlime = 0f;

    private boolean alive;

    // Para los proyectiles
    private final float projectileCooldown; // Tiempo entre lanzamientos de proyectiles
    private float currentProjectileCooldown; // Temporizador actual del proyectil
    private final float projectileSpeed = 150f; // Velocidad de los proyectiles
    private final float projectileDamage = 2f; // Daño del proyectil
    private final float projectileScale = 1.0f; // Escala del sprite del proyectil


    // Lista donde se añadirán los proyectiles creados
    private List<Projectile> activeProjectiles;

    public BossSlime(float posX, float posY, List<Projectile> projectilesList) {
        this.soundAttack = Gdx.audio.newSound(Gdx.files.internal("sounds/RetroImpact LoFi09.wav"));
        this.soundFireBall = Gdx.audio.newSound(Gdx.files.internal("sounds/Retro Explosion Short 01.wav"));

        this.salud = 10;
        this.alive = true;
        this.speed = 0f;
        this.position = new Vector2(posX, posY);
        this.velocity = new Vector2();

        this.hitBox = new RectangleHitBox(position.x, position.y, 30, 30);

        this.attackHitBox = new RectangleHitBox(position.x, position.y, 60, 60);
        attackHitBox.active(false);

        this.detectionArea = new RectangleHitBox(position.x, position.y, 250, 250);
        this.attackTriggerArea = new RectangleHitBox(position.x, position.y, 60, 60);
        // Inicializar la nueva área de lanzamiento de proyectiles
        this.projectileLaunchArea = new RectangleHitBox(position.x, position.y, 250, 250); // Tamaño del área, ajusta según necesites


        this.slimeState = SlimeState.IDLE;
        this.slimeDirection = SlimeDirection.FRONT;

        this.attackCooldown = 1.8f;
        this.currentCooldown = 0f;

        // Inicialización de variables de proyectil
        this.projectileCooldown = 2.5f;
        this.currentProjectileCooldown = 0f;

        this.activeProjectiles = projectilesList;
    }

    @Override
    public void loadAllAnimation(float scale) {
        super.loadAllAnimation(scale);
        animations = new EnumMap<>(SlimeState.class);

        // Animaciones idle
        EnumMap<SlimeDirection, LoadAnimationPng> idleAnimations = new EnumMap<>(SlimeDirection.class);
        idleAnimations.put(SlimeDirection.FRONT, new LoadAnimationPng("ASSETS/slimes/slimeJefe/SlimeJefe_Idle_Front.png", 1, 6, 0.1f, true));
        idleAnimations.get(SlimeDirection.FRONT).getSprite().setScale(scale);
        animations.put(SlimeState.IDLE, idleAnimations);

        // Animaciones de caminar (no usadas directamente, pero necesarias si SlimeModel las usa)
        EnumMap<SlimeDirection, LoadAnimationPng> walkAnimations = new EnumMap<>(SlimeDirection.class);
        walkAnimations.put(SlimeDirection.FRONT, new LoadAnimationPng("ASSETS/slimes/slimeN/SlimeN_Walk_Front.png", 1, 8, 0.1f, true));
        walkAnimations.get(SlimeDirection.FRONT).getSprite().setScale(scale);
        animations.put(SlimeState.WALK, walkAnimations);

        // Animaciones de herida
        EnumMap<SlimeDirection, LoadAnimationPng> hurtAnimations = new EnumMap<>(SlimeDirection.class);
        hurtAnimations.put(SlimeDirection.FRONT, new LoadAnimationPng("ASSETS/slimes/slimeN/SlimeN_Hurt_Front.png", 1, 5, 0.1f, false));
        hurtAnimations.get(SlimeDirection.FRONT).getSprite().setScale(scale);
        animations.put(SlimeState.HURT, hurtAnimations);

        // Animaciones de ataque
        EnumMap<SlimeDirection, LoadAnimationPng> attackAnimation = new EnumMap<>(SlimeDirection.class);
        attackAnimation.put(SlimeDirection.FRONT, new LoadAnimationPng("ASSETS/slimes/slimeJefe/SlimeJefe_Attack_Front.png", 1, 9, 0.08f, false));
        attackAnimation.get(SlimeDirection.FRONT).getSprite().setScale(scale);
        animations.put(SlimeState.ATTACK, attackAnimation);

        // Animación al morir
        EnumMap<SlimeDirection, LoadAnimationPng> dieAnimations = new EnumMap<>(SlimeDirection.class);
        dieAnimations.put(SlimeDirection.FRONT, new LoadAnimationPng("ASSETS/slimes/slimeJefe/SlimeJefe_Death.png", 1, 10, 0.1f, false));
        dieAnimations.get(SlimeDirection.FRONT).getSprite().setScale(scale);
        animations.put(SlimeState.DEATH, dieAnimations);

        setAnimation(slimeState, slimeDirection);
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (isInvulnerableSlime && (int)(invulnerabilityTimerSlime * 10) % 2 == 0) {
            // Efecto de parpadeo, no se dibuja en ciertos frames
        } else {
            super.draw(batch);
        }
    }


    public void update(float dt, Player player) {
        super.update(dt);

        updateInvulnerability(dt);

        if (salud > 0) {
            // Manejo de daño del jugador al boss
            if (!isInvulnerableSlime) {
                if (player.hitBoxAttackLeft.isActive() && player.hitBoxAttackLeft.overlaps(this.hitBox.getRectangle())) {
                    takeDamage(player.hitBoxAttackLeft);
                } else if (player.hitBoxAttackRight.isActive() && player.hitBoxAttackRight.overlaps(this.hitBox.getRectangle())) {
                    takeDamage(player.hitBoxAttackRight);
                } else if (player.hitBoxAttackFront.isActive() && player.hitBoxAttackFront.overlaps(this.hitBox.getRectangle())) {
                    takeDamage(player.hitBoxAttackFront);
                }
            }

            logicMoveAttack(dt, player);
            updateDirectionFromVelocity();

            // --- Lógica para lanzar proyectiles ---
            updateProjectileCooldown(dt, player);
        } else {
            hitBox.active(false);
            attackHitBox.active(false);
            attackTriggerArea.active(false);
            detectionArea.active(false);
            // Desactiva el área de lanzamiento de proyectiles cuando el boss muere (para evitar problemas :| )
            projectileLaunchArea.active(false);
            setSlimeState(SlimeState.DEATH);
        }

        setAnimation(slimeState, slimeDirection);
        currentAnimation.getSprite().setPosition(position.x, position.y);

        updateHitbox();
    }

    private void logicMoveAttack(float dt, Player player) {
        if (currentCooldown > 0) {
            currentCooldown -= dt;
            slimeState = SlimeState.IDLE;
            velocity.set(0, 0);
        } else if (slimeState == SlimeState.ATTACK) {
            velocity.set(0, 0);

            if (currentAnimation != null && !attackHitBox.isActive() &&
                currentAnimation.getAnimation().getKeyFrameIndex(currentAnimation.getStateTime()) >= 5) {
                attackHitBox.active(true);
            }

            if (currentAnimation.isAnimationFinished()) {
                slimeState = SlimeState.IDLE;
                attackHitBox.active(false);
                currentCooldown = attackCooldown;
            }
        } else {
            RectangleHitBox playerHitBox = player.getHitBox();
            if (attackTriggerArea.overlaps(playerHitBox.getRectangle()) && player.isAlive) {
                startAttack();
            } else if (detectionArea.overlaps(playerHitBox.getRectangle()) && player.isAlive) {
                slimeState = SlimeState.IDLE;
            } else {
                slimeState = SlimeState.IDLE;
            }
            velocity.set(0, 0);
        }
    }

    // Logica para lanzar los proyectiles
    private void updateProjectileCooldown(float dt, Player player) {
        if (salud <= 0) return;

        currentProjectileCooldown -= dt;

        if (currentProjectileCooldown <= 0 &&
            (slimeState == SlimeState.IDLE || slimeState == SlimeState.WALK) &&
            projectileLaunchArea.overlaps(player.getHitBox().getRectangle()) &&
            player.isAlive) {

            launchProjectile(player.position);
            soundFireBall.play();
            currentProjectileCooldown = projectileCooldown;
        }
    }

    // Para lanzaar lo proyectiles
    private void launchProjectile(Vector2 targetPosition) {
        // Crear un nuevo proyectil en la posición del boss, apuntando al jugador
        Projectile newProjectile = new Projectile(
            this.position.x , // Centro del boss en X
            this.position.y, // Centro del boss en Y
            targetPosition,
            projectileSpeed,
            projectileScale,
            projectileDamage
        );
        activeProjectiles.add(newProjectile);
    }


    // Para que el jefe reciba daño
    public void takeDamage(RectangleHitBox playerAttackHitBox) {
        if (!isInvulnerableSlime) {
            salud -= 2;
            soundHurt.play(0.4f);

            slimeState = SlimeState.HURT;
            if (animations.get(SlimeState.HURT).get(slimeDirection) != null) {
                animations.get(SlimeState.HURT).get(slimeDirection).resetAnimationTime();
            }

            isInvulnerableSlime = true;
            invulnerabilityTimerSlime = 0f;
            hitBox.active(false);

            if (salud <= 0) {
                slimeState = SlimeState.DEATH;
                alive = false;
                soundDeath.play(0.4f);
                Gdx.app.log("BossSlime", "Boss Slime ha muerto!");
            }
        }
    }

    // Para que no reciba daño
    private void updateInvulnerability(float dt) {
        if (isInvulnerableSlime) {
            invulnerabilityTimerSlime += dt;
            boolean hurtAnimationFinished = (slimeState == SlimeState.HURT && currentAnimation.isAnimationFinished());

            if (invulnerabilityTimerSlime >= invulnerabilityDurationSlime || hurtAnimationFinished) {
                isInvulnerableSlime = false;
                invulnerabilityTimerSlime = 0f;
                hitBox.active(true);

                if (slimeState == SlimeState.HURT && salud > 0) {
                    slimeState = SlimeState.IDLE;
                }
            }
        }
    }

    private void updateDirectionFromVelocity() {
        slimeDirection = SlimeDirection.FRONT;
    }

    @Override
    public void startAttack() {
        if (slimeState != SlimeState.ATTACK && currentCooldown <= 0) {
            slimeState = SlimeState.ATTACK;
            soundAttack.play();

            if (animations.get(SlimeState.ATTACK).get(slimeDirection) != null) {
                animations.get(SlimeState.ATTACK).get(slimeDirection).resetAnimationTime();
            }
        }
    }

    @Override
    public void updateHitbox() {
        if (getSprite() != null) {
            float centerX = getSprite().getX() + getSprite().getWidth() / 2;
            float centerY = getSprite().getY() + getSprite().getHeight() / 2;

            getHitBox().getRectangle().setCenter(centerX, centerY - 6);

            detectionArea.getRectangle().setCenter(centerX, centerY);
            attackTriggerArea.getRectangle().setCenter(centerX, centerY);
            // Actualizar la posición del área de lanzamiento de proyectiles
            projectileLaunchArea.getRectangle().setCenter(centerX, centerY);

            attackHitBox.getRectangle().setCenter(centerX, centerY);
        }
    }

    @Override
    public void drawHitbox(ShapeRenderer shapeRenderer, Color color) {
        hitBox.drawRectangle(shapeRenderer, color);
        detectionArea.drawRectangle(shapeRenderer, Color.YELLOW);
        attackTriggerArea.drawRectangle(shapeRenderer, Color.ORANGE);
        projectileLaunchArea.drawRectangle(shapeRenderer, Color.BLUE);

        if (attackHitBox.isActive()) {
            attackHitBox.drawRectangle(shapeRenderer, Color.RED);
        }
    }

    public boolean isAlive() {
        return alive;
    }
}
