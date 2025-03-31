package simulation.comportement;

import simulation.CaseEnum;
import simulation.Comportements;
import simulation.Deplacement;
import simulation.Simulation;
import simulation.personnages.Personnage;
import simulation.personnages.Position;

public class Aleatoire implements Comportement, java.io.Serializable {
    Simulation simulation;
    Personnage perso;

    public Aleatoire(Simulation simulation, Personnage perso) {
        this.simulation = simulation;
        this.perso = perso;

    }

    /**
     * Renvoi un déplacement choisit aléatoirement dans les cases disponibles
     * @return
     */
    @Override
    public Deplacement prendreDecision() {
        Deplacement deplacement = deplacementAleatoire();
        Position position = new Position(perso.getPosition().getX(), perso.getPosition().getY());
        position.deplacement(deplacement);
        boolean gardien = perso.equals(simulation.getGardien());
        boolean invalide = simulation.murPresent(position.getX(), position.getY());
        if (!gardien) {
            if (Simulation.CARTE[position.getX()][position.getY()] == CaseEnum.SPAWN_PRISONNIER.ordinal()) {
                invalide = true;
            }
        }
        while (invalide) {
            deplacement = deplacementAleatoire();
            position = new Position(perso.getPosition().getX(), perso.getPosition().getY());
            position.deplacement(deplacement);
            invalide = simulation.murPresent(position.getX(), position.getY());
            if (!gardien) {
                if (Simulation.CARTE[position.getX()][position.getY()] == CaseEnum.SPAWN_PRISONNIER.ordinal()) {
                    invalide = true;
                }
            }
        }
        return deplacement;
    }

    @Override
    public Comportements getType() {
        return Comportements.Aleatoire;
    }

    /**
     * Choisit une direction aléatoire
     * @return
     */
    private Deplacement deplacementAleatoire() {
        int random = (int) (Math.random() * 9);
        Deplacement deplacement = Deplacement.AUCUN;
        if (random == 0) {
            deplacement = Deplacement.HAUT;
        } else if (random == 1) {
            deplacement = Deplacement.BAS;
        } else if (random == 2) {
            deplacement = Deplacement.GAUCHE;
        } else if (random == 3) {
            deplacement = Deplacement.DROITE;
        } else if (random == 4) {
            deplacement = Deplacement.DIAG_HAUT_GAUCHE;
        } else if (random == 5) {
            deplacement = Deplacement.DIAG_HAUT_DROITE;
        } else if (random == 6) {
            deplacement = Deplacement.DIAG_BAS_GAUCHE;
        } else if (random == 7) {
            deplacement = Deplacement.DIAG_BAS_DROITE;
        }
        return deplacement;
    }

}
