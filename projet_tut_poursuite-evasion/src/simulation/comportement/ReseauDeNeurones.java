package simulation.comportement;

import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.basicmodelzoo.basic.Mlp;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Activation;
import ai.djl.nn.Block;
import ai.djl.nn.Blocks;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.convolutional.Conv2d;
import ai.djl.nn.core.Linear;
import ai.djl.nn.norm.Dropout;
import ai.djl.nn.pooling.Pool;
import ai.djl.translate.Batchifier;
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
    Model model;
    static SequentialBlock block;
    Personnage personnage;
    private Translator<NDArray, Integer> translator;

    /**
     * Constricteur reseau de neurone mlp
     *
     * @param nomReseau  nom du reseau
     * @param simulation simulation dans laquel le reseau agira
     */
    public ReseauDeNeurones(String nomReseau, Simulation simulation, Personnage personnage) {
        this.sim = simulation;
        this.personnage = personnage;
        if (block == null) {
            System.out.println("le bloc est null");
            setBlock();
        }

        Path modelDir = Paths.get("donnees/reseau/mlp");
        model = Model.newInstance(nomReseau);
        model.setBlock(new Mlp(Simulation.getTailleCarte() * 3, Deplacement.values().length, new int[]{350, 300, 256, 200, 128, 100, 64, 50, 32, 20}));
        try {
            model.load(modelDir);
            System.out.println("Chargement ok !");
        } catch (MalformedModelException | IOException ex) {
            System.out.println(ex.getMessage());
        }

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

                return (int) probabilities.argMax().getLong();
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
        double[] cartePos = Outil.applatissement(personnage.getPositionCarte());
        double[] carteBayesienne = Outil.applatissement(sim.getCarteBayesienne(personnage));
        double[] carte = Outil.applatissement(sim.getCarteMursSortie());
        float[] input = Outil.doubleToFloat(Outil.concatener_tab(carte, Outil.concatener_tab(carteBayesienne, cartePos)));

        NDArray arrayFlaot = manager.create(input);
        Integer resultat = 0;
        try {
            resultat = predictor.predict(arrayFlaot);
        } catch (ai.djl.translate.TranslateException te) {
            System.out.println(te.getMessage());
            return Deplacement.AUCUN;
        }
        return Deplacement.values()[resultat];
    }

    @Override
    public Comportements getType() {
        return Comportements.ReseauArbreAleatoire;
    }

    public static void setBlock() {
        block = new Mlp(Simulation.getTailleCarte() * 3, Deplacement.values().length, new int[]{350, 300, 256, 200, 128, 100, 64, 50, 32, 20});
    }

    public static SequentialBlock getBlock() {
        return block;
    }
}
