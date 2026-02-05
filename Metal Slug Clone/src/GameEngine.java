import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import java.util.ArrayList;


public class GameEngine extends Pane{

    private final double SCENE_WIDTH = 1200;
    private final double SCENE_HEIGHT = 750;

    private double cameraX;
    private Player player;
    private LevelManager levelManager;
    private InputManager inputManager;
    private ArrayList<Enemy> enemies;
    private ArrayList<POW> pow;
    private final ArrayList<Character> shooterBuffer = new ArrayList<>();
    private AnimationTimer gameloop;
    private long lastNow;
    public static double GAME_SPEED = 1.0; // Global speed multiplier
    private int deathCount;
    private static final int MAX_DEATHS = 5;
    private boolean playerWasAlive;
    private boolean isPaused;
    private javafx.stage.Stage stage;
    private boolean escapePressed;
    private Text livesDisplay;
    private Rectangle healthBarBg;
    private Rectangle healthBarFg;
    private Text healthText;
    private boolean gameOverTriggered;
    private int xpCount;
    private Text xpDisplay;

    public GameEngine(){
        cameraX = 0;
        isPaused = false;
        setStyle("-fx-padding: 0; -fx-background-color: #000;");
        setPrefSize(SCENE_WIDTH, SCENE_HEIGHT);
        setMinSize(0, 0);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        levelManager = new LevelManager();
        Level currentLevel = levelManager.getCurrentLevel();

        player = new Player(currentLevel.getPlayerStartX(),currentLevel.getPlayerStartY() , currentLevel.getLevelWidth());

        deathCount = 0;
        playerWasAlive = player.isAlive();
        gameOverTriggered = false;
        xpCount = 0;

        getChildren().addAll(currentLevel.getBackground() , player.getSpriteView());
        
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.widthProperty().addListener((o, old, w) -> updateScale());
                newScene.heightProperty().addListener((o, old, h) -> updateScale());
                updateScale();
            }
        });


        enemies = new ArrayList<>();
        pow = new ArrayList<>();
        addLevelPlatforms(currentLevel);
        addLevelEnemies(currentLevel);
        addLevelPOW(currentLevel);

        createLivesDisplay();
        createHealthBar();
        createXPDisplay();

        lastNow = 0;
        setupGameLoop();
    }

    public void setInputManager(InputManager manager) {
        this.inputManager = manager;
    }

    public void setStage(javafx.stage.Stage stage) {
        this.stage = stage;
    }

    private void addLevelPlatforms(Level currentLevel){
        ArrayList<GameObjects> entities = currentLevel.getGameObjects();
        for( GameObjects entity : entities){
            if(entity instanceof Platform){
                Platform platform = (Platform) entity;
                getChildren().add(platform.getCollisionBox());
            }
        }
    }

    private void addLevelEnemies(Level currentLevel){
        ArrayList<GameObjects> entities = currentLevel.getGameObjects();
        for( GameObjects entity : entities){
            if(entity instanceof Enemy){
                Enemy enemy = (Enemy) entity;
                enemies.add(enemy);
                getChildren().add(enemy.getSpriteView());

                // Add hitbox rectangles p-)
                if(enemy instanceof BasicEnemy){
                    BasicEnemy basic = (BasicEnemy) enemy;
                } else if(enemy instanceof ShieldedEnemy){
                    ShieldedEnemy shielded = (ShieldedEnemy) enemy;
                }
            }
        }
    }

    private void addLevelPOW(Level currentLevel){
        ArrayList<GameObjects> entities = currentLevel.getGameObjects();
        for( GameObjects entity : entities){
            if(entity instanceof POW){
                POW prisoner = (POW) entity;
                pow.add(prisoner);
                getChildren().add(prisoner.getSpriteView());
            }
        }
    }

    public void changeLevel(int levelNumber){
        getChildren().remove(levelManager.getCurrentLevel().getBackground());

        Level oldLevel = levelManager.getCurrentLevel();
        ArrayList<GameObjects> entities = oldLevel.getGameObjects();
        for (GameObjects entity : entities) {
            if (entity instanceof Platform) {
                Platform platform = (Platform) entity;
                getChildren().remove(platform.getCollisionBox());
            }
            if (entity instanceof Enemy) {
                Enemy enemy = (Enemy) entity;
                getChildren().remove(enemy.getSpriteView());

                // Remove hitboxes ;(
                if (enemy instanceof BasicEnemy) {
                    BasicEnemy basic = (BasicEnemy) enemy;
                } else if (enemy instanceof ShieldedEnemy) {
                    ShieldedEnemy shielded = (ShieldedEnemy) enemy;
                }

            }
            if (entity instanceof POW) {
                POW prisoner = (POW) entity;
                getChildren().remove(prisoner.getSpriteView());
            }
        }

        enemies.clear();

        levelManager.loadLevel(levelNumber);
        Level currentLevel = levelManager.getCurrentLevel();
        getChildren().add(currentLevel.getBackground());
        addLevelPlatforms(currentLevel);
        addLevelEnemies(currentLevel);
        addLevelPOW(currentLevel);

        player.setPosition(currentLevel.getPlayerStartX(), currentLevel.getPlayerStartY() ,  currentLevel.getLevelWidth() , 0);
        cameraX = 0;
    }

    private void setupGameLoop() {
        gameloop = new AnimationTimer() {
            public void handle(long now) {
                if (!isPaused) {
                    if (lastNow == 0) {
                        lastNow = now;
                        return;
                    }
                    long elapsed = now - lastNow;
                    lastNow = now;

                    double dtSeconds = elapsed / 1_000_000_000.0;
                    double dtScale = dtSeconds * 60.0; // normalize to 60fps baseline
                    if (dtScale > 3.0) {
                        dtScale = 3.0;
                    }
                    dtScale *= GAME_SPEED;

                    update(dtScale);
                }
            }
        };
        gameloop.start();
    }

    public void pauseGame() {
        isPaused = true;
    }

    public void resumeGame() {
        isPaused = false;
    }

    public void stopGame() {
        if (gameloop != null) {
            gameloop.stop();
        }
        Level currentLevel = levelManager.getCurrentLevel();
        if (currentLevel != null) {
            currentLevel.stopMusic();
        }
    }

    private void showPauseMenu() {
        if (stage != null) {
            pauseGame();
            javafx.scene.Scene currentScene = stage.getScene();
            PauseMenu pauseMenu = new PauseMenu(stage, currentScene, this);
            javafx.scene.Scene pauseScene = new javafx.scene.Scene(pauseMenu);
            stage.setScene(pauseScene);
        }
    }

    public void updateVolume() {
        Level currentLevel = levelManager.getCurrentLevel();
        if (currentLevel != null) {
            currentLevel.updateVolume();
        }
        if (player != null) {
            player.updateSoundVolume();
        }
        for (Enemy enemy : enemies) {
            enemy.updateSoundVolume();
        }
    }

    private void update(double dtScale) {
        if (inputManager.isEscape() && !escapePressed) {
            escapePressed = true;
            showPauseMenu();
            return;
        }
        if (!inputManager.isEscape()) {
            escapePressed = false;
        }

        player.setLeftPressed(inputManager.isLeft());
        player.setRightPressed(inputManager.isRight());
        player.setJumpPressed(inputManager.isJump());
        player.setCrouchPressed(inputManager.isCrouch());
        player.setShootPressed(inputManager.isShoot());

        player.update(cameraX ,SCENE_WIDTH, dtScale);

        PhysicsManager.updatePlayer(player, levelManager.getCurrentLevel(), dtScale);

        updateEnemies(dtScale);
        CollisionManager.checkBulletCollisions(player, enemies, cameraX, SCENE_WIDTH);
        CollisionManager.checkMeleeCollisions(player, enemies);

        updatePOW(dtScale);

        updatePlatforms();
        updateAllBullets(dtScale);

        if (trackDeaths()) {
            return;
        }
        updateLivesDisplay();
        updateHealthBar();
        updateCamera();
    }

    private void updateCamera() {
        double targetCameraX = player.getPositionX() - SCENE_WIDTH / 3.5;

        if(targetCameraX > cameraX) {
            double minCameraX = 0;
            double maxCameraX = (levelManager.getCurrentLevel().getLevelWidth() - 20) - SCENE_WIDTH;

            cameraX = Math.max(minCameraX, Math.min(targetCameraX, maxCameraX));
        }

        levelManager.getCurrentLevel().getBackground().setTranslateX(-cameraX);
        player.getSpriteView().setX(player.getPositionX() - cameraX);
    }

    private void updatePlatforms() {
        Level currentLevel = levelManager.getCurrentLevel();
        ArrayList<GameObjects> entities = currentLevel.getGameObjects();
        for (GameObjects entity : entities) {
            if (entity instanceof Platform) {
                entity.update(cameraX ,SCENE_WIDTH);
            }
        }
    }

    private void updateEnemies(double dtScale) {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);

            if (!enemy.isActive() && shouldActivate(enemy)) {
                enemy.setActive(true);
            }

            enemy.update(cameraX, SCENE_WIDTH, dtScale);

            if (enemy.isActive()) {
                if (enemy instanceof BasicEnemy) {
                    BasicEnemy basic = (BasicEnemy) enemy;
                    basic.updateBehavior(player.getPositionX(), dtScale);
                } else if (enemy instanceof ShieldedEnemy) {
                    ShieldedEnemy shielded = (ShieldedEnemy) enemy;
                    shielded.updateBehavior(player, dtScale);
                }
            }

            if (enemy.isDead()) {
                boolean readyToRemove = enemy.isReadyToRemove();
                if (!readyToRemove) {
                    continue;
                }
                
                // Award XP for killing enemy
                if(enemy instanceof BasicEnemy){
                    xpCount += 50;
                    BasicEnemy basic = (BasicEnemy) enemy;
                    ArrayList<Bullet> enemyBullets = basic.getBullets();

                    for (Bullet bullet : enemyBullets) {
                        getChildren().remove(bullet.getBulletImage());
                    }
                    enemyBullets.clear();

                } else if(enemy instanceof ShieldedEnemy){
                    xpCount += 100;
                    ShieldedEnemy shielded = (ShieldedEnemy) enemy;
                }
                
                updateXPDisplay();

                getChildren().remove(enemy.getSpriteView());
                enemies.remove(i);
            }
        }
    }

    private boolean shouldActivate(Enemy enemy) {
        if (enemy instanceof BasicEnemy) {
            BasicEnemy basic = (BasicEnemy) enemy;
            return basic.shouldActivate(player.getPositionX(), cameraX, SCENE_WIDTH);
        } else if (enemy instanceof ShieldedEnemy) {
            ShieldedEnemy shielded = (ShieldedEnemy) enemy;
            return shielded.shouldActivate(player.getPositionX(), cameraX, SCENE_WIDTH);
        }
        return false;
    }

    private void updateAllBullets(double dtScale) {
        shooterBuffer.clear();
        shooterBuffer.add(player);

        for (Enemy enemy : enemies) {
            if (enemy instanceof BasicEnemy) {
                shooterBuffer.add(enemy);
            }
        }

        for (Character shooter : shooterBuffer) {
            updateBulletsForEntity(shooter.getBullets(), dtScale);
        }
    }

    private void updateBulletsForEntity(ArrayList<Bullet> bullets, double dtScale) {
        for (Bullet bullet : bullets) {
            if (!getChildren().contains(bullet.getBulletImage())) {
                getChildren().add(bullet.getBulletImage());
            }
        }

        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(cameraX, dtScale);

            if (!bullet.isActive() || bullet.isOutOfBounds(levelManager.getCurrentLevel().getLevelWidth())) {
                getChildren().remove(bullet.getBulletImage());
                bullets.remove(i);
            }
        }
    }

    private void updatePOW(double dtScale){
        for(POW prisoner: pow){
            prisoner.update(cameraX,player.getPositionX(), dtScale);
        }
    }

    private boolean trackDeaths() {
        boolean aliveNow = player.isAlive();

        if (playerWasAlive && !aliveNow) {
            deathCount++;
            updateLivesDisplay();
            if (deathCount >= MAX_DEATHS) {
                triggerGameOver();
                return true;
            }
        }

        playerWasAlive = aliveNow;
        return false;
    }

    private void triggerGameOver() {
        stopGame();
        if (stage != null) {
            GameOverMenu gameOverMenu = new GameOverMenu(stage);
            Scene gameOverScene = new Scene(gameOverMenu);
            stage.setScene(gameOverScene);
        }
    }

    private void updateScale() {
        if (getScene() != null && getScene().getWidth() > 0 && getScene().getHeight() > 0) {
            double sceneW = getScene().getWidth();
            double sceneH = getScene().getHeight();
            
            double scaleX = sceneW / SCENE_WIDTH;
            double scaleY = sceneH / SCENE_HEIGHT;
            double scale = Math.min(scaleX, scaleY);
            
            getTransforms().clear();
            getTransforms().add(new Scale(scale, scale, 0, 0));
            
            setTranslateX((sceneW - SCENE_WIDTH * scale) / 2.0);
            setTranslateY((sceneH - SCENE_HEIGHT * scale) / 2.0);
        }
    }

    private void createLivesDisplay() {
        livesDisplay = new Text();
        livesDisplay.setX(30);
        livesDisplay.setY(50);
        livesDisplay.setFont(UITheme.boldFont(24));
        livesDisplay.setFill(Color.web("#ffcc00"));
        livesDisplay.setStroke(Color.web("#ff8800"));
        livesDisplay.setStrokeWidth(2);
        getChildren().add(livesDisplay);
        updateLivesDisplay();
    }

    private void updateLivesDisplay() {
        int livesRemaining = MAX_DEATHS - deathCount;
        StringBuilder display = new StringBuilder("LIVES: ");
        for (int i = 0; i < livesRemaining; i++) {
            display.append("● ");
        }
        livesDisplay.setText(display.toString());
    }

    private void createHealthBar() {
        healthBarBg = new Rectangle(140, 21);
        healthBarBg.setX(30);
        healthBarBg.setY(750 - 21 - 30);
        healthBarBg.setFill(Color.web("#333333"));
        healthBarBg.setStroke(Color.web("#ffcc00"));
        healthBarBg.setStrokeWidth(2);
        getChildren().add(healthBarBg);

        healthBarFg = new Rectangle(140, 21);
        healthBarFg.setX(30);
        healthBarFg.setY(750 - 21 - 30);
        healthBarFg.setFill(Color.web("#00ff00"));
        getChildren().add(healthBarFg);

        updateHealthBar();
    }

    private void updateHealthBar() {
        if (player != null) {
            int currentHealth = player.getHealth();
            int maxHealth = player.getMaxHealth();
            double healthPercent = (double) currentHealth / maxHealth;
            
            healthBarFg.setWidth(140 * healthPercent);
            
            if (healthPercent > 0.5) {
                healthBarFg.setFill(Color.web("#00ff00"));
            } else if (healthPercent > 0.25) {
                healthBarFg.setFill(Color.web("#ffff00"));
            } else {
                healthBarFg.setFill(Color.web("#ff0000"));
            }
        }
    }

    private void createXPDisplay() {
        xpDisplay = new Text();
        xpDisplay.setX(1200 - 150);
        xpDisplay.setY(50);
        xpDisplay.setFont(UITheme.boldFont(24));
        xpDisplay.setFill(Color.web("#ffcc00"));
        xpDisplay.setStroke(Color.web("#ff8800"));
        xpDisplay.setStrokeWidth(2);
        getChildren().add(xpDisplay);
        updateXPDisplay();
    }

    private void updateXPDisplay() {
        xpDisplay.setText("XP: " + xpCount);
    }
}