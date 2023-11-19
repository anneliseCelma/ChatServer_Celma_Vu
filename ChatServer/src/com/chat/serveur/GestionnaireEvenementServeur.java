package com.chat.serveur;

import com.chat.commun.evenement.Evenement; 
import com.chat.commun.evenement.GestionnaireEvenement;
import com.chat.commun.net.Connexion;

import com.chat.serveur.Invitation;
import com.chat.serveur.SalonPrive;
import com.echecs.PartieEchecs;
import com.echecs.Position;
import com.echecs.util.EchecsUtil;
import static com.echecs.util.EchecsUtil.*;

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
	private PartieEchecs echec;
	boolean jeuEchec = false;
	boolean prive = false;
	boolean boolEchec = false;
	private EtatPartieEchecs partie;
	//    private Invitation invitation;
	//    private SalonPrive salonPrive;

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

				String aliasHost=cnx.getAlias();
				String aliasInvite=evenement.getArgument();
				Invitation invitationExistante = null;

				Invitation invitationExiste=serveur.findInvitation(aliasHost, aliasInvite);
				for (Invitation invitation : serveur.getInvitations()) {
					if (invitation.getHost().equals(aliasHost) && invitation.getInvite().equals(aliasInvite)) {
						invitationExistante = invitation;
						break;
					}
				}
				if (invitationExiste!=null) {
					serveur.addSalonPrive(aliasHost,aliasInvite);
					serveur.cancelInvitation(invitationExiste, aliasInvite, aliasHost, cnx);
					prive = false;
				
				}
				else {
					Invitation nouvelleInvitation= new Invitation(aliasHost,aliasInvite);
					serveur.addInvitation(nouvelleInvitation);
					serveur.envoyerInvitation(aliasHost, aliasInvite, cnx);
					prive = true;
					
				}  	

				break;
			case "JOINOK" :
				aliasHost = evenement.getArgument();
				aliasInvite=cnx.getAlias();

				if(serveur.findSalonPrive(aliasHost, aliasInvite) == null && prive) {
					serveur.addSalonPrive(aliasHost, aliasInvite);
					jeuEchec = true;
				}


				break;
			case "CHESSOK" :
				aliasHost = evenement.getArgument();
				aliasInvite=cnx.getAlias();

				if(boolEchec) {
					if(serveur.findSalonPrive(aliasHost, aliasInvite) != null) {
						echec = new PartieEchecs();
						partie = new EtatPartieEchecs();
						serveur.envoyerMove("Jeu echec demarre");
						serveur.envoyerMove("\n" + partie.toString());
					}
				}


				break;
			case "DECLINE":
				String alias1= cnx.getAlias();
				String alias2= evenement.getArgument();
				Invitation invitation = serveur.findInvitation(alias1, alias2);
				serveur.declineOrCancelInvitation(invitation,alias1,alias2,cnx);

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

					cnx.envoyer("Commande PRV incorrecte.");
				}
				break;
			case "INV":
				String aliasDemandeur=cnx.getAlias();
				String invitations=serveur.getInvitationsRecues(aliasDemandeur);
				cnx.envoyer("INV " + invitations);

				break;
			case "QUIT":
				aliasDemandeur = cnx.getAlias();
				String aliasCible= evenement.getArgument();
				serveur.closeSalonPrive(aliasDemandeur,aliasCible);
				break;
			case "HIST":
				cnx.envoyer("HIST " + serveur.historique());
				break;

			case "ABANDON":
				if(boolEchec) {
				boolEchec = false;
				String aliasAbandon = cnx.getAlias();
				serveur.abandon(aliasAbandon);
				} else
					serveur.envoyerMove("Aucune partie d'echec trouvée");
				
				break;

			case "CHESS":
				String hostEchec = cnx.getAlias();
				String inviteEchec = evenement.getArgument();
				Invitation invitationEchec = null;
				Invitation invitationPartie = serveur.findInvitation(hostEchec, inviteEchec);

				if(serveur.findSalonPrive(hostEchec, inviteEchec) != null && prive) {
					if (invitationEchec == null) {
						serveur.envoyerEchec(hostEchec, inviteEchec, cnx);

						boolEchec = true;

					}  	

					break;
				} else 
					cnx.envoyer("Faut etre dans un salon privé pour jouer");
				break;
			case "MOVE" :
				aliasHost = evenement.getArgument();
				String aliasPOS = evenement.getArgument();

				char charInit = aliasPOS.charAt(0);
				byte intInit = (byte) Character.getNumericValue(aliasPOS.charAt(1));
				char charFin = aliasPOS.charAt(2);
				byte intFin = (byte) Character.getNumericValue(aliasPOS.charAt(3));

				System.out.println(charInit+" "+ intInit);

				if(boolEchec) {
					Position posInit = new Position(charInit, intInit);
					Position posFin = new Position(charFin,intFin);

					if(echec.deplace(posInit, posFin)) {
						serveur.envoyerMove(aliasPOS);
						echec.changerTour();

						char[][] temp1;
						temp1 = partie.getEtatPartieEchecs();
						temp1[intFin-1][charFin - 'a'] = temp1[intInit-1][charInit - 'a'];
						temp1[intInit-1][charInit - 'a'] = '.';
						serveur.envoyerMove("\n" + partie.toString());

					}
					else
						serveur.envoyerMove("INVALID");

				} else
					cnx.envoyer("Aucune partie d'echec trouvee");

				break;

			default: //Renvoyer le texte recu convertit en majuscules :
				msg = (evenement.getType() + " " + evenement.getArgument()).toUpperCase();
				cnx.envoyer(msg);
			}
		}
	}
}