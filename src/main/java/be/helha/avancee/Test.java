package be.helha.avancee;

import be.helha.avancee.items.Shield;

public class Test {
    public static void main(String[] args) {
        System.out.println("Hello, World!");

        Shield shield = new Shield("Bouclier en fer magic");
        System.out.println(shield);
        System.out.println(shield.getName());
    }
}
