package moteur;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import simulation.Deplacement;
import simulation.Simulation;

public class Clavier implements EventHandler<KeyEvent> {
    private Simulation simulation;

    public Clavier(Simulation s) {
        this.simulation = s;
    }

    @Override
    public void handle(KeyEvent keyEvent) {
        if (!simulation.etreFini()) {
            switch (keyEvent.getCode()) {
                case Z:
                case NUMPAD8:
                    this.simulation.deplacementJoueur(Deplacement.HAUT);
                    break;
                case Q:
                case NUMPAD4:
                    this.simulation.deplacementJoueur(Deplacement.GAUCHE);
                    break;
                case X:
                case NUMPAD2:
                    this.simulation.deplacementJoueur(Deplacement.BAS);
                    break;
                case S:
                case NUMPAD5:
                    this.simulation.deplacementJoueur(Deplacement.AUCUN);
                    break;
                case D:
                case NUMPAD6:
                    this.simulation.deplacementJoueur(Deplacement.DROITE);
                    break;
                case A:
                case NUMPAD7:
                    this.simulation.deplacementJoueur(Deplacement.DIAG_HAUT_GAUCHE);
                    break;
                case E:
                case NUMPAD9:
                    this.simulation.deplacementJoueur(Deplacement.DIAG_HAUT_DROITE);
                    break;
                case W:
                case NUMPAD1:
                    this.simulation.deplacementJoueur(Deplacement.DIAG_BAS_GAUCHE);
                    break;
                case C:
                case NUMPAD3:
                    this.simulation.deplacementJoueur(Deplacement.DIAG_BAS_DROITE);
                    break;
            }
        }
    }
}