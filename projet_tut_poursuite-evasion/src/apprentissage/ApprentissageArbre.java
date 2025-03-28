package apprentissage;


import ai.djl.Application;
import ai.djl.Model;
import ai.djl.nn.Activation;
import ai.djl.nn.Blocks;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.core.Linear;
import ai.djl.nn.norm.Dropout;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
import ai.djl.training.TrainingConfig;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import ai.djl.training.loss.SoftmaxCrossEntropyLoss;
import ai.djl.translate.TranslateException;
import lancercalculs.LancerCalculs;
import outils.CSVDataset;
import simulation.Simulation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ApprentissageArbre {
    public static void main(String[] args) throws IOException, TranslateException {
        //M"thode pour recalculer le dataset
        //LancerCalculs.init();

        Application application = Application.Tabular.SOFTMAX_REGRESSION;
        long inputSize = Simulation.getTailleCarte()* 3L;
        long outputSize = 9;



        //parametrage nb couches / neurones
        SequentialBlock block = new SequentialBlock();
        block.add(Blocks.batchFlattenBlock(inputSize));
        //block.add(Activation::relu);
        block.add(Linear.builder().setUnits(350).build());
        block.add(Activation::relu);
        block.add(Linear.builder().setUnits(300).build());
        block.add(Activation::relu);
        block.add(Linear.builder().setUnits(256).build());
        block.add(Activation::relu);
        block.add(Linear.builder().setUnits(200).build());
        block.add(Activation::relu);
        block.add(Linear.builder().setUnits(128).build());
        block.add(Activation::relu);
        block.add(Linear.builder().setUnits(100).build());
        block.add(Activation::relu);
        block.add(Linear.builder().setUnits(64).build());
        block.add(Activation::relu);
        block.add(Linear.builder().setUnits(50).build());
        block.add(Activation::relu);
        block.add(Linear.builder().setUnits(32).build());
        block.add(Activation::relu);
        block.add(Linear.builder().setUnits(20).build());
        block.add(Activation::relu);
        block.add(Linear.builder().setUnits(outputSize).build());

        //creation du modele du réseau a sauvegarder
        Model model = Model.newInstance("reseau_Arbre");
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

        CSVDataset csvDataset = new CSVDataset.Builder().setSampling(32, true).build("donnees/game_data.csv");
        CSVDataset csvDatasetValidate = new CSVDataset.Builder().setSampling(32, true).build("donnees/game_data_validation.csv");

        System.out.println(csvDataset.size());
        System.out.println(csvDatasetValidate.size());

        EasyTrain.fit(trainer, epoch, csvDataset, csvDatasetValidate);

        //enregistrement du model
        Path modelDir = Paths.get("donnees/mlp");
        Files.createDirectories(modelDir);

        model.setProperty("Epoch", String.valueOf(epoch));

        model.save(modelDir, "mlp_");
    }
}
