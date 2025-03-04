package simulation.comportement;

import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDArrays;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Activation;
import ai.djl.nn.Block;
import ai.djl.nn.Blocks;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.convolutional.Conv2d;
import ai.djl.nn.core.Linear;
import ai.djl.nn.pooling.Pool;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
import ai.djl.training.TrainingConfig;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import ai.djl.translate.Batchifier;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import outils.CSVDataset;
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

    public ReseauDeNeurones(String nomReseau, Simulation simulation, Personnage personnage) throws TranslateException, IOException {

        SequentialBlock block = new SequentialBlock();

        // Entrée 1 : Carte combinée (10, 10, 2)
        Block CNN = new SequentialBlock()
                .add(Conv2d.builder().setKernelShape(new Shape(3, 3)).setFilters(32).build())
                .add(Activation.reluBlock())
                .add(Pool.maxPool2dBlock(new Shape(2, 2)))
                .add(Blocks.batchFlattenBlock()); // Aplatir les caractéristiques

        // Couches fully connected
        block.add(CNN);
        block.add(Linear.builder().setUnits(128).build());
        block.add(Activation.reluBlock());
        block.add(Linear.builder().setUnits(64).build());
        block.add(Activation.reluBlock());
        block.add(Linear.builder().setUnits(32).build());
        block.add(Activation.reluBlock());
        block.add(Linear.builder().setUnits(Deplacement.values().length).build());

        // Créer le modèle
        Model model = Model.newInstance(nomReseau);
        model.setBlock(block);
        try {
            model.load(Paths.get("donnees/mlp"));
            System.out.println("Réseau chargé");
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }


        this.sim = simulation;
        this.personnage = personnage;
        this.model = model;

        this.translator = new Translator<NDArray, Integer>() {
            @Override
            public NDList processInput(TranslatorContext ctx, NDArray input) {
                // L'entrée est déjà un NDArray, on le met simplement dans un NDList
                return new NDList(input);
            }

            @Override
            public Integer processOutput(TranslatorContext ctx, NDList list) {
                // Trouver l'index de la probabilité la plus haute
                NDArray softmax = list.singletonOrThrow().softmax(0);
                NDArray probabilities = softmax.argMax();
                 return (int) probabilities.getLong();
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

        //Creation de l'entré du RN
        // Conversion de la colonne "map" en un tableau de float
        float[] mapValues = Outil.conversionDoubleFloat(Outil.applatissement(sim.getCarteBayesienne(sim.getGardien())));
        // Conversion de la colonne "pos" en un tableau de float
        float[] posValues = new float[2];
        posValues[0] = sim.getGardien().getPosition().getX();
        posValues[1] = sim.getGardien().getPosition().getY();
        float[] posMapValue = new float[Simulation.getTailleCarte()];
        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE[0].length; j++) {
                if (i == posValues[0] && j == posValues[1]) {
                    posMapValue[(j * Simulation.CARTE[0].length) + i] = 1;
                } else {
                    posMapValue[(Simulation.CARTE[0].length * j) + i] = 0;
                }
            }
        }
        StringBuilder d = new StringBuilder();
        for (int i=0; i<sim.getCarteBayesienne(sim.getGardien()).length; i++) {
            for (int j = 0; j < sim.getCarteBayesienne(sim.getGardien())[0].length; j++) {
                d.append(mapValues[i]).append(",");
            }
            d.append("\n");
        }
        System.out.println("entrée bayesienne : ");
        System.out.println(d);


        float[] carteReel = Outil.conversionDoubleFloat(Outil.applatissement(sim.getCarteMursSortie()));
        d = new StringBuilder();
        for (int i=0; i<Simulation.CARTE.length ; i++) {
            for (int j = 0; j < Simulation.CARTE[0].length; j++) {
                d.append(carteReel[i]);
            }
            d.append("\n");
        }
        System.out.println("carte reel : ");
        NDArray posR = manager.create(posMapValue).reshape(1, Simulation.CARTE.length, Simulation.CARTE[0].length);

        // Crée les cartes avec la dimension des canaux
        NDArray bayesien = manager.create(mapValues).reshape(1, Simulation.CARTE.length, Simulation.CARTE[0].length); // (1, H, W)
        NDArray realMap = manager.create(carteReel).reshape(1, Simulation.CARTE.length, Simulation.CARTE[0].length); // (1, H, W)
        // Concatène sur l'axe des canaux (0 -> channel)
        NDArray inputData = NDArrays.concat(new NDList(bayesien, realMap, posR)); // Résultat : (2, H, W)

        Integer resultat = 0;
        try {
            resultat = predictor.predict(inputData);
            System.out.println("resultats : " + resultat);
        } catch (Exception e ) {
            System.out.println("erreur");
            System.out.println(e.getMessage());
            return Deplacement.AUCUN;
        }
        return Deplacement.values()[resultat];
    }

    @Override
    public Comportements getType() {
        return Comportements.ReseauArbreAleatoire;
    }
}
