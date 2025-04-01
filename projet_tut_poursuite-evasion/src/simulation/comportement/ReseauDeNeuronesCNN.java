package simulation.comportement;

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
import ai.djl.nn.norm.Dropout;
import ai.djl.nn.pooling.Pool;
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
import java.nio.file.Paths;

public class ReseauDeNeuronesCNN implements Comportement {
    Simulation sim;
    static SequentialBlock block;
    Personnage personnage;
    Model model;
    private Translator<NDArray, Integer> translator;

    /**
     * Constructeur reseau de neurones CNN
     *
     * @param nomReseau  nom du reseau
     * @param simulation simulation dans laquel le reseau agira
     * @param personnage Personange auquel est attaché le réseau
     */
    public ReseauDeNeuronesCNN(String nomReseau, Simulation simulation, Personnage personnage) {
        if (block == null) {
            System.out.println("le bloc est null");
            setBlock();
        }

        // Créer le modèle
        Model model = Model.newInstance(nomReseau);
        model.setBlock(block);
        try {
            model.load(Paths.get("donnees/reseau/cnn"));
            System.out.println("Réseau chargé");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        this.sim = simulation;
        this.model = model;
        this.personnage = personnage;

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

    /**
     * Methode qui permet de prendre une decision a partir d'input donnés
     *
     * @return
     */
    @Override
    public Deplacement prendreDecision() {
        var predictor = model.newPredictor(translator);
        NDManager manager = NDManager.newBaseManager();

        //Creation de l'entré du RN
        // Conversion de la colonne "map" en un tableau de float
        float[] mapValues = Outil.conversionDoubleFloat(Outil.applatissement(sim.getCarteBayesienne(personnage)));
        // Conversion de la colonne "pos" en un tableau de float
        float[] posValues = new float[2];
        posValues[0] = personnage.getPosition().getX();
        posValues[1] = personnage.getPosition().getY();
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
        for (int i = 0; i < sim.getCarteBayesienne(personnage).length; i++) {
            for (int j = 0; j < sim.getCarteBayesienne(personnage)[0].length; j++) {
                d.append(mapValues[i]).append(",");
            }
            d.append("\n");
        }

        float[] carteReel = Outil.conversionDoubleFloat(Outil.applatissement(sim.getCarteMursSortie()));
        d = new StringBuilder();
        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE[0].length; j++) {
                d.append(carteReel[i]);
            }
            d.append("\n");
        }

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
        } catch (Exception e) {
            System.out.println("erreur");
            System.out.println(e.getMessage());
            return Deplacement.AUCUN;
        }
        return Deplacement.values()[resultat];
    }

    @Override
    public Comportements getType() {
        return Comportements.ReseauArbreCNN;
    }

    /**
     * Creation du squellette du RN
     */
    public static void setBlock() {
        block = new SequentialBlock();
        // Entrée 1 : Carte combinée (10, 10, 2)
        Block CNN = new SequentialBlock()
                .add(Conv2d.builder().setKernelShape(new Shape(3, 3)).setFilters(32).build())
                .add(Activation.leakyReluBlock(0.1f)) // Moins de neurones morts
                .add(Pool.maxPool2dBlock(new Shape(2, 2), new Shape(1, 1))) // Stride 1 pour moins réduire
                .add(Conv2d.builder().setKernelShape(new Shape(3, 3)).setFilters(64).build()) // Plus de filtres
                .add(Activation.leakyReluBlock(0.1f))
                .add(Pool.maxPool2dBlock(new Shape(2, 2))) // Normal
                .add(Conv2d.builder().setKernelShape(new Shape(3, 3)).setFilters(128).build()) // Encore plus de filtres
                .add(Activation.leakyReluBlock(0.1f))
                .add(Pool.maxPool2dBlock(new Shape(2, 2)))
                .add(Blocks.batchFlattenBlock());

        block.add(CNN);
        block.add(Linear.builder().setUnits(256).build());
        block.add(Activation.reluBlock());
        block.add(Dropout.builder().optRate(0.3f).build());

        block.add(Linear.builder().setUnits(128).build());
        block.add(Activation.reluBlock());
        block.add(Dropout.builder().optRate(0.3f).build());

        block.add(Linear.builder().setUnits(64).build());
        block.add(Activation.reluBlock());
        block.add(Dropout.builder().optRate(0.3f).build());
        block.add(Linear.builder().setUnits(Deplacement.values().length).build());
    }

    public static SequentialBlock getBlock() {
        return block;
    }
}
