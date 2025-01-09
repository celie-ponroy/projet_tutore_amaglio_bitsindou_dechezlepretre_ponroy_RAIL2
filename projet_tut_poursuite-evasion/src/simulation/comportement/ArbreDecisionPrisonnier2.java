package simulation.comportement;

import simulation.Case;
import simulation.CaseEnum;
import simulation.Deplacement;
import simulation.Simulation;
import simulation.personnages.Personnage;
import simulation.personnages.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ArbreDecisionPrisonnier2 extends ArbreDecision implements Comportement {
    private Simulation simulation;
    private Personnage personnage;

    public ArbreDecisionPrisonnier2(Simulation simulation, Personnage personnage){
        this.simulation = simulation;
        this.personnage = personnage;
    }

    /**
     * Renvoie le deplacement à prendre
     * @return
     */
    @Override
    public Deplacement prendreDecision() {
        Stack<Position> s = Simulation.CHEMINS_P.get(List.of(personnage.getPosition(),Simulation.getPosSortie()));
        if(s.empty())
            return direction(personnage.getPosition(),Simulation.getPosSortie());
        //si on voit pas le gardien
        if(!simulation.estVisible(personnage, true)){
            //position la plus probable du gardien
            Case cG = simulation.getBayesiens().get(personnage).getPlusGrandeProbas().get(0);
            Position pG = new Position(cG.getX(), cG.getY());
            if(!s.contains(pG))
                return direction(personnage.getPosition(), s.getLast());
            return fuir(pG);
            //return direction(personnage.getPosition(), s.getLast());//rajouter en évitant là où on pense que le gardien est
        }

        //sinon est ce qu'il est sur le chemin de la sortie ?
        if(s.contains(simulation.getGardien().getPosition())){
            //on fui le gardien
            //on cherche la meilleure solution des cases à coté de nous
            return fuir(simulation.getGardien().getPosition());

        }else{
            //on va vers la sortie
            return direction(personnage.getPosition(), s.getLast());
        }
    }


    /**
     * choisit le meilleur deplacement pour fuir
     * @return
     */
    private Deplacement fuir(Position pG){

        //on cherche la meilleure solution des cases à coté de nous
        Position positionPerso = this.personnage.getPosition();
        Position sortie = Simulation.getPosSortie();
        List<Position> casesVisites = new ArrayList<>();
        casesVisites.add(positionPerso);
        List<Position> casesAVisiter = positionPerso.casesAdjacentes();
        boolean end = false;
        int i = 0;

        while(!end){
            i++;
            for (Position p: casesAVisiter){
                if(casesVisites.contains(p)){
                    continue;
                }
                casesVisites.add(p);
                Stack<Position> s = Simulation.CHEMINS_P.get(List.of(p,sortie));

                boolean croiserG = s.contains(pG);
                Stack<Position> cheminRecherché = Simulation.CHEMINS_P.get(List.of(positionPerso,p));
                if(cheminRecherché.contains(pG)||s.contains(pG)){
                    croiserG = true;
                }
                for (Position positionAdjacenteGardien: pG.casesAdjacentes()){
                    if(casesVisites.contains(positionAdjacenteGardien)){
                        continue;
                    }
                    casesVisites.add(positionAdjacenteGardien);
                    if(cheminRecherché.contains(positionAdjacenteGardien)||s.contains(positionAdjacenteGardien)) {
                        croiserG = true;
                        break;
                    }

                }
                if(!cheminRecherché.empty()&&!croiserG) {//cas si aucun chemin (dont murs)  //si le gardien bloque on cherche une autre case
                    //System.out.println("chemin trouvé : "+direction(positionPerso, cheminRecherché.getLast()));
                    return direction(positionPerso, cheminRecherché.getLast());//ne prends pas en compte les diagonales
                }
            }
            List<Position> casesAVisiter2 = new ArrayList<>();
            for (Position p: casesAVisiter){
                for (Position p2: p.casesAdjacentes()){

                    if(Simulation.CARTE[0].length<=p2.getX()||Simulation.CARTE.length<=p2.getY()||p2.getX()<0||p2.getY()<0){
                        continue;
                    }
                    if(Simulation.CARTE[p2.getY()][p2.getX()]== CaseEnum.MUR.ordinal()){
                        continue;
                    }
                    if(!casesVisites.contains(p2)){
                        casesAVisiter2.add(p2);
                    }
                }
            }
            casesAVisiter.clear();
            casesAVisiter = casesAVisiter2;
            if(casesAVisiter.isEmpty()){
                end = true;
            }
            if (i>15){
                end = true;
            }
        }
        
        //System.out.println("Aucun chemin trouvé");

        return oppose(direction(positionPerso,simulation.getGardien().getPosition()));
    }
}
