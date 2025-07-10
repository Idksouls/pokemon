public enum PokemonType {
    NORMAL, FIRE, WATER, ELECTRIC, GRASS, ICE, FIGHTING, POISON, GROUND, FLYING, PSYCHIC, BUG, ROCK, GHOST, DRAGON, DARK, STEEL, FAIRY;

    public double getEffectiveAgainst(PokemonType effectiveType) {
        switch (this) {
            case FIRE:
                switch (effectiveType) {
                    case GRASS: return 2.0;
                    case WATER: return 0.5;
                    case FIRE: return 0.5;
                    case BUG: return 2.0;
                    case ICE: return 2.0;
                    case ROCK: return 0.5;
                    case STEEL: return 2.0;
                    default: return 1.0;
                }
            case WATER:
                switch (effectiveType) {
                    case FIRE: return 2.0;
                    case GRASS: return 0.5;
                    case WATER: return 0.5;
                    case GROUND: return 2.0;
                    case ROCK: return 2.0;
                    default: return 1.0;
                }
            case GRASS:
                switch (effectiveType) {
                    case WATER: return 2.0;
                    case FIRE: return 0.5;
                    case GRASS: return 0.5;
                    case POISON: return 0.5;
                    case GROUND: return 2.0;
                    case ROCK: return 2.0;
                    case FLYING: return 0.5;
                    case BUG: return 0.5;
                    case DRAGON: return 0.5;
                    case STEEL: return 0.5;
                    default: return 1.0;
                }
            case ELECTRIC:
                switch (effectiveType) {
                    case WATER: return 2.0;
                    case ELECTRIC: return 0.5;
                    case FLYING: return 2.0;
                    case GROUND: return 0.0;
                    case DRAGON: return 0.5;
                    default: return 1.0;
                }
            case PSYCHIC:
                switch (effectiveType) {
                    case FIGHTING: return 2.0;
                    case POISON: return 2.0;
                    case PSYCHIC: return 0.5;
                    case STEEL: return 0.5;
                    case DARK: return 0.0;
                    default: return 1.0;
                }
            case NORMAL:
                switch (effectiveType) {
                    case ROCK: return 0.5;
                    case STEEL: return 0.5;
                    case GHOST: return 0.0;
                    default: return 1.0;
                }
            case FLYING:
                switch (effectiveType) {
                    case FIGHTING: return 2.0;
                    case BUG: return 2.0;
                    case GRASS: return 2.0;
                    case ELECTRIC: return 0.5;
                    case ROCK: return 0.5;
                    case STEEL: return 0.5;
                    default: return 1.0;
                }
            case BUG:
                switch (effectiveType) {
                    case GRASS: return 2.0;
                    case PSYCHIC: return 2.0;
                    case DARK: return 2.0;
                    case FIGHTING: return 0.5;
                    case FLYING: return 0.5;
                    case POISON: return 0.5;
                    case GHOST: return 0.5;
                    case STEEL: return 0.5;
                    case FIRE: return 0.5;
                    default: return 1.0;
                }
            case POISON:
                switch (effectiveType) {
                    case GRASS: return 2.0;
                    case POISON: return 0.5;
                    case GROUND: return 0.5;
                    case ROCK: return 0.5;
                    case GHOST: return 0.5;
                    case STEEL: return 0.0;
                    default: return 1.0;
                }
            case GROUND:
                switch (effectiveType) {
                    case FIRE: return 2.0;
                    case ELECTRIC: return 2.0;
                    case POISON: return 2.0;
                    case ROCK: return 2.0;
                    case STEEL: return 2.0;
                    case FLYING: return 0.0;
                    default: return 1.0;
                }
            case ROCK:
                switch (effectiveType) {
                    case FIRE: return 2.0;
                    case ICE: return 2.0;
                    case FLYING: return 2.0;
                    case BUG: return 2.0;
                    case FIGHTING: return 0.5;
                    case GROUND: return 0.5;
                    case STEEL: return 0.5;
                    default: return 1.0;
                }
            case GHOST:
                switch (effectiveType) {
                    case PSYCHIC: return 2.0;
                    case GHOST: return 2.0;
                    case DARK: return 0.5;
                    case NORMAL: return 0.0;
                    default: return 1.0;
                }
            case DRAGON:
                switch (effectiveType) {
                    case DRAGON: return 2.0;
                    case STEEL: return 0.5;
                    case FAIRY: return 0.0;
                    default: return 1.0;
                }
            case DARK:
                switch (effectiveType) {
                    case PSYCHIC: return 2.0;
                    case GHOST: return 2.0;
                    case DARK: return 0.5;
                    case FIGHTING: return 0.5;
                    case FAIRY: return 0.5;
                    default: return 1.0;
                }
            case STEEL:
                switch (effectiveType) {
                    case ICE: return 2.0;
                    case ROCK: return 2.0;
                    case FAIRY: return 2.0;
                    case FIRE: return 0.5;
                    case WATER: return 0.5;
                    case ELECTRIC: return 0.5;
                    case STEEL: return 0.5;
                    default: return 1.0;
                }
            case FAIRY:
                switch (effectiveType) {
                    case FIGHTING: return 2.0;
                    case DRAGON: return 2.0;
                    case DARK: return 2.0;
                    case POISON: return 0.5;
                    case STEEL: return 0.5;
                    case FIRE: return 0.5;
                    default: return 1.0;
                }
            case FIGHTING:
                switch (effectiveType) {
                    case NORMAL: return 2.0;
                    case ROCK: return 2.0;
                    case STEEL: return 2.0;
                    case ICE: return 2.0;
                    case DARK: return 2.0;
                    case POISON: return 0.5;
                    case FLYING: return 0.5;
                    case PSYCHIC: return 0.5;
                    case BUG: return 0.5;
                    case FAIRY: return 0.5;
                    case GHOST: return 0.0;
                    default: return 1.0;
                }
            case ICE:
                switch (effectiveType) {
                    case GRASS: return 2.0;
                    case GROUND: return 2.0;
                    case FLYING: return 2.0;
                    case DRAGON: return 2.0;
                    case FIRE: return 0.5;
                    case WATER: return 0.5;
                    case ICE: return 0.5;
                    case STEEL: return 0.5;
                    default: return 1.0;
                }
            default:
                return 1.0;
        }
    }
}

