import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.List;

public class SpriteAnimation {
    private final ImageView imageView;
    private final Image spriteSheet;
    private final ArrayList<Rectangle2D> frames;
    private final long frameDurationNs;

    private int currentFrame = 0;
    private long lastUpdateTime = 0;
    private boolean isPlaying = false;
    private int loopCount = 0;

    private static final List<SpriteAnimation> ALL_ANIMATIONS = new ArrayList<>();
    private static final AnimationTimer SHARED_TIMER = new AnimationTimer() {
        @Override
        public void handle(long now) {
            for (SpriteAnimation anim : ALL_ANIMATIONS) {
                if (!anim.isPlaying) {
                    continue;
                }

                if (anim.lastUpdateTime == 0) {
                    anim.lastUpdateTime = now;
                    continue;
                }

                long elapsedNs = now - anim.lastUpdateTime;
                if (elapsedNs >= anim.frameDurationNs) {
                    anim.currentFrame = anim.currentFrame + 1;

                    if (anim.currentFrame >= anim.frames.size()) {
                        anim.currentFrame = 0;
                        anim.loopCount++;
                    }

                    Rectangle2D frame = anim.frames.get(anim.currentFrame);
                    anim.imageView.setViewport(frame);
                    anim.lastUpdateTime = now;
                }
            }
        }
    };

    static {
        SHARED_TIMER.start();
    }

    public SpriteAnimation(ImageView imageView, Image spriteSheet, ArrayList<Rectangle2D> frames, double fps) {
        this.imageView = imageView;
        this.spriteSheet = spriteSheet;
        this.frames = frames;
        this.frameDurationNs = (long)(1_000_000_000.0 / fps);
        ALL_ANIMATIONS.add(this);
    }

    public void play() {
        if (frames == null || frames.isEmpty()) {
            System.err.println("Cannot play - no frames available");
            return;
        }

        imageView.setImage(spriteSheet);

        currentFrame = 0;
        loopCount = 0;
        lastUpdateTime = 0;
        isPlaying = true;

        imageView.setViewport(frames.get(0));
    }

    public void pause() {
        isPlaying = false;
    }

    public void stop() {
        isPlaying = false;
        currentFrame = 0;
        loopCount = 0;
        lastUpdateTime = 0;

        if (frames != null && !frames.isEmpty()) {
            imageView.setViewport(frames.get(0));
        }
    }

    public void setToFirstFrame() {
        if (frames != null && !frames.isEmpty()) {
            imageView.setImage(spriteSheet);
            imageView.setViewport(frames.get(0));
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public int getLoopCount() {
        return loopCount;
    }
}