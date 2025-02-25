package affichage;

import moteur.Jeu;


/**
 * interface pour afficher le jeu
 */
public interface DessinJeu {

    /**
     * affiche l'etat du jeu dans le canvas passe en parametre
     *
     * @param jeu jeu a afficher
     */
    void update(Jeu jeu);

}
