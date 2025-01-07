package simulation.comportement;

import simulation.Deplacement;
import simulation.Simulation;
import simulation.personnages.Personnage;
import simulation.personnages.Position;

public class Aleatoire implements Comportement{
    Simulation simulation;
    Personnage perso;
    public Aleatoire(Simulation simulation, Personnage perso){
        this.simulation = simulation;
        this.perso = perso;

    }

    @Override
    public Deplacement prendreDecision() {
        Deplacement deplacement = deplacementAleatoire();
        Position position = new Position(perso.getPosition().getX(), perso.getPosition().getY());
        position.deplacement(deplacement);
        while(simulation.murPresent(position.getX(), position.getY())){
            deplacement = deplacementAleatoire();
            position = new Position(perso.getPosition().getX(), perso.getPosition().getY());
            position.deplacement(deplacement);
        }
        return deplacement;
    }
    private Deplacement deplacementAleatoire(){
        int random = (int) (Math.random() * 9);
        Deplacement deplacement = Deplacement.AUCUN;
        if(random == 0){
            deplacement = Deplacement.HAUT;
        } else if(random == 1){
            deplacement = Deplacement.BAS;
        } else if(random == 2){
            deplacement = Deplacement.GAUCHE;
        } else if(random == 3){
            deplacement = Deplacement.DROITE;
        } else if(random == 4){
            deplacement = Deplacement.DIAG_HAUT_GAUCHE;
        } else if(random == 5){
            deplacement = Deplacement.DIAG_HAUT_DROITE;
        } else if(random == 6){
            deplacement = Deplacement.DIAG_BAS_GAUCHE;
        } else if(random == 7){
            deplacement = Deplacement.DIAG_BAS_DROITE;
        }
        return deplacement;
    }
}
