package simulation.comportement.reseau_neurones;

public class Sigmoide implements FonctionActivation{
    @Override
    public double evaluate(double value) {
        return 1/(1+Math.exp(-value));
    }

    @Override
    public double evaluateDer(double value) {
        return this.evaluate(value)-Math.pow(this.evaluate(value), 2);
    }
}
