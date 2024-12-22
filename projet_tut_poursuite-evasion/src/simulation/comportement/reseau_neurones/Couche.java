package simulation.comportement.reseau_neurones;

import java.io.Serializable;

class Couche implements Serializable {
    public Neurone Neurons[];
    public int Length;

    /**
     * Couche de Neurones
     *
     * @param l    Taille de la couche
     * @param prev Taille de la couche précédente
     */
    public Couche(int l, int prev) {
        Length = l;
        Neurons = new Neurone[l];
        for (int j = 0; j < Length; j++)
            Neurons[j] = new Neurone(prev);
    }
}