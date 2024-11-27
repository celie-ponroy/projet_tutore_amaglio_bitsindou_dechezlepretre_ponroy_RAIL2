package simulation.personnages;

public class Position {


    private int x;
    private int y;

    Position(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    //se déplace de x, y
    public void deplacer(int x, int y){
        this.x = this.x + x;
        this.y = this.y + y;
    }
}
