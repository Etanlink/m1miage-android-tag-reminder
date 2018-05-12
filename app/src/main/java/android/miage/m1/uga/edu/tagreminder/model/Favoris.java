package android.miage.m1.uga.edu.tagreminder.model;

import java.io.Serializable;

public class Favoris implements Serializable {

    private Arret arret;
    private LigneTransport ligne;
    private String direction;

    public Favoris(Arret arret, LigneTransport ligne, String direction) {
        this.arret = arret;
        this.ligne = ligne;
        this.direction = direction;
    }

    /* GETTER */
    public Arret getArret() { return arret; }
    public LigneTransport getLigne() { return ligne; }
    public String getDirection() { return direction; }

    /* SETTER */
    public void setArret(Arret arret) { this.arret = arret; }
    public void setLigne(LigneTransport ligne) { this.ligne = ligne; }
    public void setDirection(String direction) { this.direction = direction; }

    @Override
    public String toString() {
        return "Favoris : " +
                arret.getName() + " " +
                ligne.getShortName() + " " +
                direction;
    }
}
