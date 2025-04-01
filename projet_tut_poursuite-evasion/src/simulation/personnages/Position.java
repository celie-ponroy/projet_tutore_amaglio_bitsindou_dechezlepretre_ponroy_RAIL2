package simulation.personnages;


import simulation.Deplacement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Position implements Serializable {


    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Position getNouvellePosition(Deplacement d){
        Position p = new Position(this.x, this.y);
        p.deplacement(d);
        return p;
    }
    public void deplacement(Deplacement d) {
        switch (d) {
            case BAS:
                this.y += 1;
                break;
            case HAUT:
                this.y -= 1;
                break;
            case GAUCHE:
                this.x -= 1;
                break;
            case DROITE:
                this.x += 1;
                break;
            case DIAG_HAUT_GAUCHE:
                this.y -= 1;
                this.x -= 1;
                break;
            case DIAG_HAUT_DROITE:
                this.y -= 1;
                this.x += 1;
                break;
            case DIAG_BAS_GAUCHE:
                this.y += 1;
                this.x -= 1;
                break;
            case DIAG_BAS_DROITE:
                this.y += 1;
                this.x += 1;
                break;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ";" + y + ")";
    }

    public ArrayList<Position> casesAdjacentes() {
        ArrayList<Position> res = new ArrayList<>();
        int x = this.x;
        int y = this.y;
        for (int y1 = -1; y1 <= 1; y1++) {
            for (int x1 = -1; x1 <= 1; x1++) {
                if (x1 == 0 && y1 == 0) {
                    continue;
                }
                res.add(new Position(x + x1, y + y1));
            }
        }
        return res;
    }

}
