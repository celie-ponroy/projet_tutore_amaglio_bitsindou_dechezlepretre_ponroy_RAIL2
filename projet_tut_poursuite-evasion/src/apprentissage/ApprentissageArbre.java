package apprentissage;


import ai.djl.Application;
import ai.djl.Model;
import ai.djl.basicdataset.tabular.CsvDataset;
import ai.djl.nn.Activation;
import ai.djl.nn.Blocks;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.core.Linear;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
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

public class ApprentissageArbre {
    public static void main(String[] args) throws IOException, TranslateException {

        Application application = Application.Tabular.SOFTMAX_REGRESSION;
        long inputSize = Simulation.getTailleCarte()+2;
        long outputSize = 9;

        //parametrage nb couches / neurones
        SequentialBlock block = new SequentialBlock();
        block.add(Blocks.batchFlattenBlock(inputSize));
        block.add(Linear.builder().setUnits(200).build());
        block.add(Activation::relu);
        block.add(Linear.builder().setUnits(100).build());
        block.add(Activation::relu);
        block.add(Linear.builder().setUnits(outputSize).build());

        //creation du modele du réseau a sauvegarder
        Model model = Model.newInstance("reseau_Arbre");
        model.setBlock(block);

        //parametrage de l'entrainement
        DefaultTrainingConfig config = new DefaultTrainingConfig(Loss.softmaxCrossEntropyLoss())
                .addEvaluator(new Accuracy()) // Fonction qui permet de comprendre comment performe le réseau a l'entrainement
                .addTrainingListeners(TrainingListener.Defaults.logging()); //affiche les info d'entrainement

        Trainer trainer = model.newTrainer(config);

        int epoch = 50;
        CSVDataset csvDataset = new CSVDataset.Builder().setSampling(32,false).build();
        System.out.println(csvDataset.size());
        EasyTrain.fit(trainer, epoch, csvDataset, null);

        //enregistrement du model
        Path modelDir = Paths.get("donnees/mlp");
        Files.createDirectories(modelDir);

        model.setProperty("Epoch", String.valueOf(epoch));

        model.save(modelDir, "mlp_"+200+"_"+100);

    }
}
