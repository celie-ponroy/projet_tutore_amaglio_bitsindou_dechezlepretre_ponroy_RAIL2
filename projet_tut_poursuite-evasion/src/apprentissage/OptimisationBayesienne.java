package apprentissage;

import ai.djl.Model;
import ai.djl.nn.Activation;
import ai.djl.nn.Blocks;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.core.Linear;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
import ai.djl.training.TrainingConfig;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import ai.djl.translate.TranslateException;
import outils.CSVDatasetCNN;
import simulation.Simulation;

import java.io.*;
import java.util.*;

//Class qui permet de tester différentes valeurs pour les hyperparametrès du réseau de neurones
public class OptimisationBayesienne {

    private static final int MIN_NEURONS = 50, MAX_NEURONS = 500;
    private static final int MIN_LAYERS = 2, MAX_LAYERS = 6;
    private static final int INITIAL_SAMPLES = 10;  // Nombre de points initiaux
    private static final int ITERATIONS = 50;  // Nombre total d'optimisations
    static float lossMini = 100;
    static float BETA = 10;

    private static List<int[]> testedConfigs = new ArrayList<>();
    private static List<Double> lossResults = new ArrayList<>();
    private static Random random = new Random();

    public static void main(String[] args) throws IOException, TranslateException {
        // Phase initiale : collecter des données aléatoires
        for (int i = 0; i < INITIAL_SAMPLES; i++) {
            int[] config = getRandomConfig();
            double loss = evaluate(config);
            testedConfigs.add(config);
            lossResults.add(loss);
            lossMini = 100;
        }

        // Phase d'optimisation bayésienne
        for (int i = INITIAL_SAMPLES; i < ITERATIONS; i++) {
            int[] nextConfig = suggestNextConfiguration();
            double loss = evaluate(nextConfig);
            testedConfigs.add(nextConfig);
            lossResults.add(loss);
        }

        // Résultat final
        try {
            FileWriter fw = new FileWriter("meuilleur R2SEAU.txt");
            for (int i = 0; i < testedConfigs.size(); i++) {
                fw.write("neurones et couches : " + testedConfigs.get(i)[0] + " " + testedConfigs.get(i)[1] + " loss : " + lossResults.get(i) + "\n");
            }
            fw.close();
            System.out.println("Le texte a été écrit avec succès");
        } catch (IOException e) {
            e.printStackTrace();
        }

        int bestIndex = lossResults.indexOf(Collections.min(lossResults));
        System.out.println("Meilleure configuration : " + Arrays.toString(testedConfigs.get(bestIndex)));
        System.out.println("Meilleure loss : " + lossResults.get(bestIndex));
    }

    // Génère une configuration aléatoire pour l'initialisation
    private static int[] getRandomConfig() {
        return new int[]{
                random.nextInt(MAX_NEURONS - MIN_NEURONS) + MIN_NEURONS,
                random.nextInt(MAX_LAYERS - MIN_LAYERS) + MIN_LAYERS
        };
    }

    // Fonction d'acquisition : LCB lower confidente bound
    private static int[] suggestNextConfiguration() {
        int[] bestConfig = null;
        double bestLCB = Double.MAX_VALUE;
        Set<String> triedConfigs = new HashSet<>(); // Pour éviter les doublons

        // Tester des valeurs légèrement différentes pour éviter de répéter les mêmes configurations
        for (int i = 0; i < 15; i++) {  // Tester 15 nouvelles configurations aléatoires proches
            int[] candidate = getRandomNearConfig(); // Génère une config aléatoire proche

            // Vérifier qu'on ne l'a pas déjà testée
            String key = Arrays.toString(candidate);
            if (triedConfigs.contains(key)) continue;
            triedConfigs.add(key);

            double mu = predictMean(candidate);
            double sigma = predictVariance(candidate);
            double LCB = mu - BETA * sigma;  // LCB = exploitation - exploration

            if (LCB < bestLCB) {
                bestLCB = LCB;
                bestConfig = candidate;
            }
        }

        // Si on répète trop souvent la même valeur, on force une exploration aléatoire
        if (Collections.frequency(testedConfigs, bestConfig) > 3) {
            bestConfig = getRandomConfig();
        }

        return bestConfig;
    }

