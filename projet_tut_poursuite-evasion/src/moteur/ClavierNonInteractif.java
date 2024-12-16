package moteur;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import simulation.Simulation;

public class ClavierNonInteractif implements EventHandler<KeyEvent> {
    private Simulation simulation;

    public ClavierNonInteractif(Simulation s) {
        this.simulation = s;
    }

    @Override
    public void handle(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.RIGHT){
            simulation.deplacerAgents();
        }
    }
}
