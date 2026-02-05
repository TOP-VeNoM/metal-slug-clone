import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class POW extends GameObjects {

    private String type;
    private SpriteAnimation animation;
    private double scale;
    private double speed;


    public POW(double x, double y , String type) {
        super(x, y);
        this.type = type;
        this.spriteView = new ImageView();
        this.scale = 2.75;
        this.speed = 0;
        spriteView.setScaleX(scale);
        spriteView.setScaleY(scale);

        loadAnimations();
    }

    private void loadAnimations() {
        if(type.equals("Old Man Sitting")) {
            Image animSheet = SpriteSheetLoader.loadImage("/Assets/POW/Old Man Sitting/Old_Man_Sitting.png");
            ArrayList<Rectangle2D> animFrames = SpriteSheetLoader.loadFrames("/Assets/POW/Old Man Sitting/Old_Man_Sitting_data.json");
            animation = new SpriteAnimation(spriteView, animSheet, animFrames, 10);
            animation.play();
        }

        if(type.equals("Old Man Log")) {
                Image animSheet = SpriteSheetLoader.loadImage("/Assets/POW/Old Man Log/Sheet.png");
                ArrayList<Rectangle2D> animFrames = SpriteSheetLoader.loadFrames("/Assets/POW/Old Man Log/sheet_data.json");
                animation = new SpriteAnimation(spriteView, animSheet, animFrames, 8);
                animation.play();
        }

        if(type.equals("Man Log")) {
            Image animSheet = SpriteSheetLoader.loadImage("/Assets/POW/Man Log/man.png");
            ArrayList<Rectangle2D> animFrames = SpriteSheetLoader.loadFrames("/Assets/POW/Man Log/man.json");
            animation = new SpriteAnimation(spriteView, animSheet, animFrames, 8);
            animation.play();
        }

        if(type.equals("Old Man Run")) {
            speed = 2 ;
            scale = 3;
            spriteView.setScaleX(scale);
            spriteView.setScaleY(scale);
            Image animSheet = SpriteSheetLoader.loadImage("/Assets/POW/Old Man Run/run.png");
            ArrayList<Rectangle2D> animFrames = SpriteSheetLoader.loadFrames("/Assets/POW/Old Man Run/run_data.json");
            animation = new SpriteAnimation(spriteView, animSheet, animFrames, 10);
            animation.play();
        }

        if(type.equals("Man Run")) {
            speed = 1.5 ;
            scale = 3;
            spriteView.setScaleX(scale);
            spriteView.setScaleY(scale);
            Image animSheet = SpriteSheetLoader.loadImage("/Assets/POW/Man Run/run.png");
            ArrayList<Rectangle2D> animFrames = SpriteSheetLoader.loadFrames("/Assets/POW/Man Run/run_data.json");
            animation = new SpriteAnimation(spriteView, animSheet, animFrames, 10);
            animation.play();
        }

        if(type.equals("Old Man Hanging")) {
            scale = 2.4;
            spriteView.setScaleX(scale);
            spriteView.setScaleY(scale);
            Image animSheet = SpriteSheetLoader.loadImage("/Assets/POW/Old Man Hanging/sheet.png");
            ArrayList<Rectangle2D> animFrames = SpriteSheetLoader.loadFrames("/Assets/POW/Old Man Hanging/data.json");
            animation = new SpriteAnimation(spriteView, animSheet, animFrames, 10);
            animation.play();
        }

        if(type.equals("Man Hanging")) {
            scale = 2.4;
            spriteView.setScaleX(scale);
            spriteView.setScaleY(scale);
            Image animSheet = SpriteSheetLoader.loadImage("/Assets/POW/Man Hanging/man_hanging.png");
            ArrayList<Rectangle2D> animFrames = SpriteSheetLoader.loadFrames("/Assets/POW/Man Hanging/man_hanging_data.json");
            animation = new SpriteAnimation(spriteView, animSheet, animFrames, 10);
            animation.play();
        }

        if(type.equals("Rope")) {
            scale = 2;
            spriteView.setScaleX(scale);
            spriteView.setScaleY(scale);

            Image animSheet = SpriteSheetLoader.loadImage("/Assets/POW/rope/rope.png");
            ArrayList<Rectangle2D> animFrames = SpriteSheetLoader.loadFrames("/Assets/POW/rope/rope_data.json");
            animation = new SpriteAnimation(spriteView, animSheet, animFrames, 1);
            animation.play();
        }

        if(type.equals("CEO")) {
            scale = 3.5;
            spriteView.setScaleX(scale);
            spriteView.setScaleY(scale);
            Image animSheet = SpriteSheetLoader.loadImage("/Assets/POW/CEO/ceo.png");
            ArrayList<Rectangle2D> animFrames = SpriteSheetLoader.loadFrames("/Assets/POW/CEO/ceo.json");
            animation = new SpriteAnimation(spriteView, animSheet, animFrames, 10);
            animation.play();
        }
    }

    @Override
    public void update(double cameraX, double playerPositionX, double dtScale) {
        double distanceToPlayer = Math.abs(playerPositionX - positionX);
        boolean shouldAnimate = distanceToPlayer < 2000;

        if (animation != null) {
            if (shouldAnimate && !animation.isPlaying()) {
                animation.play();
            } else if (!shouldAnimate && animation.isPlaying()) {
                animation.pause();
            }
        }

        if(playerPositionX >= 2500){
            positionX -= speed * dtScale;
        }
        spriteView.setX(positionX - cameraX);
        spriteView.setY(positionY);
    }

    @Override
    public void update(double cameraX, double playerPositionX) {
        update(cameraX, playerPositionX, 1.0);
    }
}
