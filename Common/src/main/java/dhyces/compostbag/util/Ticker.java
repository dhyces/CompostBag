package dhyces.compostbag.util;

public class Ticker {
    final int first;
    final int rest;
    int ticks;

    /**
     * On the first interaction, it should be a longer delay before more items are input.
     * The rest should be fairly short, but enough delay to see items disappear.
     */
    public Ticker(int firstInteract, int rest) {
        this.first = firstInteract;
        this.rest = rest;
    }

    /**
     * Returns whether or not this tick should interact
     */
    public boolean tick() {
        ticks++;
        if (ticks <= first) {
            return ticks % first == 0;
        }
        if (ticks % rest == 0) {
            ticks = first;
            return true;
        }
        return false;
    }

    public void restart() {
        ticks = 0;
    }

    public boolean inProgress() {
        return ticks >= first;
    }
}
