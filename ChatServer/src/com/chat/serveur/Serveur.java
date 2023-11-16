package com.chat.serveur;

import com.chat.commun.evenement.Evenement;
import com.chat.commun.evenement.EvenementUtil;
import com.chat.commun.evenement.GestionnaireEvenement;
import com.chat.commun.net.Connexion;
import com.chat.commun.thread.Lecteur;
import com.chat.commun.thread.ThreadEcouteurDeTexte;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ListIterator;
import java.util.Vector;

/**
 * Cette classe repr�sente un serveur sur lequel des clients peuvent se connecter.
 *
 * @author Abdelmoum�ne Toudeft (Abdelmoumene.Toudeft@etsmtl.ca)
 * @version 1.0
 * @since 2023-09-01
 */
public class Serveur implements Lecteur {

    //Liste des connect�s au serveur :
    protected final Vector<Connexion> connectes = new Vector<>();

    //Nouveaux clients qui ne se sont pas encore "identifi�s":
    private final Vector<Connexion> nouveaux = new Vector<>();
    //Ce thread s'occupe d'interagir avec les nouveaux pour valider leur connexion :
    private Thread threadNouveaux;
    private int port = 8888;
    //Thred qui attend de nouvelles connexions :
    private ThreadEcouteurDeConnexions ecouteurConnexions;
    //Thread qui �coute l'arriv�e de texte des clients connect�s :
    private ThreadEcouteurDeTexte ecouteurTexte;
    //Le serveur-socket utilis� par le serveur pour attendre que les clients se connectent :
    private ServerSocket serverSocket;
    //Indique si le serveur est d�j� d�marr� ou non :
    private boolean demarre;
    //�couteur qui g�re les �v�nements correspondant � l'arriv�e de texte de clients :
    protected GestionnaireEvenement gestionnaireEvenementServeur;

    /**
     * Cr�e un serveur qui va �couter sur le port sp�cifi�.
     *
     * @param port int Port d'�coute du serveur
     */
    public Serveur(int port) {
        this.port = port;
    }

    /**
     * D�marre le serveur, s'il n'a pas d�j� �t� d�marr�. D�marre le thread qui �coute l'arriv�e de clients et le
     * qui �coute l'arriv�e de texte. Mets en place le gestionnaire des �v�nements du serveur.
     *
     * @return boolean true, si le serveur a �t� d�marr� correctement, false, si le serveur a d�j� �t� d�marr� ou si
     */
    public boolean demarrer() {
        if (demarre) //Serveur deja demarre.
            return false;
        try {
            serverSocket = new ServerSocket(port);
            ecouteurConnexions = new ThreadEcouteurDeConnexions(this);
            ecouteurConnexions.start();
            ecouteurTexte = new ThreadEcouteurDeTexte(this);
            ecouteurTexte.start();
            gestionnaireEvenementServeur = new GestionnaireEvenementServeur(this);
            demarre = true;
            return true;
        } catch (IOException e) {
            System.out.println("serveurSocket erreur : " + e.getMessage());
        }
        return false;
    }

