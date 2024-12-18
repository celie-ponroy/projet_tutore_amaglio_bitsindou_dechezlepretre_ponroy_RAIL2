package moteur;


import affichage.DessinJeu;

/**
 * modele de jeu
 */
public interface Jeu {

    void ajouterObservateur(DessinJeu dj);
    void notifierObservateurs();

    /**
     * initialisation du jeu
     */
    void init();

    /**
     * verifie si le jeu est fini
     *
     * @return booleen true si le jeu est fini
     */
    boolean etreFini();
}
