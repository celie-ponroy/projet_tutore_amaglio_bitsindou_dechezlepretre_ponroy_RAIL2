package simulation;

import java.io.Serializable;
import java.util.Objects;

/**
 * Cette classe permet de représenter une case lors de manipulation
 * pour les calculs d'inférence bayésienne
 */

public class Case implements Serializable {
    private int x;
    private int y;

    public Case(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Case aCase = (Case) o;
        return x == aCase.x && y == aCase.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Case{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
