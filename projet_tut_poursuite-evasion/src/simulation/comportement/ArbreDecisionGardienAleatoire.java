package simulation.comportement;
    import simulation.Case;
    import simulation.Comportements;
    import simulation.Deplacement;
    import simulation.Simulation;
    import simulation.personnages.Personnage;
    import simulation.personnages.Position;

    import java.io.Serializable;
    import java.util.List;
    import java.util.Stack;

    public class ArbreDecisionGardienAleatoire extends ArbreDecision implements Comportement , Serializable {
        private Simulation simulation;
        private Personnage personnage;

    public ArbreDecisionGardienAleatoire(Simulation simulation, Personnage personnage) {
        this.simulation = simulation;
        this.personnage = personnage;
    }

    /**
     * Renvoie le deplacement à prendre
     *
     * @return
     */
    @Override
    public Deplacement prendreDecision() {
        //si on voit l'autre personnage
        if (simulation.estVisible(simulation.getPrisonnier(), true)) {
            //on va vers l'autre personnage
            //on recupere le chemin
            Stack<Position> s = Simulation.CHEMINS_G.get(List.of(simulation.getGardien().getPosition(), simulation.getPrisonnier().getPosition()));
            if (s.size() < 2) {
                return direction(personnage.getPosition(), simulation.getPrisonnier().getPosition());
            }
            Stack<Position> sMoitie = Simulation.CHEMINS_G.get(List.of(simulation.getPrisonnier().getPosition(), s.get(s.size() / 2)));
            return direction(personnage.getPosition(), sMoitie.getLast()); //on va vers où l'autre personnage va
        } else {
            // on recupere les probas
            //on associe à chaque proba un déplacement
            double[][] probas = this.simulation.getBayesiens().get(personnage).getCarteBayesienne();

            int[] deplacement = choixDeplacementAleatoire(probas);
            Stack<Position> s = Simulation.CHEMINS_G.get(List.of(personnage.getPosition(), new Position(deplacement[0], deplacement[1])));
            return direction(personnage.getPosition(), s.getLast());

        }

    }

    @Override
    public Comportements getType() {
        return Comportements.ArbreAleatoire;
    }

    /**
     * Choisit une case vers où se deplacer en fonction des probas
     *
     * @param probas
     * @return
     */
    private int[] choixDeplacementAleatoire(double[][] probas) {
        var choix = Math.random();

        double somme = 0.0;
        for (int i = 0; i < probas.length; i++) {
            for (int j = 0; j < probas[i].length; j++) {
                if (probas[i][j] == -1) {
                    continue;
                }
                somme += probas[i][j];

                if (choix <= somme) {
                    return new int[]{j, i};
                }
            }
        }
        return new int[]{probas.length - 1, probas[0].length - 1};
    }
}
