package outils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Outil {
    public static void sauve(String nomFichier, Object obj) {
        try {
            // cree un flux de sortie ( fichier puis flux d ’ objet )
            FileOutputStream os = new FileOutputStream(nomFichier);
            ObjectOutputStream oos = new ObjectOutputStream(os);
            // ecrit l ’ objet
            oos.writeObject(obj);
            // referme le flux
            oos.close();
        } catch (IOException e) {
            System.out.println(" erreur d ’E / S ");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(" erreur hors E / S ");
            e.printStackTrace();
        }
    }
}
