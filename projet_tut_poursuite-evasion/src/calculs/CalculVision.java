package calculs;

import outils.ChargementCarte;
import simulation.CaseEnum;
import simulation.personnages.Position;

import java.awt.geom.Line2D;
import java.io.*;
import java.util.*;

// Classe permettant de calculer la vision

public class CalculVision {
    static int[][] CARTE = ChargementCarte.charger("donnees/laby.txt");

    /**
     * Recuperer la vision depuis le fichier vision.txt
     *
     * @return la liste des cases pour toutes les positions de la carte
     */
    public static HashMap<Position, ArrayList<Position>> recupererVision(String G_P) {
        HashMap<Position, ArrayList<Position>> vision = new HashMap<>();
        //on récupère la vision depuis le fichier vision.txt
        try {
            BufferedReader br = new BufferedReader(new FileReader("./donnees/vision"+G_P+".txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                String[] coordonnees = parts[0].split(",");
                int x = Integer.parseInt(coordonnees[0]);
                int y = Integer.parseInt(coordonnees[1]);

                ArrayList<Position> positions = new ArrayList<>();
                //on cherche les positions
                //il faut enlever les crochets et les parenthèses
                parts[1] = parts[1].replace("[", "").replace("]", "").replace("(", "").replace(")", "").replace(" ", "");
                positions = new ArrayList<>();
                String[] positionsStr = parts[1].split(",");
                for (String positionStr : positionsStr) {
                    String[] coordonneesPosition = positionStr.split(";");
                    if (coordonneesPosition.length != 2) {
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
     *
     * @throws IOException
     */
    public static void ecrireVision() throws IOException {

        HashMap vision = calculerCarteVision();
        HashMap<Position, ArrayList<Position>> camera = new HashMap();
        for (int y = 0; y < CARTE.length; y++) {
            for (int x = 0; x < CARTE[0].length; x++) {
                if (CARTE[y][x] == CaseEnum.CAMERA.ordinal()) {
                    var c = ajoutCamera(vision,x,y,3);
                    if(c != null)
                        camera.put(new Position(x,y),c);
                }
            }
        }
        ecrireFichierVision(vision,"G");
        ecrireFichierVision(camera,"C");
        vision = calculerCarteVision();
        ecrireFichierVision(vision,"P");

    }
    public static void ecrireFichierVision(HashMap vision, String nomfichier) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("./donnees/vision"+nomfichier+".txt"));
        for (int y = 0; y < CARTE.length; y++) {
            for (int x = 0; x < CARTE[0].length; x++) {
                //afficher la position
                bos.write((x + "," + y + ":").getBytes());
                bos.write((vision.get(new Position(x, y)) + ";" + "\n").getBytes());
            }
        }
        bos.close();

    }

    /**
     * Calculer la vision de chaque case de la carte
     *
     * @return la liste des cases pour toutes les positions de la carte
     */
    public static HashMap calculerCarteVision() {
        HashMap<Position, ArrayList<Position>> res = new HashMap();
        for (int y = 0; y < CARTE.length; y++) {
            for (int x = 0; x < CARTE[0].length; x++) {
                //si la case est un mur
                if (CARTE[y][x] == CaseEnum.MUR.ordinal()) {
                    res.put(new Position(x, y), new ArrayList());
                    continue;
                }
                res.put(new Position(x, y), calculerVision(x, y,29));
            }
        }
        cleanVision(res);


        return res;
    }


    /**
     * Ajoute à la vision une camera (zonne de vision en plus)
     * @param vision vison à laquellle on ajoute
     * @param x position x de la caméra
     * @param y position y de la caméra
     * @param taille portée de la caméra
     */

    public static  ArrayList<Position> ajoutCamera(HashMap<Position, ArrayList<Position>> vision, int x,int y , int taille) {
        var casesAlarme = calculerVision(x,y,taille);
        cleanVisionCase(casesAlarme,new Position(x,y));
        for (var key : vision.keySet()) {
            vision.get(key).addAll(casesAlarme);
        }
        return casesAlarme;
    }

    /**
     * Calculer la vision d'un personnage sur une case
     *
     * @param xPerso position x du personnage
     * @param yPerso position y du personnage
     * @return la liste des positions des cases visibles
     */
    public static ArrayList calculerVision(int xPerso, int yPerso, int tailleVision) {
        ArrayList res = new ArrayList();

        //recuperrer la carte autour du personnage
        //pour chaque case de la carte où le personnage etre
        int tailledecalage = (tailleVision - 1) / 2;
        int[][] vision = new int[tailleVision][tailleVision];
        for (int y = -tailledecalage; y <= tailledecalage; y++) {
            for (int x = -tailledecalage; x <= tailledecalage; x++) {

                int coordoneeCarteX = xPerso + x;
                int coordoneeCarteY = yPerso + y;

                int coordoneeVisionX = x + tailledecalage;
                int coordoneeVisionY = y + tailledecalage;


                if (coordoneeCarteY < 0 || coordoneeCarteY >= CARTE.length || coordoneeCarteX < 0 || coordoneeCarteX >= CARTE[0].length) {
                    vision[coordoneeVisionY][coordoneeVisionX] = CaseEnum.MUR.ordinal();


                    continue;
                }
                vision[coordoneeVisionY][coordoneeVisionX] = CARTE[coordoneeCarteY][coordoneeCarteX];

            }
        }

        //determiner une liste de murs
        List<Position> murs = new ArrayList<>();
        for (int y = -tailledecalage; y <= tailledecalage; y++) {
            for (int x = -tailledecalage; x <= tailledecalage; x++) {
                if (vision[y + tailledecalage][x + tailledecalage] == CaseEnum.MUR.ordinal()) {

                    murs.add(new Position(xPerso + x, yPerso + y));
                }
            }
        }
        //pour chaque case de la vision
        for (int y = -tailledecalage; y <= tailledecalage; y++) {
            for (int x = -tailledecalage; x <= tailledecalage; x++) {
                //si la case est un mur


                if (!(vision[y + tailledecalage][x + tailledecalage] == CaseEnum.MUR.ordinal())) {//si la case est pas un mur


                    //on trace une droite entre le personnage et la case
                    int xCaseCourante = xPerso + x;
                    int yCaseCourante = yPerso + y;

                    Line2D line = new Line2D.Double(xPerso, yPerso, xCaseCourante, yCaseCourante);
                    boolean visible = true;
                    for (Position mur : murs) {
                        //on regarde si la droite un des cotés du mur
                        //nonVisible =
                        if (line.intersects(mur.getX() - 0.5, mur.getY() - 0.5, 1, 1)) {
                            visible = false;
                        }
                    }
                    if (visible) {
                        res.add(new Position(xCaseCourante, yCaseCourante));
                    }
                }
            }
        }
        return res;
    }

    /**
     * Nettoyer la vision pour ne garder que les cases voulues
     *
     * @param vision
     */
    private static void cleanVision(HashMap<Position, ArrayList<Position>> vision) {
        //on retire les cases non voulues
        for (Position pPerso : vision.keySet()) {
            cleanVisionCase(vision.get(pPerso), pPerso);

        }
    }
    private static void cleanVisionCase( ArrayList<Position> visionCur, Position pPerso) {
        Iterator<Position> iterator = visionCur.iterator();
        while (iterator.hasNext()) {
            Position position = iterator.next();
            ArrayList<Position> casesVisites = new ArrayList<>();
            if (!parcours(pPerso, position, visionCur, casesVisites)) {
                iterator.remove();
            }
        }
    }

    /**
     * Parcours de la carte pour voir si il y a un chemin entre la case et le personnage
     *
     * @param pPerso
     * @param positionCur
     * @param visionCur
     * @param casesVisites
     * @return true si il y a un chemin entre la case et le personnage
     */

    private static boolean parcours(Position pPerso, Position positionCur, ArrayList<Position> visionCur, ArrayList<Position> casesVisites) {
        if (positionCur.equals(pPerso)) {
            return true;
        }
        ArrayList<Position> casesAdjacentes = positionCur.casesAdjacentes();
        casesVisites.add(positionCur);
        boolean res = false;

        //on parcours les cases visibles adjacentes si elles ne sont pas déjà visités
        for (Position caseAdjacente : casesAdjacentes) {
            if (casesVisites.contains(caseAdjacente)) {//si la case est déjà visitée on passe
                continue;
            }
            if (!visionCur.contains(caseAdjacente)) {//si la case n'est pas dans la vision on passe
                continue;
            }
            //si la case est celle du perso on garde la case sinon on la retire
            if (pPerso.equals(caseAdjacente)) {
                return true;
            }
            //si la case a un chemin de la case adjacente au perso qui passe par que des cases visibles
            if (parcours(pPerso, caseAdjacente, visionCur, casesVisites))
                res = true;
        }
        return res;
    }

}
