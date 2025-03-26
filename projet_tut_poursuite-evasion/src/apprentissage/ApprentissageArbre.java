package apprentissage;


import ai.djl.Model;
import ai.djl.ndarray.NDManager;
import ai.djl.nn.SequentialBlock;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
import ai.djl.training.TrainingConfig;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import ai.djl.translate.TranslateException;
import outils.CSVDataset;
import simulation.comportement.ReseauDeNeuronesCNN;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ApprentissageArbre {
    public static void main(String[] args) throws IOException, TranslateException {
        //Methode pour recalculer le dataset
        //LancerCalculs.init();

        String nonReseau = "reseau_" + args[0];

        SequentialBlock block = new SequentialBlock();
        switch (args[0]) {
            case "CNN":
                ReseauDeNeuronesCNN.setBlock();
                block = ReseauDeNeuronesCNN.getBlock();
                break;
            case "MLP":
                ReseauDeNeuronesCNN.setBlock();
                block = ReseauDeNeuronesCNN.getBlock();
                break;
        }

        // Créer le modèle
        Model model = Model.newInstance(nonReseau);
        model.setBlock(block);

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

        int epoch = 3;

        CSVDataset csvDataset = new CSVDataset.Builder().setSampling(32, true).build("donnees/game_data.csv");
        CSVDataset csvDatasetValidate = new CSVDataset.Builder().setSampling(32, true).build("donnees/game_data_validation.csv");

        //entrainement du réseau
        EasyTrain.fit(trainer, epoch, csvDataset, csvDatasetValidate);

        //enregistrement du model
        Path modelDir = Paths.get("donnees/mlp");
        Files.createDirectories(modelDir);

        model.setProperty("Epoch", String.valueOf(epoch));

        model.save(modelDir, args[0]);
    }

    public void afficherRecord(CSVDataset csvDataset, CSVDataset csvDatasetValidate) {
        System.out.println("csv size : " + csvDataset.size());
        System.out.println("csv validate size : " + csvDatasetValidate.size());
        System.out.println(csvDataset.get(NDManager.newBaseManager(), 0).getData().get(0));
    }
}
