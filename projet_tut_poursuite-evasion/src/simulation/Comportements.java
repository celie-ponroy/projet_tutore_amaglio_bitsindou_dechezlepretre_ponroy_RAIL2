package simulation;

import java.io.Serializable;

public enum Comportements implements Serializable {
    ArbreDeterministePris, //2
    ArbreDeterministeGard,
    ArbreDeterministev2, //prisonnier
    AleatoirePris,
    AleatoireGard,//2
    ArbreAleatoire, //gardien
    ReseauArbreCNN, //gardien
    ReseauRenforcement, //prisonnier
    ReseauArbreMLP; //gardien

    //Méthode toString

    @Override
    public String toString() {
        String res = "";
        switch (this) {
            case ArbreDeterministePris:
                res = "Arbre déterministe du prisonnier";
                break;
            case ArbreDeterministeGard:
                res = "Arbre déterministe du gardien";
                break;
            case ArbreDeterministev2:
                res = "Arbre déterministe v2";
                break;
            case AleatoirePris:
                res = "Aleatoire du prisonnier";
                break;
            case AleatoireGard:
                res = "Aleatoire du gardien";
                break;
            case ArbreAleatoire:
                res = "Arbre aléatoire";
                break;
            case ReseauArbreCNN:
                res = "Réseau de neurones (CNN)";
                break;
            case ReseauRenforcement:
                res = "Réseau de neurones (Renforcement)";
                break;
            case ReseauArbreMLP:
                res = "Réseau de neurones (MLP)";
                break;
        }
        return res;
    }
}
