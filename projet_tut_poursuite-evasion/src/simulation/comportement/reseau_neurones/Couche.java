package simulation.comportement.reseau_neurones;

class Couche {
    public Neurone Neurons[];
<<<<<<< HEAD
    public int Length;
=======
    public int 	  Length;
>>>>>>> 1279a4b (rebasage arbre)

    /**
     * Couche de Neurones
     *
<<<<<<< HEAD
     * @param l    Taille de la couche
     * @param prev Taille de la couche précédente
=======
     * @param l     Taille de la couche
     * @param prev  Taille de la couche précédente
>>>>>>> 1279a4b (rebasage arbre)
     */
    public Couche(int l, int prev) {
        Length = l;
        Neurons = new Neurone[l];

<<<<<<< HEAD
        for (int j = 0; j < Length; j++)
=======
        for(int j = 0; j < Length; j++)
>>>>>>> 1279a4b (rebasage arbre)
            Neurons[j] = new Neurone(prev);
    }
}