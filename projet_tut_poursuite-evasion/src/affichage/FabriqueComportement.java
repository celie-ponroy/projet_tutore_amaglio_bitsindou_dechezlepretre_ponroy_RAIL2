package affichage;

import javafx.scene.control.ComboBox;
import simulation.Comportements;
import simulation.Simulation;
import simulation.comportement.Aleatoire;
import simulation.comportement.Comportement;

import static simulation.Comportements.*;

//Classe qui permet de créer des comportements et les comboBox associées
public class FabriqueComportement {

    //Créer une comboBox avec les comportements possibles du prisonnier
    public static ComboBox<Comportements> creerComboBoxPrisonnier() {
        ComboBox<Comportements> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(ArbreDeterministe, ArbreDeterministev2, Aleatoire, ArbreAleatoire);
        comboBox.setValue(ArbreDeterministe); //
        return comboBox;
    }

    //Créer une comboBox avec les comportements possibles du policier
    public static ComboBox<Comportements> creerComboBoxPolicier() {
        ComboBox<Comportements> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(ArbreDeterministe, ArbreDeterministev2, Aleatoire, ArbreAleatoire, ReseauArbreDeterministe, ReseauArbreAleatoire);
        comboBox.setValue(ArbreDeterministe);
        return comboBox;
    }

    //Créer une simulation en fonction de la chaîne de caractères passée en paramètre
    public static Simulation creerComportement(boolean perso, String comportement) {
        Simulation simulation = null;
        switch (comportement) {
            case "ArbreDeterministe":
                return simulation = new Simulation(perso, Comportements.ArbreDeterministe);
            case "ArbreDeterministev2":
                return simulation = new Simulation(perso, Comportements.ArbreDeterministev2);
            case "Aleatoire":
                return simulation = new Simulation(perso, Comportements.Aleatoire);
            case "ArbreAleatoire":
                return simulation = new Simulation(perso, Comportements.ArbreAleatoire);
            case "ReseauArbreDeterministe":
                return simulation = new Simulation(perso, Comportements.ReseauArbreDeterministe);
            case "ReseauArbreAleatoire":
                return simulation = new Simulation(perso, Comportements.ReseauArbreAleatoire);
        }
        return simulation;
    }


}
