package content.global.activity.ttrail.anagram;

import core.plugin.Plugin;

public final class AnagramCluePlugin extends AnagramClueScroll {

    public AnagramCluePlugin() {
        this(null);
    }

    public AnagramCluePlugin(AnagramClue clue) {
        super(clue);
    }

    @Override
    public Plugin<Object> newInstance(Object arg) {
        for (AnagramClue clue : AnagramClue.values()) {
            register(new AnagramCluePlugin(clue));
        }
        return this;
    }
}
