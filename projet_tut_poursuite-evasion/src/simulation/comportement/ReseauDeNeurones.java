package simulation.comportement;

import simulation.Deplacement;

public class ReseauDeNeurones implements Comportement{


    @Override
    public Deplacement prendreDecision() {
        return Deplacement.AUCUN;
    }
}
