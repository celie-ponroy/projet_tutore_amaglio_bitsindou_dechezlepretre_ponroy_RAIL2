package simulation.apprentissage;

import outils.Outil;
import simulation.Simulation;
import simulation.comportement.reseau_neurones.ReseauDeNeurones;

public class ApprentissageDeArbre {
    public static void main(String[] args) {
        //Args  0 : nb couches cachées
        //Args 1 : nb neurones par couches cachées
        //Args 2: nb parties jouées
        int nbCouches = Integer.parseInt(args[0])+2;
        int[] couches = new int[nbCouches];
        couches[0] = 14*12+2;
        couches[couches.length-1] = 9;
        for(int i = 1; i < couches.length-1; i++){
            couches[i] = Integer.parseInt(args[1]);
        }
        ReseauDeNeurones rn = new ReseauDeNeurones(couches, 0.3);
        int compteur = 0;
        while(compteur < Integer.parseInt(args[2])) {
            Simulation simulation = new Simulation(rn, false);
            simulation.apprentissage(500);
            System.out.println("Nombre de tours : " + simulation.getNbTours() + "\nSimulation finie : " + simulation.etreFini() + "\n");
            System.out.println("Gardien : " + simulation.getGardien());
            System.out.println("Prisonnier : " + simulation.getPrisonnier());
            compteur++;
        }
        Outil.sauve("Sauvegardes_reseaux/rnArbre"+args[2]+"-"+nbCouches+"-"+args[1]+".save", rn);
    }
}
