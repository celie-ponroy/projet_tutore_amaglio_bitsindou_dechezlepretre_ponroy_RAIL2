package moteur;

import javafx.scene.input.KeyEvent;

public class Clavier {

    /**
     * controle appuyes
     */
    public boolean diagHG, haut,diagHD ,neutre, diagBG ,gauche,diagBD, droite,bas;


    /**
     * stocke les commandes
     *
     * @param event evenement clavier
     */
    public void appuyerTouche(KeyEvent event) {

        switch (event.getCode()) {
            // si en haut a gauche
            case A:
                this.diagHG = true;
                break;
            // si en haut a droite
            case E:
                this.diagHD = true;
                break;

            // si touche haut
            case Z:
                this.haut = true;
                break;

            // si touche gauche
            case Q:
                this.gauche = true;
                break;

            // si touche droite
            case D:
                this.droite = true;
                break;

            //si touche neutre
            case S:
                this.neutre = true;
                break;
            // si en bas a gauche
            case W:
                this.diagBG = true;
                break;
            // si en bas a droite
            case C:
                this.diagBD = true;
                break;
            // si en bas
            case X:
                this.bas = true;
                break;
        }

    }

    /**
     * stocke les commandes
     *
     * @param event evenement clavier
     */
    public void relacherTouche(KeyEvent event) {

        switch (event.getCode()) {

            // si en haut a gauche
            case A:
                this.diagHG = false;
                break;
            // si en haut a droite
            case E:
                this.diagHD = false;
                break;

            // si touche haut
            case Z:
                this.haut = false;
                break;

            // si touche gauche
            case Q:
                this.gauche = false;
                break;

            // si touche droite
            case D:
                this.droite = false;
                break;

            //si touche neutre
            case S:
                this.neutre = false;
                break;
            // si en bas a gauche
            case W:
                this.diagBG = false;
                break;
            // si en bas a droite
            case C:
                this.diagBD = false;
                break;
            // si en bas
            case X:
                this.bas = false;
                break;
        }
    }
}
