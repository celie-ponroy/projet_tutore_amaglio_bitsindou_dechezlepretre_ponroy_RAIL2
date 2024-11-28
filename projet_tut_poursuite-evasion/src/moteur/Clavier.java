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
            case NUMPAD7:
                this.diagHG = true;
                break;
            // si en haut a droite
            case E:
            case NUMPAD9:
                this.diagHD = true;
                break;

            // si touche haut
            case Z:
            case NUMPAD8:
                this.haut = true;
                break;

            // si touche gauche
            case Q:
            case NUMPAD4:
                this.gauche = true;
                break;

            // si touche droite
            case D:
            case NUMPAD6:
                this.droite = true;
                break;

            //si touche neutre
            case S:
            case NUMPAD5:
                this.neutre = true;
                break;
            // si en bas a gauche
            case W:
            case NUMPAD1:
                this.diagBG = true;
                break;
            // si en bas a droite
            case C:
            case NUMPAD3:
                this.diagBD = true;
                break;
            // si en bas
            case X:
            case NUMPAD2:
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
            case NUMPAD7:
                this.diagHG = false;
                break;
            // si en haut a droite
            case E:
            case NUMPAD9:
                this.diagHD = false;
                break;

            // si touche haut
            case Z:
            case NUMPAD8:
                this.haut = false;
                break;

            // si touche gauche
            case Q:
            case NUMPAD4:
                this.gauche = false;
                break;

            // si touche droite
            case D:
            case NUMPAD6:
                this.droite = false;
                break;

            //si touche neutre
            case S:
            case NUMPAD5:
                this.neutre = false;
                break;
            // si en bas a gauche
            case W:
            case NUMPAD1:
                this.diagBG = false;
                break;
            // si en bas a droite
            case C:
            case NUMPAD3:
                this.diagBD = false;
                break;
            // si en bas
            case X:
            case NUMPAD2:
                this.bas = false;
                break;
        }
    }
}
