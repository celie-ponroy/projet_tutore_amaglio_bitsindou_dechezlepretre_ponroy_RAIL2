package lancercalculs;

import affichage.DessinJeu;
import moteur.Jeu;
import simulation.Comportements;
import simulation.Simulation;

import java.util.ArrayList;
import java.util.List;

//Classe qui lance le nombre de simualtion en fonction du nombre de parties donnée par l'utilisateur et des comportements choisis
//Elle se base sur le MVC
public class LancerAnalyse implements Jeu {

    //atributs
    private boolean etreFini = false;
    private List<DessinJeu> observateurs;
    private int nbVictoireGardien;
    private int nbVictoirePrisonnier;
    private int matchNull;
    private int nbIterationCourrante;
    private int nbDeplacementPerso;
    private List<Integer> historiqueDeplacementsPerso = new ArrayList<>();


    //constructeur
    public LancerAnalyse() {
        nbVictoireGardien = 0;
        nbVictoirePrisonnier = 0;
        matchNull = 0;
        observateurs = new ArrayList<>();
        nbIterationCourrante=0;
        nbDeplacementPerso=0;
    }

    /**
     * Méthode qui lance les analyses
     */
    public void lancerAnalyse(int nbParties, Comportements comportementG, Comportements comportementP) {
        for (int i = 1; i <= nbParties; i++) {
            //A iteration on remet les déplacements à 0
            nbDeplacementPerso=0;

            nbIterationCourrante=i;
            Simulation simulation = new Simulation(comportementG, comportementP);
            //Nombre de déplacements des personnages par partie
            nbDeplacementPerso= simulation.getNbDeplacementsPerso();
            //Ajout des déplacements dans les listes
            historiqueDeplacementsPerso.add(nbDeplacementPerso);
            System.out.println("Partie " + (i) + comportementG + " VS " + comportementP);
            //Compte le nombre de victoire
            if (simulation.getVictoireGardien() == simulation.getVictoirePrisonnier()){
                matchNull++;
            } else if (simulation.getVictoireGardien()) {
                nbVictoireGardien++;
            } else if (simulation.getVictoirePrisonnier()) {
                nbVictoirePrisonnier++;
            }
            System.out.println("Victoire Gardien: " + nbVictoireGardien + " Victoire Prisonnier: " + nbVictoirePrisonnier + " Match Null: " + matchNull);
            notifierObservateurs();
        }
        etreFini = true;
    }

    //Méthode qui ajoute un observateur
    @Override
    public void ajouterObservateur(DessinJeu dj) {
        this.observateurs.add(dj);
    }

    //Méthode qui notifie les observateurs (ici les graphiques)
    @Override
    public void notifierObservateurs() {
        for (DessinJeu dj : observateurs) {
            dj.update(this);
        }
    }

    @Override
    public boolean etreFini() {
        return etreFini;
    }

    /**
     * Méthode qui retourne le nombre de victoire du gardien
     */
    public int getNbVictoireGardien() {
        return nbVictoireGardien;
    }

    /**
     * Méthode qui retourne le nombre de victoire du prisonnier
     */
    public int getNbVictoirePrisonnier() {
        return nbVictoirePrisonnier;
    }

    /**
     * Méthode qui retourne le nombre de match null
     */
    public int getMatchNull() {
        return matchNull;
    }

    public int getNbIterationCourrante() {
        return nbIterationCourrante;
    }

    public int getNbDeplacementPerso(int index ) {
        if (index >= 0 && index < historiqueDeplacementsPerso.size()) {
            return historiqueDeplacementsPerso.get(index);
        }
        return -1; // Valeur par défaut en cas d'index invalide
    }


    public List<Integer> getHistoriqueDeplacementsPrisonnier() {
        return historiqueDeplacementsPerso;
    }

    /**
     * Méthode qui réinitialise les données des graphiques
     */
    public void reinitialiser() {
        nbVictoireGardien = 0;
        nbVictoirePrisonnier = 0;
        matchNull = 0;
        nbIterationCourrante = 0;
        nbDeplacementPerso = 0;
        historiqueDeplacementsPerso.clear();
        etreFini = false;
        notifierObservateurs();
    }
}
