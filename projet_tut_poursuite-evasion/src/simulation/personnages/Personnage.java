package simulation.personnages;

public interface Personnage {
    public final int DIAGHG = 0;
    public final int HAUT = 1;
    public final int DIAGHD = 2;
    public final int GAUCHE = 3;
    public final int NEUTRE = 4;
    public final int DROITE = 5;
    public final int DIAGBG = 6;
    public final int BAS = 7;
    public final int DIAGBD = 8;
    public void deplacer(int action);
}
