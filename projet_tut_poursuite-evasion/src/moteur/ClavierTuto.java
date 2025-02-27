package moteur;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import simulation.Deplacement;
import simulation.Simulation;
import simulation.tuto.SimulationTutoriel;

public class ClavierTuto implements EventHandler<KeyEvent> {
    private SimulationTutoriel simulation;

    public ClavierTuto(SimulationTutoriel s) {
        this.simulation = s;
    }

    @Override
    public void handle(KeyEvent keyEvent) {
        if (!simulation.etreFini()) {
            simulation.update(keyEvent);
        }
    }
}