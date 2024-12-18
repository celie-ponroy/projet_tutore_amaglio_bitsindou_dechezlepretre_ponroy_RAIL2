package simulation.comportement;

import simulation.Deplacement;

public interface Comportement {
    public Deplacement prendreDecision();
    public Deplacement prendreDecision(double[] entrees);
}
