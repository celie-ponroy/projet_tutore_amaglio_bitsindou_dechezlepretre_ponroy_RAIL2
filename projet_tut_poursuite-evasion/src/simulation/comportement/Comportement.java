package simulation.comportement;

import ai.djl.translate.TranslateException;
import simulation.Deplacement;

public interface Comportement {
    public Deplacement prendreDecision() throws TranslateException;
}
