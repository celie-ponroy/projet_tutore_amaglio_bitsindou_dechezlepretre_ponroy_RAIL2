package main;

import affichage.VuePrincipale;
import moteur.Jeu;
import moteur.MoteurJeu;
import simulation.Simulation;

import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        int width = 800;
        int height = 600;
        int pFPS = 10;

        // creation des objets
        Jeu simulation = new Simulation();
        VuePrincipale  vp = new VuePrincipale();

        // parametrage du moteur de jeu
        MoteurJeu.setTaille(width, height);
        MoteurJeu.setFPS(pFPS);

        // lancement du jeu
        MoteurJeu.launch(simulation, vp);

    }

}
