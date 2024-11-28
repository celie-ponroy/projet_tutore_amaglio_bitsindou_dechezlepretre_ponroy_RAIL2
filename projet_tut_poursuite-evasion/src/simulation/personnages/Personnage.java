package simulation.personnages;

import simulation.Case;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Personnage {
    protected Position position;
    public Personnage(int x, int y){
        this.position = new Position(x, y);
    }
    /**
     * Méthode permettant de récupérer les coordonnées des cases qui sont
     * vues par l'agent
     * @return liste d'objet Case
     */
    public List<Case> getCasesVisibles(){
        List<Case> casesVue = new ArrayList<>();
        Set<Case> casesPasVisibles = new HashSet<>();
        int x = this.position.getX();
        int y = this.position.getY();
        for(int nbTours = 0; nbTours < 4; nbTours++){
            
        }

        return casesVue;
    }
    public abstract void deplacer(int x, int y);

    public Position getPosition(){
        return this.position;
    }
}
