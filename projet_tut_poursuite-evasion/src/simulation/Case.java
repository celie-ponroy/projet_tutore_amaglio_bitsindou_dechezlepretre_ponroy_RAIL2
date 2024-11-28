package simulation;

import java.util.Objects;

/**

 Cette classe permet de représenter une case lors de manipulation
 pour les calculs d'inférence bayésienne*/
public class Case {
    private int x;
    private int y;
    private int contenu;

    public Case(int x, int y, int contenu) {
        this.x = x;
        this.y = y;
        this.contenu = contenu;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getContenu() {
        return contenu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Case aCase = (Case) o;
        return x == aCase.x && y == aCase.y && contenu == aCase.contenu;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, contenu);
    }
}
