package lancercalculs;

import affichage.DessinJeu;
import javafx.application.Platform;
import moteur.Jeu;
import simulation.Comportements;
import simulation.Simulation;
import simulation.personnages.Position;

import java.util.ArrayList;
import java.util.HashMap;
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
    //HashMap pour stocker les cases et leur nombre de fois ou elles ont été visitées
    private HashMap casesVisitees = new HashMap();
    private int nbIterationsTotal; // Nombre total d'itérations à effectuer


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
     * Méthode qui lance une seule partie d'analyse
     */
    public void lancerAnalyse(int numeroPartie, Comportements comportementG, Comportements comportementP) {
        // Réinitialisation des déplacements pour cette partie
        nbDeplacementPerso = 0;
        nbIterationCourrante = numeroPartie;

        // Création et exécution de la simulation
        Simulation simulation = new Simulation(comportementG, comportementP);
        nbDeplacementPerso = simulation.getNbDeplacementsPerso();

        // Ajout des déplacements dans l'historique
        historiqueDeplacementsPerso.add(nbDeplacementPerso);

        // Récupération des positions
        List<Position> posPrisonnier = simulation.historiquePosition.get(simulation.getPrisonnier());
        List<Position> posGardien = simulation.historiquePosition.get(simulation.getGardien());

        // Mise à jour des cases visitées pour le prisonnier
        updateCasesVisitees(posPrisonnier);

        // Mise à jour des cases visitées pour le gardien
        updateCasesVisitees(posGardien);

        // Affichage du résultat de la partie
        System.out.println("Partie " + numeroPartie + " " + comportementG + " VS " + comportementP);

        // Mise à jour des statistiques
        if (simulation.getVictoireGardien() == simulation.getVictoirePrisonnier()) {
            matchNull++;
        } else if (simulation.getVictoireGardien()) {
            nbVictoireGardien++;
        } else if (simulation.getVictoirePrisonnier()) {
            nbVictoirePrisonnier++;
        }

        System.out.println("Victoire Gardien: " + nbVictoireGardien +
                " Victoire Prisonnier: " + nbVictoirePrisonnier +
                " Match Null: " + matchNull);

        // Notification des observateurs pour mise à jour de l'interface
        notifierObservateurs();

        // On marque la fin si c'est la dernière partie
        if (numeroPartie == nbIterationsTotal) {
            etreFini = true;
        }
    }

    /**
     * Méthode utilitaire pour mettre à jour les cases visitées
     */
    private void updateCasesVisitees(List<Position> positions) {
        for (Position pos : positions) {
            if (casesVisitees.containsKey(pos)) {
                casesVisitees.put(pos, (int)casesVisitees.get(pos) + 1);
            } else {
                casesVisitees.put(pos, 1);
            }
        }
    }

    //Méthode qui ajoute un observateur
    @Override
    public void ajouterObservateur(DessinJeu dj) {
        this.observateurs.add(dj);
    }

    //Méthode qui notifie les observateurs (ici les graphiques)
    @Override
    public void notifierObservateurs() {
        Platform.runLater(() -> {
            for (DessinJeu dj : observateurs) {
                dj.update(this);
            }
        });
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

    public HashMap <Position, Integer> getCasesVisitees() {
        return casesVisitees;
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
        casesVisitees.clear();
        notifierObservateurs();
    }

    // Méthode pour définir le nombre total d'itérations
    public void setNbIterationsTotal(int nbIterations) {
        this.nbIterationsTotal = nbIterations;
    }

}
