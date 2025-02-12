package simulation;

import java.io.Serializable;

public enum Deplacement implements Serializable {
    HAUT, BAS, GAUCHE, DROITE, DIAG_HAUT_GAUCHE, DIAG_HAUT_DROITE, DIAG_BAS_GAUCHE, DIAG_BAS_DROITE, AUCUN;
}
