package calculs;

import outils.ChargementCarte;
import simulation.CaseEnum;
import simulation.personnages.Position;

import java.io.*;
import java.util.*;
//basé sur https://codegym.cc/groups/posts/a-search-algorithm-in-java

public class CalculChemins {
    public static int[][] CARTE = ChargementCarte.charger("donnees/laby.txt");

    /**
     * Recuperer la vision depuis le fichier cheminP.txt du prisonnier
     *
     * @return la liste des chemins pour toutes les paires de positions de la carte
     */
    public static HashMap<List<Position>, Stack> recupererCheminPrisonnier() {
        return lirefichier("./donnees/cheminsP.txt");
    }

    /**
     * Recuperer la vision depuis le fichier cheminG.txt du gardien
     *
     * @return la liste des chemins pour toutes les paires de positions de la carte
     */
    public static HashMap<List<Position>, Stack> recupererCheminGardien() {
        return lirefichier("./donnees/cheminsG.txt");
    }

    private static HashMap<List<Position>, Stack> lirefichier(String nomFichier) {
        HashMap<List<Position>, Stack> chemins = new HashMap<List<Position>, Stack>();
        //on recupere la chemins depuis le fichier chemins.txt
        try {
            BufferedReader br = new BufferedReader(new FileReader(nomFichier));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                //positions 1 et 2
                String[] positions = parts[0].split("\\|");
                String[] coordonnees1 = positions[0].split(",");
                String[] coordonnees2 = positions[1].split(",");
                Position position1 = new Position(Integer.parseInt(coordonnees1[0]), Integer.parseInt(coordonnees1[1]));
                Position position2 = new Position(Integer.parseInt(coordonnees2[0]), Integer.parseInt(coordonnees2[1]));


                //on cherche les positions
                //il faut enlever les crochets et les parenthèses
                parts[1] = parts[1].replace("[", "").replace("]", "").replace("(", "").replace(")", "").replace(" ", "");
                String[] positionsStr = parts[1].split(",");
                Stack listPositions = new Stack();
                for (String positionStr : positionsStr) {
                    String[] coordonneesPosition = positionStr.split(";");
                    if (coordonneesPosition.length != 2) {
                        continue;
                    }
                    int xPosition = Integer.parseInt(coordonneesPosition[0]);
                    int yPosition = Integer.parseInt(coordonneesPosition[1]);
                    listPositions.add(new Position(xPosition, yPosition));
                }
                chemins.put(List.of(position1, position2), listPositions);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chemins;
    }

    /**
     * Ecrire les chemins dans un fichier
     *
     * @throws IOException
     */
    public static void ecrireChemins(String nomCarte) throws IOException {
        //ecrire dans les fichiers

        ecrireFichier("./donnees/cheminsP.txt", false, nomCarte);
        ecrireFichier("./donnees/cheminsG.txt", true, nomCarte);
    }

    private static void ecrireFichier(String nomFichier, Boolean gardien, String nomCarte) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(nomFichier));
        HashMap vision = calculerChemins(gardien, nomCarte);
        for (int y1 = 0; y1 < CARTE.length; y1++) {
            for (int x1 = 0; x1 < CARTE[0].length; x1++) {
                for (int y2 = 0; y2 < CARTE.length; y2++) {
                    for (int x2 = 0; x2 < CARTE[0].length; x2++) {
                        Position src = new Position(x1, y1);
                        Position dest = new Position(x2, y2);
                        bos.write((x1 + "," + y1 + "|" + x2 + "," + y2 + ":").getBytes());
                        bos.write((vision.get(List.of(src, dest)) + ";" + "\n").getBytes());
                    }
                }
            }
        }
        bos.close();
    }

    /**
     * Calcule les chemins
     * @param gardien
     * @param nomCarte
     * @return
     */
    public static HashMap<List<Position>, Stack> calculerChemins(Boolean gardien, String nomCarte) {
        CARTE = ChargementCarte.charger(nomCarte);

        //changer la carte pour prendre en compte les raccourcis

        for (int i = 0; i < CARTE.length; i++) {
            for (int j = 0; j < CARTE[0].length; j++) {
                if (CARTE[i][j] == CaseEnum.RACCOURCI_GARDIEN.ordinal()) {
                    if (!gardien)
                        CARTE[i][j] = CaseEnum.MUR.ordinal();
                    else
                        CARTE[i][j] = CaseEnum.SOL.ordinal();
                }
            }
        }


        var mapStack = new HashMap<List<Position>, Stack>();

        for (int y1 = 0; y1 < CARTE.length; y1++) {
            for (int x1 = 0; x1 < CARTE[0].length; x1++) {
                for (int y2 = 0; y2 < CARTE.length; y2++) {
                    for (int x2 = 0; x2 < CARTE[0].length; x2++) {
                        Position src = new Position(x1, y1);
                        Position dest = new Position(x2, y2);
                        var res = aStarSearch(CARTE, CARTE.length, CARTE[0].length, src, dest);
                        //liste de positions avec src et dest
                        mapStack.put(List.of(src, dest), res);
                    }
                }
            }
        }
        return mapStack;
    }

    public static class Details {
        double value;
        int i;
        int j;

        public Details(double value, int i, int j) {
            this.value = value;
            this.i = i;
            this.j = j;
        }
    }


    public static class Cell {
        public Position parent;

        public double f, g, h;

        Cell() {
            parent = new Position(-1, -1);
            f = -1;
            g = -1;
            h = -1;
        }
    }

    /**
     * Verifier si la position de la case "point" est valide(si elle est dans la carte)
     *
     * @param rows
     * @param cols
     * @param point
     * @return
     */
    static boolean isValid(int rows, int cols, Position point) {
        if (rows > 0 && cols > 0)
            return (point.getY() >= 0) && (point.getY() < rows)
                    && (point.getX() >= 0)
                    && (point.getX() < cols);
        return false;
    }

    static boolean isUnBlocked(int[][] grid, int rows, int cols, Position point) {
        return isValid(rows, cols, point) //si la position est valide (dans le tableau
                && grid[point.getY()][point.getX()] != CaseEnum.MUR.ordinal();//si la case n'est pas un mur
    }


    static boolean isDestination(Position position, Position dest) {
        return position == dest || position.equals(dest);
    }


    static double calculateHValue(Position src, Position dest) {
        return Math.sqrt(Math.pow((src.getY() - dest.getY()), 2.0) + Math.pow((src.getX() - dest.getX()), 2.0));
    }


    static Stack tracePath(Cell[][] cellDetails, Position dest) {
        Stack<Position> path = new Stack<>();

        int row = dest.getY();
        int col = dest.getX();

        Position nextNode;
        do {
            path.push(new Position(col, row));
            nextNode = cellDetails[row][col].parent;
            row = nextNode.getY();
            col = nextNode.getX();
        } while (cellDetails[row][col].parent != nextNode);
        //on retire la dernière position
        path.remove(path.size() - 1);
        return path;
    }

    static Stack aStarSearch(int[][] grid, int rows, int cols, Position src, Position dest) {

        if (!isValid(rows, cols, src)) {
            return new Stack();
        }

        if (!isValid(rows, cols, dest)) {
            return new Stack();
        }

        if (!isUnBlocked(grid, rows, cols, src)
                || !isUnBlocked(grid, rows, cols, dest)) {
            return new Stack();
        }

        if (isDestination(src, dest)) {
            return new Stack();
        }

        boolean[][] closedList = new boolean[rows][cols];//liste des cases fermées(visitées)
        Cell[][] cellDetails = new Cell[rows][cols];

        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                cellDetails[x][y] = new Cell();
            }
        }


        int i, j;
        i = src.getY();
        j = src.getX();
        cellDetails[i][j] = new Cell();
        cellDetails[i][j].f = 0.0;
        cellDetails[i][j].g = 0.0;
        cellDetails[i][j].h = 0.0;
        cellDetails[i][j].parent = new Position(j, i);

        PriorityQueue<Details> openList = new PriorityQueue<>((o1, o2) -> (int) Math.round(o1.value - o2.value));

        openList.add(new Details(0.0, i, j));

        while (!openList.isEmpty()) {
            Details p = openList.peek();
            i = p.i;
            j = p.j;
            openList.poll();
            closedList[i][j] = true;
            //on regarde les voisins
            int[][] directions = {
                    {-1, -1}, {-1, 0}, {-1, 1},
                    {0, -1}, {0, 1},
                    {1, -1}, {1, 0}, {1, 1}
            };//(voisin

            for (int[] add : directions) {
                if (add[0] == 0 && add[1] == 0) {
                    continue;
                }
                Position neighbour = new Position(j + add[1], i + add[0]);

                if (isValid(rows, cols, neighbour)) {//si la position est valide
                    boolean diag = (add[0] != 0 && add[1] != 0);
                    boolean diagbloquee = (diag) && (grid[neighbour.getY()][j] == 0 || grid[i][neighbour.getX()] == 0);

                    if (cellDetails[neighbour.getY()] == null) {
                        cellDetails[neighbour.getY()] = new Cell[cols];
                    } //si la ligne n'existe pas, on la crée

                    if (cellDetails[neighbour.getY()][neighbour.getX()] == null) { //si la case n'existe pas
                        cellDetails[neighbour.getY()][neighbour.getX()] = new Cell();//on la crée
                    }

                    if (isDestination(neighbour, dest)) {//si on est arrivé
                        if (!diagbloquee) {
                            cellDetails[neighbour.getY()][neighbour.getX()].parent = new Position(j, i);
                            return tracePath(cellDetails, dest);
                        }

                    } else if (!closedList[neighbour.getY()][neighbour.getX()]
                            && isUnBlocked(grid, rows, cols, neighbour) && (!diagbloquee)) {// si la case n'est pas bloquée et n'est pas fermée

                        double gNew, hNew, fNew;
                        gNew = cellDetails[i][j].g + 1.0;
                        hNew = calculateHValue(neighbour, dest);
                        fNew = gNew + hNew;

                        if (cellDetails[neighbour.getY()][neighbour.getX()].f == -1
                                || cellDetails[neighbour.getY()][neighbour.getX()].f > fNew) {

                            openList.add(new Details(fNew, neighbour.getY(), neighbour.getX()));
                            cellDetails[neighbour.getY()][neighbour.getX()].g = gNew;

                            cellDetails[neighbour.getY()][neighbour.getX()].f = fNew;
                            cellDetails[neighbour.getY()][neighbour.getX()].parent = new Position(j, i);
                        }

                    }
                }
            }

        }
        return new Stack();
    }

}