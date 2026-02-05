import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SpriteSheetLoader {

    private static final Map<String, ArrayList<Rectangle2D>> FRAME_CACHE = new HashMap<>();
    private static final Map<String, Image> IMAGE_CACHE = new HashMap<>();

    public static ArrayList<Rectangle2D> loadFrames(String resourcePath) {
        ArrayList<Rectangle2D> cached = FRAME_CACHE.get(resourcePath);
        if (cached != null) {
            return cached;
        }

        ArrayList<Rectangle2D> frames = new ArrayList<>();

        try {
            InputStream is = SpriteSheetLoader.class.getResourceAsStream(resourcePath);
            if (is == null) {
                System.err.println("Could not load: " + resourcePath);
                return frames;
            }

            JsonObject root = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();
            JsonArray sprites = root.getAsJsonArray("sprites");

            for (JsonElement element : sprites) {
                JsonObject sprite = element.getAsJsonObject();
                int x = sprite.get("x").getAsInt();
                int y = sprite.get("y").getAsInt();
                int width = sprite.get("width").getAsInt();
                int height = sprite.get("height").getAsInt();

                frames.add(new Rectangle2D(x, y, width, height));
            }

            ArrayList<Rectangle2D> immutableFrames = new ArrayList<>(frames);
            FRAME_CACHE.put(resourcePath, immutableFrames);

            System.out.println("Successfully loaded " + frames.size() + " frames with Gson");

        } catch (Exception e) {
            System.err.println("Gson error: " + e.getMessage());
            e.printStackTrace();
        }

        return FRAME_CACHE.getOrDefault(resourcePath, frames);
    }

    public static Image loadImage(String resourcePath) {
        Image cached = IMAGE_CACHE.get(resourcePath);
        if (cached != null) {
            return cached;
        }

        InputStream is = SpriteSheetLoader.class.getResourceAsStream(resourcePath);
        if (is == null) {
            System.err.println("Could not load image: " + resourcePath);
            return null;
        }

        Image img = new Image(is);
        IMAGE_CACHE.put(resourcePath, img);
        return img;
    }
}