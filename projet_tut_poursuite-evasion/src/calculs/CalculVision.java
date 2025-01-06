package calculs;

import simulation.personnages.Position;

import java.awt.geom.Line2D;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Classe permettant de calculer la vision

public class CalculVision {
    static int[][] carte = new int[][]{
        {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
        {-1, 0, 0, 0, 0, 0,-1, 2,-1,-1,-1,-1,-1,-1},
        {-1,-1,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,-1},
        {-1, 0, 0, 0, 0, 0,-1,-1,-1,-1,-1,-1, 0,-1},
        {-1, 0,-1,-1, 0, 0,-1,-1,-1,-1,-1,-1, 0,-1},
        {-1, 0,-1,-1, 0, 0, 0, 0, 0, 0,-1,-1, 0,-1},
        {-1, 0, 0, 0, 0,-1,-1,-1,-1, 0, 0, 0, 0,-1},
        {-1,-1, 0, 0, 0, 0, 0, 0,-1,-1,-1,-1,-1,-1},
        {-1,-1,-1,-1, 0,-1,-1,-1,-1,-1,-1,-1,-1,-1},
        {-1,-1,-1, 0, 0, 0,-1,-1,-1,-1,-1,-1,-1,-1},
        {-1,-1,-1, 0, 0, 0,-1,-1,-1,-1,-1,-1,-1,-1},
        {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}};//carte de simulation
    static int mur =-1;

    /**
     * Recuperer la vision depuis le fichier vision.txt
     * @return la liste des cases pour toutes les positions de la carte
     */
    public static HashMap<Position,ArrayList<Position>> recupererVision() {
        HashMap<Position, ArrayList<Position>> vision = new HashMap<>();
        //on recupere la vision depuis le fichier vision.txt
        try {
            BufferedReader br = new BufferedReader(new FileReader("./donnees/vision.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                String[] coordonnees = parts[0].split(",");
                int x = Integer.parseInt(coordonnees[0]);
                int y = Integer.parseInt(coordonnees[1]);

                ArrayList<Position> positions = new ArrayList<>();
                //on cherche les positions
                //il faut enlever les crochets et les parenthèses
                parts[1] = parts[1].replace("[","").replace("]","").replace("(","").replace(")","").replace(" ","");
                positions = new ArrayList<>();
                String[] positionsStr = parts[1].split(",");
                for (String positionStr : positionsStr) {
                    String[] coordonneesPosition = positionStr.split(";");
                    if(coordonneesPosition.length != 2){
                        continue;
                    }
                    int xPosition = Integer.parseInt(coordonneesPosition[0]);
                    int yPosition = Integer.parseInt(coordonneesPosition[1]);
                    positions.add(new Position(xPosition, yPosition));
                }
                vision.put(new Position(x, y), positions);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vision;
    }

    /**
     * Ecrire la vision dans un fichier (et dans le terminal)
     * @throws IOException
     */
    public static void ecrireVision() throws IOException {
        HashMap vision = calculerCarteVision();
        //ecrire dans un fichier
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("./donnees/vision.txt"));
        for (int y = 0; y < carte.length ; y++) {
            for (int x = 0; x < carte[0].length; x++) {
                //afficher la position
                bos.write((x+","+y+":").getBytes());
                bos.write((vision.get(new Position(x,y))+";"+"\n").getBytes());
            }
        }
        bos.close();

    }

    /**
     * Calculer la vision de chaque case de la carte
     * @return la liste des cases pour toutes les positions de la carte
     */
    public static HashMap calculerCarteVision(){

        HashMap res = new HashMap();
        for (int y = 0; y < carte.length ; y++) {
            for (int x = 0; x < carte[0].length; x++) {
               //si la case est un mur
                if (carte[y][x] == mur) {
                    res.put(new Position(x,y),new ArrayList());
                    continue;
                }
                res.put(new Position(x,y),calculerVision(x, y));
            }
        }
        cleanVision(res);
        return res;
    }

    /**
     * Calculer la vision d'un personnage sur une case
     * @param xPerso position x du personnage
     * @param yPerso position y du personnage
     * @return la liste des positions des cases visibles
     */
    public static ArrayList calculerVision(int xPerso, int yPerso) {
        ArrayList res = new ArrayList();

        //recuperrer la carte autour du personnage
        //pour chaque case de la carte où le personnage etre
        int tailleVision = 9;//29 pour toute la carte 9 default
        int tailledecalage = (tailleVision - 1) / 2;
        int[][] vision = new int[tailleVision][tailleVision];
        for (int y = -tailledecalage; y <= tailledecalage; y++) {//a verifier les bornes
            for (int x = -tailledecalage; x <= tailledecalage; x++) {
                //si on est en bordure de la carte
                int coordoneeCarteX = xPerso + x;
                int coordoneeCarteY = yPerso + y;

                int coordoneeVisionX = x + tailledecalage;
                int coordoneeVisionY = y + tailledecalage;
                if (coordoneeCarteY < 0 || coordoneeCarteY >= carte.length ||coordoneeCarteX < 0 || coordoneeCarteX >= carte[0].length) {
                    vision[coordoneeVisionY][coordoneeVisionX] = mur;

                    continue;
                }
                vision[coordoneeVisionY][coordoneeVisionX] = carte[coordoneeCarteY][coordoneeCarteX];

            }
        }

        //determiner une liste de murs
        List<Position> murs = new ArrayList<>();
        for (int y = -tailledecalage; y <= tailledecalage; y++) {
            for (int x = -tailledecalage; x <= tailledecalage; x++) {
                if (vision[y+tailledecalage][x+tailledecalage] == mur) {

                    murs.add(new Position(xPerso + x, yPerso + y));
                }
            }
        }
        //pour chaque case de la vision
        for (int y = -tailledecalage; y <= tailledecalage; y++) {
            for (int x = -tailledecalage; x <= tailledecalage; x++) {
                //si la case est un mur

                if (!(vision[y+tailledecalage][x+tailledecalage] == mur)) {//si la case est pas un mur


                    //on trace une droite entre le personnage et la case
                    int xCaseCourante = xPerso + x;
                    int yCaseCourante = yPerso + y;

                    Line2D line = new Line2D.Double(xPerso, yPerso, xCaseCourante, yCaseCourante);
                    boolean visible = true;
                    for (Position mur : murs) {
                        //on regarde si la droite un des cotés du mur
                        //nonVisible =
                        if(line.intersects(mur.getX() - 0.5, mur.getY() - 0.5, 1, 1)){
                            visible=false;
                        }
                    }
                    if (visible) {//si visible
                        res.add(new Position(xCaseCourante, yCaseCourante));
                    }
                }
            }
        }
        return res;
    }
    private static void cleanVision(HashMap<Position,ArrayList<Position>> vision){
        //on retire les cases non voulues
        for (Position pPerso: vision.keySet()) {
            ArrayList<Position> visionCur = vision.get(pPerso);

            //si la case n'as pas un chemin qui part de la case au personnage qui passe par que des cases visibles
            //on retire la case

            for (int j = 0; j < visionCur.size(); j++) {
                Position position = visionCur.get(j);
                System.out.println("Clean vision de :"+pPerso +" "+position);
                ArrayList<Position> casesVisites = new ArrayList<>();
                if (!parcours(pPerso,position,visionCur, casesVisites)){
                    System.out.println("ok");
                    visionCur.remove(j);
                }

            }

        }
    }

    /**
     * Parcours de la carte pour voir si il y a un chemin entre la case et le personnage
     * @param pPerso
     * @param positionCur
     * @param visionCur
     * @param casesVisites
     * @return true si il y a un chemin entre la case et le personnage
     */
    private static boolean parcours(Position pPerso, Position positionCur, ArrayList<Position> visionCur, ArrayList<Position> casesVisites){
        System.out.println(positionCur+" : "+visionCur+" "+casesVisites);
        if(positionCur.equals(pPerso)){
            return true;
        }
        ArrayList<Position> casesAdjacentes = casesAdjacentes(positionCur);
        casesVisites.add(positionCur);
        boolean res = false;

        //on parcours les cases visibles adjacentes si elles ne sont pas déjà visités
        for (Position caseAdjacente :casesAdjacentes) {
            if(casesVisites.contains(caseAdjacente)){//si la case est déjà visitée on passe
                continue;
            }
            if(!visionCur.contains(caseAdjacente)){//si la case n'est pas dans la vision on passe
                continue;
            }
            //si la case est celle du perso on garde la case sinon on la retire
            if(pPerso.equals(caseAdjacente)){
                return true;
            }
            //si la case a un chemin de la case adjacente au perso qui passe par que des cases visibles
            if(parcours(pPerso,caseAdjacente,visionCur,casesVisites))
                res = true;
        }
        return res;
    }

    private static ArrayList<Position> casesAdjacentes(Position position){
        ArrayList<Position> res = new ArrayList<>();
        int x = position.getX();
        int y = position.getY();
        for(int y1=-1;y1<=1;y1++){
            for(int x1=-1;x1<=1;x1++){
                if(x1==0 && y1==0){
                    continue;
                }
                res.add(new Position(x+x1,y+y1));
            }
        }
        return res;
    }




}
