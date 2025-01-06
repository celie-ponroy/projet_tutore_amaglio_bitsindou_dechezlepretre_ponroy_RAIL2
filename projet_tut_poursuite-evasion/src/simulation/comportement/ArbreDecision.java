package simulation.comportement;
import simulation.Deplacement;
import simulation.personnages.Position;

public class ArbreDecision {
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
