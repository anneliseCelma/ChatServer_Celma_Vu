package com.chat.serveur;

import com.chat.commun.evenement.Evenement;
import com.chat.commun.evenement.GestionnaireEvenement;
import com.chat.commun.net.Connexion;

import com.chat.serveur.Invitation;
import com.chat.serveur.SalonPrive;


/**
 * Cette classe repr�sente un gestionnaire d'�v�nement d'un serveur. Lorsqu'un serveur re�oit un texte d'un client,
 * il cr�e un �v�nement � partir du texte re�u et alerte ce gestionnaire qui r�agit en g�rant l'�v�nement.
 *
 * @author Abdelmoum�ne Toudeft (Abdelmoumene.Toudeft@etsmtl.ca)
 * @version 1.0
 * @since 2023-09-01
 */
public class GestionnaireEvenementServeur implements GestionnaireEvenement {
    private Serveur serveur;
    private Invitation invitation;

    /**
     * Construit un gestionnaire d'�v�nements pour un serveur.
     *
     * @param serveur Serveur Le serveur pour lequel ce gestionnaire g�re des �v�nements
     */
    public GestionnaireEvenementServeur(Serveur serveur) {
        this.serveur = serveur;
       
    }

    /**
     * M�thode de gestion d'�v�nements. Cette m�thode contiendra le code qui g�re les r�ponses obtenues d'un client.
     *
     * @param evenement L'�v�nement � g�rer.
     */
    @Override
    public void traiter(Evenement evenement) {
        Object source = evenement.getSource();
        Connexion cnx;
        String msg, typeEvenement, aliasExpediteur;
        ServeurChat serveur = (ServeurChat) this.serveur;
       
        if (source instanceof Connexion) {
            cnx = (Connexion) source;
            System.out.println("SERVEUR-Recu : " + evenement.getType() + " " + evenement.getArgument());
            typeEvenement = evenement.getType();
            switch (typeEvenement) {
                case "EXIT": //Ferme la connexion avec le client qui a envoy� "EXIT":
                    cnx.envoyer("END");
                    serveur.enlever(cnx);
                    cnx.close();
                    break;
                case "LIST": //Envoie la liste des alias des personnes connect�es :
                    cnx.envoyer("LIST " + serveur.list());
                    break;
                case "MSG" :
                	String message=evenement.getArgument();
                	serveur.envoyerATousSauf(message,cnx.getAlias(),cnx);
                	serveur.ajouterHistorique(message, cnx.getAlias());
                //Ajoutez ici d�autres case pour g�rer d�autres commandes.
                	break;
                	
                case "JOIN":
                    //System.out.print("Bug");
                    	String aliasHost=cnx.getAlias();
                    	String aliasInvite=evenement.getArgument();
                    	//serveur.envoyerInvitation(aliasHost, aliasInvite,cnx);
                    	
                    
                    	 boolean invitationTrouvee = false;
                    	 Invitation invitationExistante = null;
                    	
                    	  for (Invitation invitation : serveur.getInvitations()) {
                    	        if (invitation.getHost().equals(aliasInvite) && invitation.getInvite().equals(aliasHost)) {
                    	            invitationTrouvee = true;
                    	            invitationExistante = invitation;
                    	            break;
                    	        }
                    	    }

                    	    if (invitationTrouvee) {
                    	      
                    	        serveur.addSalonPrive(aliasHost, aliasInvite);
                    	        serveur.cancelInvitation(invitationExistante,aliasInvite,aliasHost,cnx);
                    	    } else {
                    
                    	        Invitation nouvelleInvitation = new Invitation(aliasHost, aliasInvite);
                    	        serveur.addInvitation(nouvelleInvitation);
                    	        serveur.envoyerInvitation(aliasHost, aliasInvite, cnx);
                    	    }   
                    	    
                    	
                    	//serveur.ajouterInvitation(aliasHost,aliasInvite);
                    	//serveur.creerSalonPrive(aliasHost,aliasInvite);
                    	//if ("INVITE".equals(commande)) {
                    		//serveur.ajouterInvitation(aliasHost,aliasInvite);
                    		//serveur.informerInvitation(aliasHost,aliasInvite);
                    	//}else if("ACCEPT".equals(commande)) {
                    	//	serveur.creerSalonPrive(aliasHost,aliasInvite);
                    		//serveur.informerSalonPrive(aliasHost,aliasInvite);
                    	//}
                    	
                    	break;
                case "ACCEPT" :
                	aliasHost = evenement.getArgument();
                	aliasInvite=cnx.getAlias();
                	serveur.addSalonPrive(aliasHost, aliasInvite);
              
                	break;
                case "DECLINE":
                	aliasHost = evenement.getArgument();
                	aliasInvite=cnx.getAlias();
                	serveur.declineInvitation(invitation,aliasInvite, aliasHost,cnx);
                
                	break;
                	
                case "PRV":
                    aliasHost = cnx.getAlias();
                    String[] args = evenement.getArgument().split(" ", 2); 
                    if (args.length == 2) {
                        aliasInvite = args[0];
                        String prvmessage = args[1];

                       
                        SalonPrive salon = serveur.findSalonPrive(aliasHost, aliasInvite);

                        if (salon != null) {
                           
                            serveur.envoyerMessagePrive(aliasHost,aliasInvite, prvmessage);
                        } else {
                            
                            cnx.envoyer("Aucun salon privé n'existe entre vous et " + aliasInvite);
                        }
                    } else {
                        
                        cnx.envoyer("Commande PRV incorrecte. Utilisation : PRV alias2 message");
                    }
                    break;
                case "INV":
                	String aliasDemandeur=cnx.getAlias();
                	serveur.getInvitationsRecues(aliasDemandeur);
                	
                	break;
                case "QUIT":
                	aliasDemandeur = cnx.getAlias();
                	String aliasCible= evenement.getArgument();
                	//serveur.quitSalon(aliasDemandeur,aliasCible);
                	break;
                case "HIST":
                	cnx.envoyer("HIST " + serveur.historique());
                	break;

                default: //Renvoyer le texte recu convertit en majuscules :
                    msg = (evenement.getType() + " " + evenement.getArgument()).toUpperCase();
                    cnx.envoyer(msg);
            }
        }
    }
}