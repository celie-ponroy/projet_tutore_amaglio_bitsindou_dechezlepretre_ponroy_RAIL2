package moteur;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import simulation.Deplacement;
import simulation.Simulation;

public class Clavier implements EventHandler<KeyEvent> {
    private Simulation simulation;

    public Clavier(Simulation s){
        this.simulation = s;
    }

    @Override
    public void handle(KeyEvent keyEvent) {
        switch (keyEvent.getCode()){
            case Z:
                this.simulation.deplacementJoueur(Deplacement.HAUT);
                break;
            case Q:
                this.simulation.deplacementJoueur(Deplacement.GAUCHE);
                break;
            case X:
                this.simulation.deplacementJoueur(Deplacement.BAS);
                break;
            case S:
                this.simulation.deplacementJoueur(Deplacement.AUCUN);
                break;
            case D:
                this.simulation.deplacementJoueur(Deplacement.DROITE);
                break;
            case A:
                this.simulation.deplacementJoueur(Deplacement.DIAG_HAUT_GAUCHE);
                break;
            case E:
                this.simulation.deplacementJoueur(Deplacement.DIAG_HAUT_DROITE);
                break;
            case W:
                this.simulation.deplacementJoueur(Deplacement.DIAG_BAS_GAUCHE);
                break;
            case C:
                this.simulation.deplacementJoueur(Deplacement.DIAG_BAS_DROITE);
                break;
        }
    }
}