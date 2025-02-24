package simulation.comportement;

import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.translate.Batchifier;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import outils.Outil;
import simulation.Comportements;
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
    private Translator<NDArray, Integer> translator;

    public ReseauDeNeurones(String nomReseau, Simulation simulation, Personnage personnage) throws MalformedModelException, IOException {
        this.sim = simulation;
        this.personnage = personnage;

        Path modelDir = Paths.get("donnes/mlp/" + nomReseau);
        model = Model.newInstance("mlp");
        model.load(modelDir);

        this.translator = new Translator<NDArray, Integer>() {
            @Override
            public NDList processInput(TranslatorContext ctx, NDArray input) {
                // L'entrée est déjà un NDArray, on le met simplement dans un NDList
                return new NDList(input);
            }

            @Override
            public Integer processOutput(TranslatorContext ctx, NDList list) {
                // Trouver l'index de la probabilité la plus haute
                NDArray probabilities = list.singletonOrThrow().softmax(0);
                return probabilities.argMax().getInt();
            }

            @Override
            public Batchifier getBatchifier() {
                return Batchifier.STACK;
            }
        };
    }

    @Override
    public Deplacement prendreDecision() {
        var predictor = model.newPredictor(translator);
        NDManager manager = NDManager.newBaseManager();
        NDArray array = manager.create(Outil.applatissement(sim.getCarteBayesienne(sim.getGardien())));
        Integer resultat = 0;
        try {
            resultat = predictor.predict(array);
        } catch (TranslateException te){
            System.out.println(te.getMessage());
            return Deplacement.AUCUN;
        }
        return Deplacement.values()[resultat];
    }

    @Override
    public Comportements getType() {
        return Comportements.ReseauArbreAleatoire;
    }
}
