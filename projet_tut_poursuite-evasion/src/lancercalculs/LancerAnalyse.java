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
public class LancerAnalyse implements Jeu {

    //attributs
    private boolean etreFini = false;
    private List<DessinJeu> observateurs;
    private int nbVictoireGardien;
    private int nbVictoirePrisonnier;
    private int matchNull;
    private int nbIterationCourrante;
    private int nbDeplacementPerso;
    private List<Integer> historiqueDeplacementsPerso = new ArrayList<>();
    private HashMap casesVisitees = new HashMap();
    private HashMap casesVisiteesPrisonnier = new HashMap();
    private HashMap casesVisiteesGardien = new HashMap();
    private int nbIterationsTotal; // Nombre total d'itérations à effectuer
    private boolean pause;
    private HashMap<Position, Integer> casesDepartPris = new HashMap<>();
    private HashMap<Position, Integer> casesDepartGard = new HashMap<>();


    //constructeur
    public LancerAnalyse() {
        nbVictoireGardien = 0;
        nbVictoirePrisonnier = 0;
        matchNull = 0;
        observateurs = new ArrayList<>();
        nbIterationCourrante = 0;
        nbDeplacementPerso = 0;
        pause = false;
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
        List<Position> posPrisonnier = simulation.getHistoriquePosition().get(simulation.getPrisonnier());
        List<Position> posGardien = simulation.getHistoriquePosition().get(simulation.getGardien());

        //Récupération de la position de départ(toute première position) du prisonnier et du gardien
        Position posDepartPrisonnier = posPrisonnier.get(0);
        Position posDepartGardien = posGardien.get(0);

        //Mise à jour de la position de départ du prisonnier
        if (casesDepartPris.containsKey(posDepartPrisonnier)) {
            casesDepartPris.put(posDepartPrisonnier, (int) casesDepartPris.get(posDepartPrisonnier) + 1);
        } else {
            casesDepartPris.put(posDepartPrisonnier, 1);
        }

        //Mise à jour de la position de départ du gardien
        if (casesDepartGard.containsKey(posDepartGardien)) {
            casesDepartGard.put(posDepartGardien, (int) casesDepartGard.get(posDepartGardien) + 1);
        } else {
            casesDepartGard.put(posDepartGardien, 1);
        }

        // Mise à jour des cases visitées par le prisonnier
        updateCasesVisiteesPrisonnier(posPrisonnier);

        // Mise à jour des cases visitées par le gardien
        updateCasesVisiteesGardien(posGardien);


        // Mise à jour des statistiques
        if (simulation.getVictoireGardien() == simulation.getVictoirePrisonnier()) {
            matchNull++;
        } else if (simulation.getVictoireGardien()) {
            nbVictoireGardien++;
        } else if (simulation.getVictoirePrisonnier()) {
            nbVictoirePrisonnier++;
        }

        // Notification des observateurs pour mise à jour de l'interface
        notifierObservateurs();

        // True si la fin si c'est la dernière partie
        if (numeroPartie == nbIterationsTotal) {
            etreFini = true;
        }
    }


    /**
     * Méthode pour mettre à jour les cases visitées par le prisonnier
     * @param positions
     */
    private void updateCasesVisiteesPrisonnier(List<Position> positions) {
        for (Position pos : positions) {
            //si la case est déjà visitée, on incrémente le nombre de fois où elle a été visitée
            if (casesVisiteesPrisonnier.containsKey(pos)) {
                casesVisiteesPrisonnier.put(pos, (int) casesVisiteesPrisonnier.get(pos) + 1);
                casesVisitees.put(pos, (int) casesVisitees.get(pos) + 1); // Mise à jour des cases visitées par les deux personnages
            } else {
                casesVisiteesPrisonnier.put(pos, 1);
                casesVisitees.put(pos, 1); // Mise à jour des cases visitées par les deux personnages
            }
        }
    }

    /**
     * Méthode pour mettre à jour les cases visitées par le gardien
     * @param positions
     */
    private void updateCasesVisiteesGardien(List<Position> positions) {
        for (Position pos : positions) {
            //si la case est déjà visitée, on incrémente le nombre de fois où elle a été visitée
            if (casesVisiteesGardien.containsKey(pos)) {
                casesVisiteesGardien.put(pos, (int) casesVisiteesGardien.get(pos) + 1);
                casesVisitees.put(pos, (int) casesVisitees.get(pos) + 1); // Mise à jour des cases visitées par les deux personnages
            } else {
                casesVisiteesGardien.put(pos, 1);
                casesVisitees.put(pos, 1); // Mise à jour des cases visitées par les deux personnages
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

    /**
     * Méthode qui retourne le nombre d'itérations courrantes
     * @return nbIterationCourrante
     */
    public int getNbIterationCourrante() {
        return nbIterationCourrante;
    }

    /**
     * Méthode qui retourne le nombre de déplacements du personnage
     *
     * @return nbDeplacementPerso
     */
    public int getNbDeplacementPerso(int index) {
        if (index >= 0 && index < historiqueDeplacementsPerso.size()) {
            return historiqueDeplacementsPerso.get(index);
        }
        return -1; // Valeur par défaut en cas d'index invalide
    }

    /**
     * Méthode qui retourne les cases visitées
     * @return
     */
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
        casesVisiteesPrisonnier.clear();
        casesVisiteesGardien.clear();
        notifierObservateurs();
    }

    /**
     * Méthode qui retourne le nombre total d'itérations
     * @param nbIterations
     */
    public void setNbIterationsTotal(int nbIterations) {
        this.nbIterationsTotal = nbIterations;
    }

    /**
     * Méthode qui retourne true si l'analyse est en pause
     * @return true si l'analyse est en pause false sinon
     */
    public synchronized boolean isPause() {
        return pause;
    }

    /**
     * Méthode qui met à jour l'état de pause de l'analyse
     *
     * @param b true ou false
     */
    public void setPause(boolean b) {
        this.pause = b;
    }

    public HashMap<Position, Integer> getCasesDepartGard() {
        return casesDepartGard;
    }

    public HashMap<Position, Integer> getCasesDepartPris() {
        return casesDepartPris;
    }

    public HashMap<Position, Integer> getCasesVisiteesPrisonnier() {
        return casesVisiteesPrisonnier;
    }

    public HashMap<Position, Integer> getCasesVisiteesGardien() {
        return casesVisiteesGardien;
    }
}
