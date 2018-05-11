package android.miage.m1.uga.edu.tagreminder.model;

import java.io.Serializable;

public class Favoris implements Serializable {

    private Arret arret;
    private LigneTransport ligne;
    private int direction;

    public Favoris(Arret arret, LigneTransport ligne, int direction) {
        this.arret = arret;
        this.ligne = ligne;
        this.direction = direction;
    }

    /* GETTER */
    public Arret getArret() { return arret; }
    public LigneTransport getLigne() { return ligne; }
    public int getDirection() { return direction; }

    /* SETTER */
    public void setArret(Arret arret) { this.arret = arret; }
    public void setLigne(LigneTransport ligne) { this.ligne = ligne; }
    public void setDirection(int direction) { this.direction = direction; }

    @Override
    public String toString() {
        return "Favoris{" +
                "arret=" + arret +
                ", ligne=" + ligne +
                ", direction=" + direction +
                '}';
    }
}