    /**
     * Arr�te le serveur en arr�tant les threads qui �coutent l'arriv�e de client, l'arriv�e de texte et le traitement
     * des nouveaux clients.
     */
    public void arreter() {
        ListIterator<Connexion> iterateur;
        Connexion cnx;

        if (!demarre)
            return;
        ecouteurConnexions.interrupt();
        ecouteurTexte.interrupt();
        if (threadNouveaux!=null) threadNouveaux.interrupt();
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("serveurSocket erreur : " + e.getMessage());
        }
        //On ferme toutes les connexions apr�s avoir envoer "END." � chacun des clients :
        iterateur = connectes.listIterator();
        while (iterateur.hasNext()) {
            cnx = iterateur.next();
            cnx.envoyer("END.");
            cnx.close();
        }
        demarre = false;
    }

    /**
     * Cette m�thode bloque sur le ServerSocket du serveur jusqu'� ce qu'un client s'y connecte. Dans ce cas, elle
     * cr�e la connexion vers ce client et l'ajoute � la liste des nouveaux connect�s.
     */
    public void attendConnexion() {
        try {
            Socket sock = serverSocket.accept();
            Connexion cnx = new Connexion(sock);
            nouveaux.add(cnx);
            System.out.println("Nouveau connecte");
            cnx.envoyer("WAIT_FOR alias");
            if (threadNouveaux == null) {
                threadNouveaux = new Thread() {
                    @Override
                    public void run() {
                        int i;
                        Connexion connexion;
                        ListIterator<Connexion> it;
                        boolean verifOK = true;
                        String hist;

                        while (!interrupted()) {
                            it = Serveur.this.nouveaux.listIterator();
                            while (it.hasNext()) {
                                connexion = it.next();

                                //V�rifier ici si le client s'est bien identifi�, si n�cessaire
                                verifOK = validerConnexion(connexion);
                                if (verifOK) {
                                    it.remove();
                                    Serveur.this.ajouter(connexion);
                                }
                            }
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }
                };
                threadNouveaux.start();
            }
        } catch (IOException e) {

        }
    }

    /**
     * Valide l'arriv�e d'un nouveau client sur le serveur. Cette impl�mentation
     * par d�faut valide automatiquement le client en retournant true.
     * Cette m�thode sera red�finie dans les classes filles, comme ServerChat,
     * pour impl�menter une validation en fonction des besoins de l'application.
     * Par exemple, ServerChat va v�rifier si le nouveau client a fourni un
     * alias valide.
     *
     * @param connexion Connexion la connexion repr�sentant le client.
     * @return boolean true.
     */
    protected boolean validerConnexion(Connexion connexion) {
        return true;
    }
    /**
     * Ajoute la connexion d'un nouveau client � la liste des connect�s.
     * @param connexion Connexion la connexion repr�sentant le client
     * @return boolean true, si l'ajout a �t� effectu� avec succ�s, false, sinon
     */
    public synchronized boolean ajouter(Connexion connexion) {
        System.out.println(connexion.getAlias()+" est arriv�!");
        boolean res = this.connectes.add(connexion);
        return res;
    }

    public synchronized boolean enlever(Connexion connexion) {
        System.out.println(connexion.getAlias()+" est parti!");
        boolean res = this.connectes.remove(connexion);
        return res;
    }
    /**
     * Cette m�thode scanne tous les clients actuellement connect�s � ce serveur pour v�rifie s'il y a du texte qui
     * arrive. Pour chaque texte qui arrive, elle cr�e un �v�nement contenant les donn�es du texte et demande au
     * gestionnaire d'�v�nement serveur de traiter l'�v�nement.
     */
    public synchronized void lire() {
        ListIterator<Connexion> iterateur = connectes.listIterator();
        Connexion cnx;
        String[] t;
        Evenement evenement;
        for (int i=0;i<connectes.size();i++) {
            cnx = connectes.get(i);
            String texte = cnx.getAvailableText();
            if (!"".equals(texte)) {
                t = EvenementUtil.extraireInfosEvenement(texte);
                evenement = new Evenement(cnx, t[0], t[1]);
                gestionnaireEvenementServeur.traiter(evenement);
            }
        }
    }

    /**
     * Retourne le port d'�coute de ce serveur
     *
     * @return int Le port d'�coute
     */
    public int getPort() {
        return port;
    }

    /**
     * Sp�cifie le port d'�coute du serveur.
     *
     * @param port int Le port d'�coute
     */
    public void setPort(int port) {
        this.port = port;
    }
    /**
     * Indique si le serveur a �t� d�marr�.
     *
     * @return boolean true si le serveur est d�marr� et false sinon
     */
    public boolean isDemarre() {
        return demarre;
    }
}