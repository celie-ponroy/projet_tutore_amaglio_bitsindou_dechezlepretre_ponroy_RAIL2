package simulation.comportement;

import ai.djl.MalformedModelException;
import ai.djl.Model;

import ai.djl.basicmodelzoo.basic.Mlp;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
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
    Personnage personnage;
    Model model;
    private Translator<NDArray, Integer> translator;

    public ReseauDeNeurones(String nomReseau, Simulation simulation, Personnage personnage)  {
        this.sim = simulation;
        this.personnage = personnage;

        Path modelDir = Paths.get("donnees/mlp");
        model = Model.newInstance("mlp_"+200+"_"+100);
        model.setBlock(new Mlp(Simulation.getTailleCarte()+2, Deplacement.values().length, new int[] {200, 100}));
        try {
            model.load(modelDir);
        }catch (MalformedModelException | IOException ex) {}


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
                System.out.println(probabilities);
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
        double[] tableau = new double[Simulation.getTailleCarte()+2];
        double[] tableau2 = Outil.applatissement(sim.getCarteBayesienne(sim.getGardien()));
        for (int i = 0; i < tableau2.length; i++) {
            tableau[i] = tableau2[i];
        }
        tableau[tableau.length-2] = (double) personnage.getPosition().getY() /Simulation.CARTE.length;
        tableau[tableau.length-1] = (double) personnage.getPosition().getX() /Simulation.CARTE[0].length;

        NDArray array = manager.create(tableau);
        float[] popolola = new float[Simulation.getTailleCarte()+2];
        for (int i = 0; i < popolola.length; i++) {
            popolola[i] = Float.parseFloat(String.valueOf(tableau[i]));
        }
        NDArray arrayFlaot = manager.create(popolola);
        Integer resultat = 0;
        try {
            resultat = predictor.predict(arrayFlaot);
        } catch (ai.djl.translate.TranslateException te){
            return Deplacement.AUCUN;
        }
        return Deplacement.values()[resultat];
    }

    @Override
    public Comportements getType() {
        return Comportements.ReseauArbreAleatoire;
    }
}
