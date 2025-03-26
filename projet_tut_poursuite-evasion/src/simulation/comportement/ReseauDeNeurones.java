package simulation.comportement;

import ai.djl.MalformedModelException;
import ai.djl.Model;

import ai.djl.basicmodelzoo.basic.Mlp;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
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

    public ReseauDeNeurones(String nomReseau, Simulation simulation, Personnage personnage)  {
        this.sim = simulation;
        this.personnage = personnage;

        Path modelDir = Paths.get("donnees/mlp");
        model = Model.newInstance(nomReseau);
        model.setBlock(new Mlp(Simulation.getTailleCarte()*3, Deplacement.values().length, new int[] {350, 300, 256,200, 128,100, 64,50, 32, 20}));
        try {
            model.load(modelDir);
            System.out.println("Chargement ok !");
        }catch (MalformedModelException | IOException ex) {
            System.out.println(ex.getMessage());
        }

        this.translator = new Translator<NDArray, Integer>() {
            @Override
            public NDList processInput(TranslatorContext ctx, NDArray input) {
                // L'entrée est déjà un NDArray, on le met simplement dans un NDList
                return new NDList(input);
            }

            @Override
            public Integer processOutput(TranslatorContext ctx, NDList list) {
                // Trouver l'index de la probabilité la plus haute
                NDArray probabilities = list.singletonOrThrow().softmax(0);

                return (int) probabilities.argMax().getLong();
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
        double[] cartePos = Outil.applatissement(sim.getGardien().getPositionCarte());
        double[] carteBayesienne = Outil.applatissement(sim.getCarteBayesienne(sim.getGardien()));
        double[] carte = Outil.applatissement(sim.getCarteMursSortie());
        float[] input = Outil.doubleToFloat(Outil.concatener_tab(carte, Outil.concatener_tab(carteBayesienne, cartePos)));
        //Outil.afficher_tab(Outil.concatener_tab(carte, Outil.concatener_tab(carteBayesienne, cartePos)));
//        System.out.println("\nCarte Pos");
//        System.out.println(sim.getGardien().getPosition());
//        for(int i = 0; i < Simulation.CARTE.length; i++){
//            for(int j = 0; j < Simulation.CARTE[0].length; j++){
//                System.out.print(sim.getGardien().getPositionCarte()[i][j]+" ");
//            }
//            System.out.println();
//        }
//
//        System.out.println("\nCarte Baye");
//        for(int i = 0; i < Simulation.CARTE.length; i++){
//            for(int j = 0; j < Simulation.CARTE[0].length; j++){
//                System.out.print(sim.getCarteBayesienne(sim.getGardien())[i][j]+" ");
//            }
//            System.out.println();
//        }
//
//        System.out.println("\nCarte");
//        for(int i = 0; i < Simulation.CARTE.length; i++){
//            for(int j = 0; j < Simulation.CARTE[0].length; j++){
//                System.out.print(sim.getCarteMursSortie()[i][j]+" ");
//            }
//            System.out.println();
//        }

        NDArray arrayFlaot = manager.create(input);
        Integer resultat = 0;
        try {
            resultat = predictor.predict(arrayFlaot);
        } catch (ai.djl.translate.TranslateException te){
            System.out.println(te.getMessage());
            return Deplacement.AUCUN;
        }
        //System.out.println(Deplacement.values()[resultat]);
        return Deplacement.values()[resultat];
    }

    @Override
    public Comportements getType() {
        return Comportements.ReseauArbreAleatoire;
    }
}
