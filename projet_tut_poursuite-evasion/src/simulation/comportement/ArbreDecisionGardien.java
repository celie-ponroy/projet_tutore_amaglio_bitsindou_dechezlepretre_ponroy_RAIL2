package simulation.comportement;

import simulation.Case;
import simulation.Deplacement;
import simulation.Simulation;
import simulation.personnages.Personnage;
import simulation.personnages.Position;

import java.util.List;
import java.util.Stack;

public class ArbreDecisionGardien extends ArbreDecision implements Comportement {
    private Simulation simulation;
    private Personnage personnage;

    public ArbreDecisionGardien(Simulation simulation, Personnage personnage) {
        this.simulation = simulation;
        this.personnage = personnage;
    }

    /**
     * Renvoie le deplacement à prendre
     *
     * @return
     */
    @Override
    public Deplacement prendreDecision() {
        //si on voit l'autre personnage
        if (simulation.estVisible(personnage, false)) {
            //on va vers l'autre personnage
            //on recupere le chemin
            Stack<Position> s = Simulation.CHEMINS_G.get(List.of(simulation.getGardien().getPosition(), simulation.getPrisonnier().getPosition()));
            if (s.size() < 2) {
                return direction(personnage.getPosition(), simulation.getPrisonnier().getPosition());
            }
            Stack<Position> sMoitie = Simulation.CHEMINS_G.get(List.of(simulation.getPrisonnier().getPosition(), s.get(s.size() / 2)));
            return direction(personnage.getPosition(), sMoitie.getLast()); //on va vers où l'autre personnage va
        } else {
            //on va vers la proba la plus grande
            List<Case> probas = this.simulation.getBayesiens().get(personnage).getPlusGrandeProbas();
            if (probas.size() == 1) {
                Stack<Position> sProba = Simulation.CHEMINS_G.get(List.of(simulation.getGardien().getPosition(), new Position(probas.get(0).getX(), probas.get(0).getY())));
                return direction(personnage.getPosition(), sProba.getLast());
            }
            //si deux probas (ou plus) sont égales, on va vers la plus proche de la sortie
            Position sortie = Simulation.getPosSortie();
            //on cherche la case la plus proche de la liste des cases
            Case c = probas.stream().min((c1, c2) -> {
                return (int) (Math.abs(c1.getX() - sortie.getX()) + Math.abs(c1.getY() - sortie.getY()) - Math.abs(c2.getX() - sortie.getX()) - Math.abs(c2.getY() - sortie.getY()));
            }).orElse(probas.get(0));
            Stack<Position> s2 = Simulation.CHEMINS_G.get(List.of(personnage.getPosition(), new Position(c.getX(), c.getY())));
            return direction(personnage.getPosition(), s2.getLast());
        }

    }
}
