package com.chat.serveur;

import com.chat.serveur.Serveur;

/**
 * Cette classe permet de cr�er des threads capables d'�couter continuellement sur un objet de type Serveur
 * l'arriv�e de nouveaux clients.
 *
 * @author Abdelmoum�ne Toudeft (Abdelmoumene.Toudeft@etsmtl.ca)
 * @version 1.0
 * @since 2023-09-01
 */
public class ThreadEcouteurDeConnexions extends Thread {

    Serveur serveur;

    /**
     * Construit un thread sur un serveur.
     *
     * @param s Serveur Le serveur sur lequel le thread va �couter.
     */
    public ThreadEcouteurDeConnexions(Serveur s) {
        serveur = s;
    }

    /**
     * M�thode principale du thread. Cette m�thode appelle continuellement la m�thode attendConnexion() du serveur.
     */
    public void run() {
        while (!interrupted()) {
            serveur.attendConnexion();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
