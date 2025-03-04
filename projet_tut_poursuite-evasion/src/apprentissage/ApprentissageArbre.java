package apprentissage;


import ai.djl.Application;
import ai.djl.Model;
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
import ai.djl.training.loss.SoftmaxCrossEntropyLoss;
import ai.djl.translate.TranslateException;
import outils.CSVDataset;
import simulation.Deplacement;
import simulation.Simulation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ApprentissageArbre {
    public static void main(String[] args) throws IOException, TranslateException {
        //Methode pour recalculer le dataset
        //LancerCalculs.init();

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
        Model model = Model.newInstance("reseau_CNN");
        model.setBlock(block);

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

        int epoch = 20;

        CSVDataset csvDataset = new CSVDataset.Builder().setSampling(32, true).build("donnees/game_data.csv");
        CSVDataset csvDatasetValidate = new CSVDataset.Builder().setSampling(32, true).build("donnees/game_data_validation.csv");

        //affichage d'un record
        //System.out.println("csv size : " + csvDataset.size());
        //System.out.println("csv validate size : " + csvDatasetValidate.size());
        //System.out.println(csvDataset.get(NDManager.newBaseManager(), 0).getData().get(0));

        EasyTrain.fit(trainer, epoch, csvDataset, csvDatasetValidate);

        //enregistrement du model
        Path modelDir = Paths.get("donnees/mlp");
        Files.createDirectories(modelDir);

        model.setProperty("Epoch", String.valueOf(epoch));

        model.save(modelDir, "CNN");
    }
}
