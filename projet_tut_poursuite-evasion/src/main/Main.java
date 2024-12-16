package main;

import affichage.VuePrincipale;
import moteur.Jeu;
import moteur.MoteurJeu;
import simulation.Simulation;

import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        int width = 700;
        int height = 700;

        // creation des objets
        Jeu simulation = new Simulation();


        // parametrage du moteur de jeu
        MoteurJeu.setTaille(width, height);

        // lancement du jeu
        MoteurJeu.launch(simulation);

    }

}
