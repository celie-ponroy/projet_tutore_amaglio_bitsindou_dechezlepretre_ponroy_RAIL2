package simulation.comportement;

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
            this.simulation.getCarteBayesienne(personnage);
            //si deux probas sont égales, on va vers la plus proche de la sortie

        }
        return Deplacement.AUCUN;

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
