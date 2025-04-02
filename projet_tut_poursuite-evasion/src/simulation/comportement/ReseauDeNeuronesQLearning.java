package simulation.comportement;

import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.basicmodelzoo.basic.Mlp;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.nn.Activation;
import ai.djl.nn.Blocks;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.core.Linear;
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

public class ReseauDeNeuronesQLearning implements Comportement {
    Simulation sim;
    Personnage personnage;
    Model model;
    private Translator<NDArray, Integer> translatorDeplacement;
    private Translator<NDArray, Float> translatorSortie;

    public ReseauDeNeuronesQLearning(String nomReseau, Simulation simulation, Personnage personnage)  {
        this.sim = simulation;
        this.personnage = personnage;

        Path modelDir = Paths.get("donnees/mlp");
        model = Model.newInstance(nomReseau);
        long inputSize = Simulation.getTailleCarte()*2L;
        int outputSize = Deplacement.values().length;
        SequentialBlock block = new SequentialBlock();
        block.add(Blocks.batchFlattenBlock(inputSize));
        block.add(Linear.builder().setUnits(350).build());
        block.add(Activation::relu);
        block.add(Linear.builder().setUnits(250).build());
        block.add(Activation::relu);
        block.add(Linear.builder().setUnits(200).build());
        block.add(Activation::relu);
        block.add(Linear.builder().setUnits(150).build());
        block.add(Activation::relu);
        block.add(Linear.builder().setUnits(50).build());
        block.add(Activation::tanh);
        block.add(Linear.builder().setUnits(outputSize).build());
        model.setBlock(block);

        try {
            model.load(modelDir);
            System.out.println("Chargement ok !");
        }catch (MalformedModelException | IOException ex) {
            System.out.println(ex.getMessage());
        }
        setTranslatorSortie();
    }
    public ReseauDeNeuronesQLearning(Model model){
        this.model = model;
        setTranslatorDeplacement();
        setTranslatorSortie();
    }

    @Override
    public Deplacement prendreDecision() {
        setTranslatorDeplacement();
        var predictor = model.newPredictor(translatorDeplacement);
        NDManager manager = NDManager.newBaseManager();
        double[] cartePos = Outil.applatissement(sim.getPrisonnier().getPositionCarte());
        double[] carte = Outil.applatissement(sim.getCarteMursSortie());
        float[] input = Outil.doubleToFloat(Outil.concatener_tab(carte, cartePos));

        NDArray arrayFlaot = manager.create(input);
        Integer resultat = 0;
        try {
            resultat = predictor.predict(arrayFlaot);
        } catch (ai.djl.translate.TranslateException te){
            System.out.println(te.getMessage());
            return Deplacement.AUCUN;
        }
        return Deplacement.values()[resultat];
    }

    @Override
    public Comportements getType() {
        return Comportements.ReseauArbreAleatoire;
    }

    public void setTranslatorDeplacement(){
        this.translatorDeplacement = new Translator<NDArray, Integer>() {
            @Override
            public NDList processInput(TranslatorContext ctx, NDArray input) {
                // L'entrée est déjà un NDArray, on le met simplement dans un NDList
                return new NDList(input);
            }

            @Override
            public Integer processOutput(TranslatorContext ctx, NDList list) {
                // Trouver l'index de la probabilité la plus haute
                NDArray probabilities = list.singletonOrThrow();
                System.out.println(probabilities);
                System.out.println(probabilities.argMax().getLong());
                return (int) probabilities.argMax().getLong();
            }

            @Override
            public Batchifier getBatchifier() {
                return Batchifier.STACK;
            }
        };
    }
    public void setTranslatorSortie(){
        this.translatorSortie = new Translator<NDArray, Float>() {
            @Override
            public NDList processInput(TranslatorContext ctx, NDArray input) {
                // L'entrée est déjà un NDArray, on le met simplement dans un NDList
                return new NDList(input);
            }

            @Override
            public Float processOutput(TranslatorContext ctx, NDList list) {
                // Trouver l'index de la probabilité la plus haute
                NDArray probabilities = list.singletonOrThrow();

                return probabilities.getFloat((int)probabilities.argMax().getLong());
            }

            @Override
            public Batchifier getBatchifier() {
                return Batchifier.STACK;
            }
        };
    }

    public float donnerSortie(){
        var predictor = model.newPredictor(translatorSortie);
        NDManager manager = NDManager.newBaseManager();
        double[] cartePos = Outil.applatissement(sim.getPrisonnier().getPositionCarte());
        double[] carte = Outil.applatissement(sim.getCarteMursSortie());
        float[] input = Outil.doubleToFloat(Outil.concatener_tab(carte, cartePos));

        NDArray arrayFlaot = manager.create(input);
        Float resultat = 0f;
        try {
            resultat = predictor.predict(arrayFlaot);
        } catch (ai.djl.translate.TranslateException te){
            System.out.println(te.getMessage());
            return 0.f;
        }
        //System.out.println(Deplacement.values()[resultat]);
        return resultat;
    }

    public void setSim(Simulation sim) {
        this.sim = sim;
    }

    public void setPersonnage(Personnage personnage) {
        this.personnage = personnage;
    }
}
