    package simulation.comportement;

    import simulation.Case;
    import simulation.Deplacement;
    import simulation.Simulation;
    import simulation.personnages.Personnage;
    import simulation.personnages.Position;

    import java.util.List;
    import java.util.Stack;

    public class ArbreDecisionGardienAleatoire extends ArbreDecision implements Comportement  {
        private Simulation simulation;
        private Personnage personnage;

        public ArbreDecisionGardienAleatoire(Simulation simulation, Personnage personnage){
            this.simulation = simulation;
            this.personnage = personnage;
        }
        /**
         * Renvoie le deplacement à prendre
         * @return
         */
        @Override
        public Deplacement prendreDecision() {
            //si on voit l'autre personnage
            if(simulation.estVisible(personnage, false)){
                //on va vers l'autre personnage
                //on recupere le chemin
                Stack<Position> s =Simulation.CHEMINS.get(List.of(simulation.getGardien().getPosition(),simulation.getPrisonnier().getPosition()));
                if(s.size()<2){
                    return direction(personnage.getPosition(), simulation.getPrisonnier().getPosition());
                }
                Stack<Position> sMoitie =Simulation.CHEMINS.get(List.of(simulation.getPrisonnier().getPosition(),s.get(s.size()/2)));
                return direction(personnage.getPosition(), sMoitie.getLast()); //on va vers où l'autre personnage va
            }else {
                // on recupere les probas
                //on associe à chaque proba un déplacement
                double[][] probas = this.simulation.getBayesiens().get(personnage).getCarteBayesienne();
                Deplacement[][] deplacements = new Deplacement[probas.length][probas[0].length];

                for(int i = 0; i < probas.length; i++){
                    for(int j = 0; j < probas[i].length; j++){
                        deplacements[i][j] = direction(personnage.getPosition(), new Position(i, j));
                    }
                }
                return choixDeplacementAleatoire(deplacements, probas);

            }
        }
        private Deplacement choixDeplacementAleatoire(Deplacement[][] deplacements,double[][] probas){//faux il faut prendre en compte les probas
            var choix = Math.random();
            double somme = 0.0;
            for(int i = 0; i < probas.length; i++){
                for(int j = 0; j < probas[i].length; j++){
                    somme += probas[i][j];
                    if(choix < somme){
                        return deplacements[i][j];
                    }
                }
            }
            return deplacements[deplacements.length-1][deplacements[0].length-1];
        }

    }
