public class Pokemon {
    private String name;
    private int hp;
    private int maxHp;
    private int attackPower;
    private PokemonType effectiveType;
    private PokemonType moveType;
    private int tier;

    public Pokemon(String name, int hp, int maxHp, int attackPower, PokemonType effectiveType, PokemonType moveType, int tier) {
        this.name = name;
        this.hp = hp;
        this.maxHp = maxHp;
        this.attackPower = attackPower;
        this.effectiveType = effectiveType;
        this.moveType = moveType;
        this.tier = tier;
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void setName(String name) {
		this.name = name;
	}

    public void setHp(int hp) {
        if (hp < 0) {
            this.hp = 0;
        } else {
            this.hp = hp;
        }
    }

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	public void setAttackPower(int attackPower) {
		this.attackPower = attackPower;
	}

	public void setEffectiveType(PokemonType effectiveType) {
		this.effectiveType = effectiveType;
	}

	public void setMoveType(PokemonType moveType) {
		this.moveType = moveType;
	}

	public void setTier(int tier) {
		this.tier = tier;
	}

	public PokemonType getEffectiveType() {
        return effectiveType;
    }

    public PokemonType getMoveType() {
        return moveType;
    }

    public int getTier() {
        return tier;
    }
}
