package affichage;

import javafx.scene.control.ComboBox;
import simulation.Comportements;
import simulation.Simulation;

import static simulation.Comportements.*;

//Classe qui permet de créer des comportements et les comboBox associées
public class FabriqueComportement {

    //Créer une comboBox avec les comportements possibles du prisonnier
    public static ComboBox<String> creerComboBoxPrisonnier() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(ArbreDeterministePris.toString(), ArbreDeterministev2.toString(), AleatoirePris.toString(), ReseauRenforcement.toString());
        comboBox.setValue(ArbreDeterministePris.toString()); //Par défaut
        return comboBox;
    }

    //Créer une comboBox avec les comportements possibles du policier
    public static ComboBox<String> creerComboBoxGardien() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(ArbreDeterministeGard.toString(), AleatoireGard.toString(), ArbreAleatoire.toString(), ReseauArbreCNN.toString(), ReseauArbreMLP.toString(), ReseauArbreAleatoire.toString());
        comboBox.setValue(ArbreDeterministeGard.toString()); //Par défaut
        return comboBox;
    }

    //Créer une simulation en fonction de la chaîne de caractères passée en paramètre
    public static Simulation creerSimulation(String comportement) {
        Simulation simulation = null;
        switch (comportement) {
            case "Arbre déterministe du prisonnier":
                return simulation = new Simulation(false , Comportements.ArbreDeterministePris);
            case "Arbre déterministe du gardien":
                return simulation = new Simulation(true, ArbreDeterministeGard);
            case "Arbre déterministe v2":
                return simulation = new Simulation(false, Comportements.ArbreDeterministev2);
            case "Aleatoire du prisonnier":
                return simulation = new Simulation(false, Comportements.AleatoirePris);
            case "Aleatoire du gardien":
                return simulation = new Simulation(true, Comportements.AleatoireGard);
            case "Arbre aléatoire":
                return simulation = new Simulation(true, Comportements.ArbreAleatoire);
            case "Réseau de neurones (CNN)":
                return simulation = new Simulation(true, Comportements.ReseauArbreCNN);
            case "Réseau de neurones (Renforcement)":
                return simulation = new Simulation(false, Comportements.ReseauRenforcement);
            case "Réseau de neurones (MLP)":
                return simulation = new Simulation(true, Comportements.ReseauArbreMLP);
            case "Réseau de neurones (Arbre aléatoire)":
                return simulation = new Simulation(true, Comportements.ReseauArbreAleatoire);
        }
        return simulation;
    }

    //Renvoie un comportement en fonction de la chaîne de caractères passée en paramètre
    public static Comportements creerComportement ( String comportement) {
        Comportements comportements = null;
        switch (comportement) {
            case "Arbre déterministe du prisonnier":
                comportements = Comportements.ArbreDeterministePris;
                break;
            case "Arbre déterministe du gardien":
                comportements = Comportements.ArbreDeterministeGard;
                break;
            case "Arbre déterministe v2":
                comportements = Comportements.ArbreDeterministev2;
                break;
            case "Aleatoire du prisonnier":
                comportements = Comportements.AleatoirePris;
                break;
            case "Aleatoire du gardien":
                comportements = Comportements.AleatoireGard;
                break;
            case "Arbre aléatoire":
                comportements = Comportements.ArbreAleatoire;
                break;
            case "Réseau de neurones (CNN)":
                comportements = Comportements.ReseauArbreCNN;
                break;
            case "Réseau de neurones (Renforcement)":
                comportements = Comportements.ReseauRenforcement;
                break;
            case "Réseau de neurones (MLP)":
                comportements = Comportements.ReseauArbreMLP;
                break;
            case "Réseau de neurones (Arbre aléatoire)":
                comportements = Comportements.ReseauArbreAleatoire;
                break;
        }
        return comportements;
    }


}
