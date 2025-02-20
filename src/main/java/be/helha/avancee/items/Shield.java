package be.helha.avancee.items;

public class Shield extends Item {
    private int defense;


    public Shield(String name) {
        super(name, "Shield");
        this.defense = 5;
    }

    public int getDamage() {
        return defense;
    }

    public void setDamage(int damage) {
        this.defense = damage;
    }

    @Override
    public String toString() {
        return "Shield:{" + super.toString() + ", defense=" + defense + '}';
    }
}