    // Génère une config aléatoire proche d'une config existante
    private static int[] getRandomNearConfig() {
        int neurons = MIN_NEURONS + random.nextInt(MAX_NEURONS - MIN_NEURONS);
        int layers = MIN_LAYERS + random.nextInt(MAX_LAYERS - MIN_LAYERS);

        // Petite modification pour éviter d'avoir toujours la même valeur
        neurons += random.nextInt(21) - 10;  // ±10 neurones
        layers += random.nextInt(3) - 1;    // ±1 couche
        neurons = Math.max(MIN_NEURONS, Math.min(MAX_NEURONS, neurons));
        layers = Math.max(MIN_LAYERS, Math.min(MAX_LAYERS, layers));

        return new int[]{neurons, layers};
    }


    private static double predictMean(int[] config) {
        double weightedSum = 0.0;
        double totalWeight = 0.0;
        double defaultValue = 1.5;  // Valeur neutre si aucune donnée proche (ex: perte moyenne constatée)

        for (int i = 0; i < testedConfigs.size(); i++) {
            int[] pastConfig = testedConfigs.get(i);
            double pastLoss = lossResults.get(i);

            double distance = euclideanDistance(config, pastConfig);
            double weight = Math.exp(-distance); // Pondération exponentielle

            weightedSum += weight * pastLoss;
            totalWeight += weight;
        }

        // Si aucune donnée proche, on retourne une valeur par défaut
        return (totalWeight > 0) ? (weightedSum / totalWeight) : defaultValue;
    }

    private static double predictVariance(int[] config) {
        double mean = predictMean(config);
        double weightedSum = 0.0;
        double totalWeight = 0.0;
        double minVariance = 0.1; // Empêche d'avoir une variance nulle

        for (int i = 0; i < testedConfigs.size(); i++) {
            int[] pastConfig = testedConfigs.get(i);
            double pastLoss = lossResults.get(i);

            double distance = euclideanDistance(config, pastConfig);
            double weight = Math.exp(-distance);

            weightedSum += weight * Math.pow(pastLoss - mean, 2);
            totalWeight += weight;
        }

        return (totalWeight > 0) ? Math.sqrt(weightedSum / totalWeight) : minVariance;
    }

    // Fonction pour calculer la distance euclidienne entre deux configurations
    private static double euclideanDistance(int[] config1, int[] config2) {
        double sum = 0.0;
        for (int i = 0; i < config1.length; i++) {
            sum += Math.pow(config1[i] - config2[i], 2);
        }
        return Math.sqrt(sum);
    }

    // Simulation d'évaluation d'un modèle avec une configuration donnée
    private static double evaluate(int[] config) throws IOException, TranslateException {
        int neurons = config[0], layers = config[1];

        long inputSize = Simulation.getTailleCarte() + 2;
        long outputSize = 9;

        //parametrage nb couches / neurones
        SequentialBlock block = new SequentialBlock();
        block.add(Blocks.batchFlattenBlock(inputSize));
        for (int i = 0; i < layers; i++) {
            block.add(Linear.builder().setUnits(neurons).build());
            block.add(Activation::relu);
        }
        block.add(Linear.builder().setUnits(outputSize).build());


        //creation du modele du réseau a sauvegarder
        Model model = Model.newInstance("reseau_Arbre");
        model.setBlock(block);
        //parametrage de l'entrainement
        TrainingConfig trainingConfig = new DefaultTrainingConfig(Loss.softmaxCrossEntropyLoss())
                .addEvaluator(new Accuracy()) // Fonction qui permet de comprendre comment performe le réseau a l'entrainement
                .addTrainingListeners(TrainingListener.Defaults.logging())
                //permet d'afficher les infos de chaque epoch
                .addTrainingListeners(new TrainingListener() {
                    @Override
                    public void onEpoch(Trainer trainer) {
                        if (trainer.getTrainingResult().getTrainLoss() < lossMini) {
                            lossMini = trainer.getTrainingResult().getTrainLoss();
                        }
                    }

                    @Override
                    public void onTrainingBatch(Trainer trainer, BatchData batchData) {
                    }

                    @Override
                    public void onValidationBatch(Trainer trainer, BatchData batchData) {
                    }

                    @Override
                    public void onTrainingBegin(Trainer trainer) {
                    }

                    @Override
                    public void onTrainingEnd(Trainer trainer) {
                    }
                });

        Trainer trainer = model.newTrainer(trainingConfig);

        int epoch = 50;

        CSVDatasetCNN csvDataset = new CSVDatasetCNN.Builder().setSampling(32, false).build("donnees/game_data.csv");
        CSVDatasetCNN csvDatasetValidate = new CSVDatasetCNN.Builder().setSampling(32, false).build("donnees/game_data_validation.csv");

        EasyTrain.fit(trainer, epoch, csvDataset, csvDatasetValidate);

        return lossMini;
    }
}
