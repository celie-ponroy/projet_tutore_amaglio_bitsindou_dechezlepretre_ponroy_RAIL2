package simulation;

import simulation.personnages.Position;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CalculVision {

    public static HashMap calculerCarteVision(){
        int [][] carte = Simulation.CARTE;
        HashMap res = new HashMap();
        for (int i = 0; i < carte.length ; i++) {
            for (int j = 0; j < carte[0].length; j++) {
                res.put(new Position(j,i),calculerVision(j, i));
            }
        }
        return res;
    }
    public static ArrayList calculerVision(int xPerso, int yPerso) {
        ArrayList res = new ArrayList();
        //recuperrer la carte autour du personnage
        //pour chaque case de la carte où le personnage etre
        int[][] vision = new int[7][7];
        for (int i = -3; i < 3; i++) {//a verifier les bornes
            for (int j = -3; j < 3; j++) {
                //si on est en bordure de la carte
                if (yPerso + i < 0 || yPerso + i >= Simulation.CARTE.length || xPerso + j < 0 || xPerso + j >= Simulation.CARTE[0].length) {
                    vision[i+3][j+3] = Simulation.MUR;
                    continue;
                }
                vision[i + 3][j + 3] = Simulation.CARTE[yPerso + i][xPerso + j];
            }
        }
        //determiner une liste de murs
        List<Position> murs = new ArrayList<>();
        for (int i = -3; i < 3; i++) {
            for (int j = -3; j < 3; j++) {
                if (vision[i+3][j+3] == Simulation.MUR) {
                    murs.add(new Position(xPerso + j, yPerso + i));
                }
            }
        }
        //pour chaque case de la vision
        for (int i = -3; i < 3; i++) {
            for (int j = -3; j < 3; j++) {
                //si la case est un mur
                if (vision[i+3][j+3] == Simulation.MUR) {
                } else {//sinon regarder si elle est bloquée par un mur
                    //on trace une droite entre le personnage et la case
                    int xCaseCourante = xPerso + j;
                    int yCaseCourante = yPerso + i;

                    Line2D line = new Line2D.Double(xPerso, yPerso, xCaseCourante, yCaseCourante);
                    boolean nonVisible = false;
                    for (Position mur : murs) {
                        //on regarde si la droite un des cotés du mur
                        nonVisible = line.intersects(mur.getX() + 0.5, mur.getX() + 0.5, 1, 1);
                    }
                    if (!nonVisible) {//si visible
                        res.add(new Position(xCaseCourante, yCaseCourante));
                    } else {
                        //vision[i][j] =;onmets en non visible
                    }
                }
            }
        }
        return res;
    }
    //pseudo code
    /*
    * function line_intersects_rectangle(line, rectangle):
    for edge in rectangle.edges:
        projection = dot_product(edge, line.direction)
        if projection > 0 and projection < edge.length:
            return True  # Line intersects rectangle
    return False
    * */
}
