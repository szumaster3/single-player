package core.game.node.entity.player.link.appearance;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * The type Body part.
 */
public final class BodyPart {
    private int look;
    private int color;

    /**
     * Instantiates a new Body part.
     */
    public BodyPart() {

    }

    /**
     * Instantiates a new Body part.
     *
     * @param look  the look
     * @param color the color
     */
    public BodyPart(final int look, final int color) {
        this.look = look;
        this.color = color;
    }

    /**
     * Instantiates a new Body part.
     *
     * @param look the look
     */
    public BodyPart(final int look) {
        this(look, 0);
    }

    /**
     * Parse.
     *
     * @param part the part
     */
    public void parse(JsonObject part) {
        int look = 0;
        int color = 0;

        try {
            JsonElement lookEl = part.get("look");
            if (lookEl != null && lookEl.isJsonPrimitive()) {
                JsonPrimitive prim = lookEl.getAsJsonPrimitive();
                if (prim.isNumber()) {
                    look = prim.getAsInt();
                } else if (prim.isString()) {
                    look = Integer.parseInt(prim.getAsString().replace("\"", "").trim());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to parse 'look': " + part.get("look"));
        }

        try {
            JsonElement colorEl = part.get("color");
            if (colorEl != null && colorEl.isJsonPrimitive()) {
                JsonPrimitive prim = colorEl.getAsJsonPrimitive();
                if (prim.isNumber()) {
                    color = prim.getAsInt();
                } else if (prim.isString()) {
                    color = Integer.parseInt(prim.getAsString().replace("\"", "").trim());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to parse 'color': " + part.get("color"));
        }

        changeLook(look);
        changeColor(color);
    }

    /**
     * Change look.
     *
     * @param look the look
     */
    public void changeLook(final int look) {
        this.look = look;
    }

    /**
     * Change color.
     *
     * @param color the color
     */
    public void changeColor(final int color) {
        this.color = color;
    }

    /**
     * Gets look.
     *
     * @return the look
     */
    public int getLook() {
        return look;
    }

    /**
     * Gets color.
     *
     * @return the color
     */
    public int getColor() {
        return color;
    }

}
