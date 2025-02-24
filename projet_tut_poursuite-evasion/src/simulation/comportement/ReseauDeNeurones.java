package simulation.comportement;

import ai.djl.MalformedModelException;
import ai.djl.Model;
import simulation.Deplacement;
import simulation.Simulation;
import simulation.personnages.Personnage;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReseauDeNeurones implements Comportement {
    Simulation sim;
    Personnage personnage;
    Model model;

    public ReseauDeNeurones(String nomReseau, Simulation simulation, Personnage personnage) throws MalformedModelException, IOException {
        this.sim = simulation;
        this.personnage = personnage;

        Path modelDir = Paths.get("donnes/mlp/" + nomReseau);
        model = Model.newInstance("mlp");
        model.load(modelDir);
    }

    @Override
    public Deplacement prendreDecision() {
        return null;
    }
}
