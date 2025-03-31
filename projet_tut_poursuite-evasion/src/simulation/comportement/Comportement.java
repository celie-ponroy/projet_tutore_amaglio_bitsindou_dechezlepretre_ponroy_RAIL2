package simulation.comportement;

import simulation.Comportements;
import simulation.Deplacement;

import java.io.Serializable;

public interface Comportement extends Serializable {
    public Deplacement prendreDecision();

    public Comportements getType();
}
