package simulation;

/**
 * Classe qui permet de créer une simulation en fonction des choix de l'utilisateur
 */
public class FabriqueSimulation {

    private Simulation simulation;

    /**
     * constructeur
     */
    public FabriqueSimulation() {

    }

    /**
     * Crée une simulation en fonction des choix de l'utilisateur
     * @param choixMode choix du mode (interactif ou non interactif)
     * @param choixPersonnage choix du personnage (prisonnier ou gardien)
     * @param choixDifficulte choix de la difficulté de l'IA
     * @return la simulation créée
     *
     */
    public Simulation creerSimulation(String choixMode, String choixPersonnage, String choixDifficulte) {
        if (choixMode.equals("Mode interactif")) {
            if (choixPersonnage.equals("Prisonnier")) {
                this.simulation = new Simulation(true);//si l'utilisateur est prisonnier
            } else {
                this.simulation = new Simulation(false);//si l'utilisateur est gardien
            }
        } else {
            //this.simulation = new Simulation(); //constructeur du mode non interactif
        }

        return this.simulation;
    }

}
