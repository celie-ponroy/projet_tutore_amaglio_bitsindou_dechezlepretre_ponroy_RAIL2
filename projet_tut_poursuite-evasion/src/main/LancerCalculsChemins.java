package main;

import simulation.CalculChemins;
import simulation.CalculVision;

import java.io.IOException;

public class LancerCalculsChemins {
    public static void main(String[] args) throws IOException {
        System.out.println("Lancer les calculs des chemins");
        CalculChemins.ecrireChemins();
    }
}
