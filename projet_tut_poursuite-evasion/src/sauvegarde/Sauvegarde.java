package sauvegarde;

import simulation.Comportements;
import simulation.Simulation;

import java.io.*;
import java.util.ArrayList;


public class Sauvegarde {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //test de sauvegarde et de chargement
       sauvegarder(new Simulation(true,Comportements.Aleatoire), "simu.ser");
        Simulation retour = charger("simu") ;
        System.out.println(retour) ;
        System.out.println(retour.getHistoriqueDeplacement()) ;
        System.out.println(nomsSauvegardes());
    }

    /**
     * Sauvegarde une simulation dans un fichier
     * @param s
     * @param nomFichier
     * @throws IOException
     */
    public static void sauvegarder(Simulation s,String nomFichier) throws IOException {
        SimulationSerializable simSer =  new SimulationSerializable(s) ;
        File fichier =  new File("donnees/sauvegardes_simulation/"+nomFichier+"") ;
        ObjectOutputStream oos =  new ObjectOutputStream(new FileOutputStream(fichier)) ;
        oos.writeObject(simSer) ;
    }

    /**
     * Charge une simulation à partir d'un fichier
     * @param nomFichier
     * @return la simulation chargée
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Simulation charger(String nomFichier) throws IOException, ClassNotFoundException {
        File fichier =  new File("donnees/sauvegardes_simulation/"+nomFichier+"") ;
        ObjectInputStream ois =  new ObjectInputStream(new FileInputStream(fichier)) ;
        SimulationSerializable simSer =  (SimulationSerializable) ois.readObject() ;
        return simSer.creerSimulation();
    }

    public static ArrayList<String> nomsSauvegardes(){
        File repertoire = new File("donnees/sauvegardes_simulation/");
        File[] decksfiles = repertoire.listFiles();
        ArrayList<String> listeFichiers = new ArrayList();
        int i = 0;
        while (i < decksfiles.length) {
            listeFichiers.add(decksfiles[i].getName());
            i++;
        }
        return listeFichiers;
    }
}
