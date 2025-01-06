package lancercalculs;

import org.apache.commons.lang3.ArrayUtils;
import org.neuroph.core.data.DataSet;
import outils.Outil;
import simulation.Deplacement;
import simulation.Simulation;
import simulation.personnages.Personnage;
import simulation.personnages.Position;

import java.io.IOException;
import java.util.List;

public class LancerCalculsDataset {

    public static void main(String[] args) throws IOException {
        System.out.println("Lancer le du dataset");
        //Pour le moment limité car par assez de position différentes (pas de spawn aléatoire)
        DataSet ds = new DataSet(170, 9);

        for (int i = 0; i < 1; i++) {
            Simulation simulation = new Simulation();

            //Pour le moment seul le gardient a un rn
            Personnage ga = simulation.getGardien();
            //ajouter un historique des déplacement choisit
            List<double[][]> histoBayes = simulation.historiqueBayesien.get(ga);
            List<Position> histoPos = simulation.historiquePosition.get(ga);
            List<Deplacement> histoDep = simulation.historiqueDeplacement.get(ga);

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
            ds.save("Sauvegardes_DataSet/dataSetTest");
        }
    }
}
