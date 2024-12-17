package simulation.comportement;

import simulation.Case;
import simulation.Deplacement;
import simulation.Simulation;
import simulation.personnages.Personnage;
import simulation.personnages.Position;

import java.util.List;
import java.util.Stack;

public class ArbreDecisionGradien implements Comportement {
    Simulation simulation;
    Personnage personnage;

    public ArbreDecisionGradien(Simulation simulation, Personnage personnage){
        this.simulation = simulation;
        this.personnage = personnage;
    }

    @Override
    public Deplacement prendreDecision() {

        personnage.getVision();
        //si on voit l'autre personnage
        if(simulation.estVisible(personnage, false)){
            //on va vers l'autre personnage

            //on recupere le chemin

            Stack s =Simulation.CHEMIN.get(List.of(simulation.getPrisonnier().getPosition(),Simulation.getPosSortie()));
            if(s.size()>2){
                return direction(personnage.getPosition(), (Position) s.get(s.size()/2));//on va vers où l'autre personnage va
            }
            return direction(personnage.getPosition(), simulation.getPrisonnier().getPosition());//on va vers l'autre personnage
        }else{
            //on va vers la proba la plus grande
            List<Case> probas = this.simulation.getBayesiens().get(personnage).getPlusGrandeProbas();
            if(probas.size() == 1){
                return direction(personnage.getPosition(), new Position(probas.get(0).getX(), probas.get(0).getY()));
            }
            //si deux probas (ou plus) sont égales, on va vers la plus proche de la sortie
            Position sortie = Simulation.getPosSortie();
            //on cherche la case la plus proche de la liste des cases
            Case c = probas.stream().min((c1, c2) -> {
                return (int) (Math.abs(c1.getX() - sortie.getX()) + Math.abs(c1.getY() - sortie.getY()) - Math.abs(c2.getX() - sortie.getX()) - Math.abs(c2.getY() - sortie.getY()));
            }).orElse(probas.get(0));
            return direction(personnage.getPosition(), new Position(c.getX(), c.getY()));
        }

    }

    /**
     * Renvoie la direction à prendre pour aller de p1 à p2
     * @param p1
     * @param p2
     * @return
     */
    public Deplacement direction(Position p1, Position p2){//TODO : recherche de chemin
        if(p1.getX() == p2.getX()){
            if(p1.getY() < p2.getY()){
                return Deplacement.BAS;
            } else if (p1.getY() > p2.getY()){
                return Deplacement.HAUT;
            }

            return Deplacement.AUCUN;
        }else if(p1.getY() == p2.getY()){
            if(p1.getX() < p2.getX()){
                return Deplacement.DROITE;
            } else if (p1.getX() > p2.getX()){
                return Deplacement.GAUCHE;
            }

        } else if (p1.getX() < p2.getX()){
            if(p1.getY() < p2.getY()){
                return Deplacement.DIAG_BAS_DROITE;
            } else {
                return Deplacement.DIAG_HAUT_DROITE;
            }
        } else if (p1.getX() > p2.getX()){
            if(p1.getY() < p2.getY()){
                return Deplacement.DIAG_BAS_GAUCHE;
            } else {
                return Deplacement.DIAG_HAUT_GAUCHE;
            }
        }
        return Deplacement.AUCUN;
    }
}
