package sauvegarde;

import simulation.Comportements;
import simulation.Simulation;

import java.io.*;


public class Sauvegarde {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //test de sauvegarde et de chargement
       sauvegarder(new Simulation(true,Comportements.Aleatoire), "simu");
        Simulation retour = charger("simu") ;
        System.out.println(retour) ;
        System.out.println(retour.getHistoriqueDeplacement()) ;
    }
    public static void sauvegarder(Simulation s,String nomFichier) throws IOException {
        SimulationSerializable simSer =  new SimulationSerializable(s) ;
        File fichier =  new File("donnees/"+nomFichier+".ser") ;
        ObjectOutputStream oos =  new ObjectOutputStream(new FileOutputStream(fichier)) ;
        oos.writeObject(simSer) ;
    }
    public static Simulation charger(String nomFichier) throws IOException, ClassNotFoundException {
        File fichier =  new File("donnees/"+nomFichier+".ser") ;
        ObjectInputStream ois =  new ObjectInputStream(new FileInputStream(fichier)) ;
        SimulationSerializable simSer =  (SimulationSerializable) ois.readObject() ;
        return simSer.creerSimulation();
    }
}
