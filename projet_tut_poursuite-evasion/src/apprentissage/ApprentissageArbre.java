package apprentissage;


import ai.djl.Application;
import ai.djl.Model;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.*;
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
import ai.djl.translate.TranslateException;
import outils.CSVDataset;
import simulation.Simulation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class ApprentissageArbre {
    public static void main(String[] args) throws IOException, TranslateException {
        //Methode pour recalculer le dataset
        //LancerCalculs.init();

        Application application = Application.Tabular.SOFTMAX_REGRESSION;
        long inputSize = Simulation.getTailleCarte() + 2;
        long outputSize = 9;

        SequentialBlock block = new SequentialBlock();

        // Entrée 1 : Carte combinée (10, 10, 2)
        Block input1 = new SequentialBlock()
                .add(Conv2d.builder().setKernelShape(new Shape(3, 3)).setFilters(32).build())
                .add(Activation.reluBlock())
                .add(Pool.maxPool2dBlock(new Shape(2, 2)))
                .add(Conv2d.builder().setKernelShape(new Shape(3, 3)).setFilters(64).build())
                .add(Activation.reluBlock())
                .add(Pool.maxPool2dBlock(new Shape(2, 2)))
                .add(Blocks.batchFlattenBlock());

        // Entrée 2 : Position du gardien (2,)
        Block input2 = new SequentialBlock();

        // Combiner les entrées
        ParallelBlock combined = new ParallelBlock(
                inputs -> {
                    NDArray mapFeatures = (NDArray) inputs.get(0);
                    NDArray guardPos = (NDArray) inputs.get(1);
                    return new NDList(mapFeatures.concat(guardPos, 1)); // Concaténer le long de la dimension 1
                },
                Arrays.asList(input1, input2)
        );

        // Couches fully connected
        combined.add(Linear.builder().setUnits(128).build());
        combined.add(Activation.reluBlock());
        combined.add(Linear.builder().setUnits(64).build());
        combined.add(Activation.reluBlock());
        combined.add(Linear.builder().setUnits(outputSize).build());

        // Créer le modèle
        Model model = Model.newInstance("reseau_CNN");
        model.setBlock(combined);

        //parametrage nb couches / neurones (sans CNN)
        // SequentialBlock block = new SequentialBlock();
        // block.add(Blocks.batchFlattenBlock(inputSize));
        // block.add(Linear.builder().setUnits(269).build());
        // block.add(Activation::relu);
        // block.add(Linear.builder().setUnits(269).build());
        // block.add(Activation::relu);
        // block.add(Linear.builder().setUnits(269).build());
        // block.add(Activation::relu);
        // block.add(Linear.builder().setUnits(269).build());
        // block.add(Activation::relu);
        // block.add(Linear.builder().setUnits(outputSize).build());
        //creation du modele du réseau a sauvegarder
        //Model model = Model.newInstance("reseau_Arbre");
        //model.setBlock(block);

        //parametrage de l'entrainement
        TrainingConfig config = new DefaultTrainingConfig(Loss.softmaxCrossEntropyLoss())
                .addEvaluator(new Accuracy()) // Fonction qui permet de comprendre comment performe le réseau a l'entrainement
                .addTrainingListeners(TrainingListener.Defaults.logging())
                //permet d'afficher les infos de chaque epoch
                .addTrainingListeners(new TrainingListener() {
                    @Override
                    public void onEpoch(Trainer trainer) {
                        System.out.println(trainer.getTrainingResult());
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
                        trainer.getTrainingResult();
                    }
                }); //affiche les info d'entrainement

        Trainer trainer = model.newTrainer(config);

        int epoch = 10;

        CSVDataset csvDataset = new CSVDataset.Builder().setSampling(32, true).build("donnees/game_data.csv");
        CSVDataset csvDatasetValidate = new CSVDataset.Builder().setSampling(32, true).build("donnees/game_data_validation.csv");

        System.out.println(csvDataset.size());
        System.out.println(csvDatasetValidate.size());

        EasyTrain.fit(trainer, epoch, csvDataset, csvDatasetValidate);

        //enregistrement du model
        Path modelDir = Paths.get("donnees/mlp");
        Files.createDirectories(modelDir);

        model.setProperty("Epoch", String.valueOf(epoch));

        model.save(modelDir, "CNN");
    }
}
