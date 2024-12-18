package simulation.apprentissage;

import outils.Outil;
import simulation.Simulation;
import simulation.comportement.reseau_neurones.ReseauDeNeurones;

public class ApprentissageDeArbre {
    public static void main(String[] args) {
        int[] couches = new int[Integer.parseInt(args[0])+2];
        couches[0] = 14*12+2;
        couches[couches.length-1] = 9;
        for(int i = 1; i < couches.length-1; i++){
            couches[i] = Integer.parseInt(args[1]);
        }
        ReseauDeNeurones rn = new ReseauDeNeurones(couches, 0.6);
        Simulation simulation = new Simulation(rn, false);
        simulation.apprentissage(Integer.parseInt(args[2]));
        System.out.println("Nombre de tours : "+simulation.getNbTours()+"\nSimulation finie : "+simulation.etreFini()+"\n");
        System.out.println("Gardien : "+simulation.getGardien());
        System.out.println("Prisonnier : "+simulation.getPrisonnier());
        Outil.sauve(("Sauvegardes_reseaux/rnArbre"+args[2]+"-"+Integer.parseInt(args[0])+2)+"-"+args[1]+".save", rn);
    }
}
