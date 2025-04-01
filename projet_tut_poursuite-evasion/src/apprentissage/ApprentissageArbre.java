package apprentissage;


import ai.djl.Model;
import ai.djl.ndarray.NDManager;
import ai.djl.nn.SequentialBlock;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
import ai.djl.training.TrainingConfig;
import ai.djl.training.dataset.Dataset;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import ai.djl.translate.TranslateException;
import outils.CSVDataset;
import outils.CSVDatasetMLP;
import outils.CSVDatasetCNN;
import simulation.comportement.ReseauDeNeurones;
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
        CSVDataset csvDataset;
        CSVDataset csvDatasetValidate;
        SequentialBlock block = new SequentialBlock();
        switch (args[0]) {
            case "cnn":
                ReseauDeNeuronesCNN.setBlock();
                block = ReseauDeNeuronesCNN.getBlock();
                csvDataset = new CSVDatasetCNN.Builder().setSampling(32, true).build("donnees/game_data.csv");
                csvDatasetValidate = new CSVDatasetCNN.Builder().setSampling(32, true).build("donnees/game_data_validation.csv");
                break;
            case "mpl":
                ReseauDeNeurones.setBlock();
                block = ReseauDeNeurones.getBlock();
                csvDataset = new CSVDatasetMLP.Builder().setSampling(32, true).build("donnees/game_data.csv");
                csvDatasetValidate = new CSVDatasetMLP.Builder().setSampling(32, true).build("donnees/game_data_validation.csv");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + args[0]);
        }

        // Créer le modèle
        Model model = Model.newInstance(nonReseau);
        model.setBlock(block);

        //parametrage de l'entrainement
        //Loss l = new SoftmaxCrossEntropyLoss("test",1,-1,false,true);
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

        int epoch = 50;


        //entrainement du réseau
        EasyTrain.fit(trainer, epoch,(Dataset) csvDataset, (Dataset)csvDatasetValidate);

        //enregistrement du model
        Path modelDir = Paths.get("donnees/reseau/"+args[0]);
        Files.createDirectories(modelDir);

        model.setProperty("Epoch", String.valueOf(epoch));

        model.save(modelDir, args[0]);
    }

    /**
     * Methode permetant d'afficher une ligne (record) du Dataset
     * @param csvDataset set de donnees pour l'apprentissage
     * @param csvDatasetValidate set de donnees pour le teste
     */
    public void afficherRecord(CSVDatasetCNN csvDataset, CSVDatasetCNN csvDatasetValidate) {
        System.out.println("csv size : " + csvDataset.size());
        System.out.println("csv validate size : " + csvDatasetValidate.size());
        System.out.println(csvDataset.get(NDManager.newBaseManager(), 0).getData().get(0));
    }
}
