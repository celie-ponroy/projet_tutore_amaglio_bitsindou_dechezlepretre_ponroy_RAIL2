package apprentissage;

import ai.djl.Model;
import ai.djl.ndarray.types.Shape;
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
import lancercalculs.LancerCalculsDataSetQLearning;
import outils.CSVDataset;
import outils.CSVDatasetCNN;
import simulation.Simulation;
import simulation.comportement.ReseauDeNeuronesQLearning;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ApprentissageQLearning {
    public static void main(String[] args) throws IOException, TranslateException {

        long inputSize = Simulation.getTailleCarte() * 2L;
        long outputSize = 9;

        //parametrage nb couches / neurones
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

        //creation du modele du réseau a sauvegarder
        Model model = Model.newInstance("reseau_Qlearning");
        model.setBlock(block);

        //parametrage de l'entrainement
        TrainingConfig config = new DefaultTrainingConfig(Loss.l2Loss())
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

        int epoch = 25;
        int batchSize = 32;

        Trainer trainer = model.newTrainer(config);
        trainer.initialize(new Shape(batchSize, inputSize));

        ReseauDeNeuronesQLearning rn = new ReseauDeNeuronesQLearning(model);

        //boucle de renforcement
        for (int i = 0; i < 200; i++) {
            //creation dataset
            LancerCalculsDataSetQLearning.launch(rn);
            CSVDatasetCNN csvDataset = new CSVDatasetCNN.Builder().setSampling(batchSize, true).build("donnees/game_data_Qlearning.csv");

            //entrainement
            EasyTrain.fit(trainer, epoch, csvDataset, null);
            rn = new ReseauDeNeuronesQLearning(model);
        }

        //enregistrement du model
        Path modelDir = Paths.get("donnees/mlp");
        Files.createDirectories(modelDir);

        model.setProperty("Epoch", String.valueOf(epoch));

        model.save(modelDir, "mlp_q");
    }
}
