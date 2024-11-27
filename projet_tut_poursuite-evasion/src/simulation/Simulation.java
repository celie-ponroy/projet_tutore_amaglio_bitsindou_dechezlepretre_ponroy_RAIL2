package simulation;

import simulation.personnages.Personnage;

public class Simulation {
    private int nbTours;
    private Personnage gardien;
    private Personnage prisonnier;
    public static int[][] carte;

    Simulation() {
        this.nbTours = 0;
    }
}
