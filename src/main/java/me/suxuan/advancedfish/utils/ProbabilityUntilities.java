package me.suxuan.advancedfish.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author: CBer_SuXuan
 * @project: AdvancedFish
 * @className: ProbabilityUntilities
 * @date: 2023/4/5 19:32
 * @description: Fish Probability
 */
public class ProbabilityUntilities {
    // by https://www.spigotmc.org/threads/getting-random-key-from-hashmap-with-chance.199285/#post-2073868

    private Set<Chance> chances;
    private int sum;

    private class Chance {
        private int upperLimit;
        private int lowerLimit;
        private Object element;
        public Chance(Object element, int lowerLimit, int upperLimit) {
            this.element = element;
            this.upperLimit = upperLimit;
            this.lowerLimit = lowerLimit;
        }
        public int getUpperLimit() {
            return this.upperLimit;
        }
        public int getLowerLimit() {
            return this.lowerLimit;
        }
        public Object getElement() {
            return this.element;
        }
        public String toString() {
            return "[" + this.lowerLimit + "|" + this.upperLimit + "]: " + this.element.toString();
        }
    }
    public ProbabilityUntilities() {
        this.chances = new HashSet<>();
        this.sum = 0;
    }

    public ProbabilityUntilities(long seed) {
        this.chances = new HashSet<>();
        this.sum = 0;
    }

    public void addChance(Object element, int chance) {
        this.chances.add(new Chance(element, this.sum, this.sum + chance));
        this.sum = this.sum + chance;
    }

    public Object getRandomElement() {
        Random random = new Random();
        int index = random.nextInt(this.sum);
        for (Chance chance : this.chances) {
            if (chance.getLowerLimit() <= index && chance.getUpperLimit() > index) {
                return chance.getElement();
            }
        }
        return null;
    }

    public int getOptions() { // might be needed sometimes
        return this.sum;
    }
    public int getChoices() { // might be needed sometimes
        return this.chances.size();
    }
}
