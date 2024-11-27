package affichage;

import javafx.scene.canvas.Canvas;
import moteur.Jeu;
import simulation.Simulation;

public class VuePrincipale implements DessinJeu {
    private Simulation simulation;

    @Override
    public void dessinerJeu(Jeu jeu, Canvas canvas) {
        // TODO Auto-generated method stub
        this.simulation = (Simulation) jeu;

    }
}
