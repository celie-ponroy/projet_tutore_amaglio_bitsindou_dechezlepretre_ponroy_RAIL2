package lancercalculs;

import org.apache.commons.lang3.ArrayUtils;
import org.neuroph.core.data.DataSet;
import outils.Outil;
import simulation.Comportements;
import simulation.Deplacement;
import simulation.Simulation;
import simulation.personnages.Personnage;
import simulation.personnages.Position;

import java.io.IOException;
import java.util.List;

public class LancerCalculsDataset {

    public static void main(String[] args) throws IOException {
        System.out.println("Lancer le du dataset");
        DataSet ds = new DataSet(170, 9);
        for (int i = 0; i < 1; i++) {
            //Choix du dataset a composer
            Simulation simulation = null;
            if (args[1].equals("deterministe")) {
                simulation = new Simulation(Comportements.ArbreDeterministe, Comportements.ArbreDeterministe);
            } else {
                simulation = new Simulation(Comportements.ArbreDeterministe, Comportements.ArbreDeterministe);
            }

            Personnage perso = null;
            if (args[0].equals("P")) {
                perso = simulation.getPrisonnier();
            } else {
                perso = simulation.getGardien();
            }

            //ajouter un historique des déplacement choisit
            List<double[][]> histoBayes = simulation.historiqueBayesien.get(perso);
            List<Position> histoPos = simulation.historiquePosition.get(perso);
            List<Deplacement> histoDep = simulation.historiqueDeplacement.get(perso);

            for (int j = 0; j < histoDep.size() - 1; j++) {
                Position p = histoPos.get(j);
                double[] bayesPlat = Outil.applatissement(histoBayes.get(j));
                double[] dsr = ArrayUtils.addAll(bayesPlat, p.getY(), p.getX());
                double[] deplacement = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
                Deplacement d = histoDep.get(j);

                //boucle pour avoir l'index du deplacement
                int k = 0;
                for (Deplacement tmp : Deplacement.values()) {
                    if (tmp.equals(d)) {
                        deplacement[k] = 1;
                    }
                    k++;
                }
                Outil.afficher_tab(deplacement);
                ds.add(dsr, deplacement);
            }
            //enregistrement du dataset
            ds.save("donnees/sauvegardes_DataSet/" + args[0] + "-DataSet-" + args[1]);
        }
    }
}
