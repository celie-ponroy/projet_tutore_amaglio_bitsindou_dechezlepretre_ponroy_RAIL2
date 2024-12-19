package simulation.apprentissage;

import outils.Outil;
import simulation.Simulation;
import simulation.comportement.reseau_neurones.ReseauDeNeurones;

public class ApprentissageDeArbre {
    public static void main(String[] args) {
        //Args  0 : nb couches cachées
        //Args 1 : nb parties jouées
        int nbCouches = Integer.parseInt(args[0])+2;
        int[] couches = new int[nbCouches];
        couches[0] = 14*12+2;
        couches[couches.length-1] = 9;
        couches[1] = 10;
        couches[2] = 5;


        ReseauDeNeurones rn = new ReseauDeNeurones(couches, 0.03);
        int compteur = 0;
        while(compteur < Integer.parseInt(args[1])) {
            Simulation simulation = new Simulation(rn, false);
            simulation.apprentissage(500);
            /*System.out.println("Nombre de tours : " + simulation.getNbTours() + "\nSimulation finie : " + simulation.etreFini() + "\n");
            System.out.println("Gardien : " + simulation.getGardien());
            System.out.println("Prisonnier : " + simulation.getPrisonnier());*/
            System.out.println(compteur);
            compteur++;
        }
        Outil.sauve("Sauvegardes_reseaux/rnArbre"+args[1]+"-"+nbCouches+"-"+args[0]+".save", rn);
    }
}
