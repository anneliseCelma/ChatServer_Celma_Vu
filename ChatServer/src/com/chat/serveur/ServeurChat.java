package com.chat.serveur;

import com.chat.commun.net.Connexion;

import java.util.ArrayList;
import java.util.List; 
import java.util.Vector;


/**
 * Cette classe �tend (h�rite) la classe abstraite Serveur et y ajoute le n�cessaire pour que le
 * serveur soit un serveur de chat.
 *
 * @author Abdelmoum�ne Toudeft (Abdelmoumene.Toudeft@etsmtl.ca)
 * @version 1.0
 * @since 2023-09-15
 */
public class ServeurChat extends Serveur {

	private Vector<String> historique= new Vector<>();
	private List<Invitation> Invitations =new ArrayList<>();
	private List<SalonPrive> salonsPrives = new ArrayList<>();
	/**
	 * Cr�e un serveur de chat qui va �couter sur le port sp�cifi�.
	 *
	 * @param port int Port d'�coute du serveur
	 */
	public ServeurChat(int port) {
		super(port);
	}


	@Override
	public synchronized boolean ajouter(Connexion connexion) {
		String hist = this.historique();
		if ("".equals(hist)) {
			connexion.envoyer("OK");
		}
		else {
			connexion.envoyer("HIST " + hist);
		}
		return super.ajouter(connexion);
	}

	public void envoyerATousSauf(String str, String aliasExpediteur , Connexion connexion) {
		for (Connexion cnx:connectes) {
			if (!cnx.getAlias().equals(aliasExpediteur)) {
				cnx.envoyer(aliasExpediteur+">>"+str);
			}
		}
	}

	public void ajouterHistorique(String message, String aliasExpediteur) {
		String aliasEtMessage= message+">>"+aliasExpediteur;
		historique.add(aliasEtMessage);
	}


	public void envoyerInvitation(String aliasHost, String aliasInvite, Connexion connexion) {
		if (!aliasHost.equals(aliasInvite)) {
			for (Connexion cnx : connectes) {	
				if (cnx.getAlias().equals(aliasInvite)) {	 
					cnx.envoyer(aliasHost+" vous invite en privée");
					break;
				}
			}
		}
	}

	public void envoyerEchec(String aliasHost, String aliasInvite, Connexion connexion) {
		if (!aliasHost.equals(aliasInvite)) {
			for (Connexion cnx : connectes) {	
				if (cnx.getAlias().equals(aliasInvite)) {	 
					cnx.envoyer(aliasHost+" vous invite a jouer au échec");
					break;
				}
			}
		}
	}
	
	public void envoyerMove(String move) {	 
		for (Connexion cnx:connectes) {
//				cnx.envoyer(move);
			cnx.envoyer("MOVE "+move);
			}
		}

	public List<Invitation>getInvitations(){
		return Invitations;
	}

	public void addInvitation(Invitation invitation) {
		Invitations.add(invitation);
	}

	public Invitation findInvitation(String alias1, String alias2) {
		for (Invitation invitation : Invitations) {
			if (invitation.getHost().equals(alias1) || invitation.getHost().equals(alias2) && invitation.getInvite().equals(alias2)|| invitation.getInvite().equals(alias1) ){
				return invitation;
			}
		}
		return null;
	}

	public void declineOrCancelInvitation(Invitation invitation, String alias1,String alias2,Connexion connexion) {

		if (invitation.getHost().equals(alias1)) {
			Invitations.remove(invitation);
			for (Connexion cnx : connectes) {
				if (cnx.getAlias().equals(alias2)) {
					cnx.envoyer( "annulation de l'invitation par "+alias1);
					break;
				}
			}
		}
		else if(invitation.getHost().equals(alias2)){

			Invitations.remove(invitation);
			for (Connexion cnx : connectes) {
				if (cnx.getAlias().equals(alias2)) {
					cnx.envoyer(alias1+" a refusé votre invitation");
					break;
				}
			}

		}
	}

	public void cancelInvitation (Invitation invitation, String aliasInvite,String aliasHost, Connexion connexion) {
		Invitations.remove(invitation);
		for (Connexion cnx : connectes) {
			if (cnx.getAlias().equals(aliasHost)) {
				cnx.envoyer(aliasInvite+" annulation de l'invitation");
				break;
			}
		}
	}

	public String getInvitationsRecues(String aliasDemandeur) {
		StringBuilder invitationsList = new StringBuilder();
		for (Invitation invitation : Invitations) {
			if (invitation.getInvite().equals(aliasDemandeur)) {
				invitationsList.append(invitation.getHost()).append(":");
			}
		}
		return invitationsList.toString();
	}


	public List<SalonPrive>getSalonsPrives(){
		return salonsPrives;
	}

	public void addSalonPrive(String aliasHost, String aliasInvite) {

		SalonPrive salonPrive =new SalonPrive(aliasHost,aliasInvite);
		salonsPrives.add(salonPrive);
		for (Connexion cnx : connectes) {
			if (cnx.getAlias().equals(aliasHost)) {
				cnx.envoyer( aliasInvite+" a rejoins le salon ");
				break;
			}
		}
	}

	public SalonPrive findSalonPrive(String alias1, String alias2) {
		for (SalonPrive salon : salonsPrives) { 
			if ((salon.getHost().equals(alias1) && salon.getInvite().equals(alias2)) ||
					(salon.getHost().equals(alias2) && salon.getInvite().equals(alias1))) {

				return salon;
			}
		}
		return null; 
	}

	public void envoyerMessagePrive(String aliasHost, String aliasInvite, String prvmessage) {

		if (!aliasHost.equals(aliasInvite)) {
			for (Connexion cnx : connectes) {

				if (cnx.getAlias().equals(aliasInvite)) {

					cnx.envoyer("PRV "+prvmessage);
					break;
				}
			}
		}
	}

	public void closeSalonPrive(String aliasDemandeur, String aliasCible) {
		SalonPrive salon = findSalonPrive(aliasDemandeur, aliasCible);
		if (salon != null) {
			removeSalonPrive(salon, aliasDemandeur, aliasCible);
		}
	}


	public void removeSalonPrive(SalonPrive salonPrive, String aliasDemandeur, String aliasCible) {
		salonsPrives.remove(salonPrive);
		for (Connexion cnx : connectes) {
			if ( cnx.getAlias().equals(aliasCible)) {
				cnx.envoyer(aliasDemandeur + " a quitté le salon privé.");
				break;
			}	
		}
	}

	/**
	 * Valide l'arriv�e d'un nouveau client sur le serveur. Cette red�finition
	 * de la m�thode h�rit�e de Serveur v�rifie si le nouveau client a envoy�
	 * un alias compos� uniquement des caract�res a-z, A-Z, 0-9, - et _.
	 *
	 * @param connexion Connexion la connexion repr�sentant le client
	 * @return boolean true, si le client a valid� correctement son arriv�e, false, sinon
	 */
	@Override
	protected boolean validerConnexion(Connexion connexion) {

		String texte = connexion.getAvailableText().trim();
		char c;
		int taille;
		boolean res = true;
		if ("".equals(texte)) {
			return false;
		}
		taille = texte.length();
		for (int i=0;i<taille;i++) {
			c = texte.charAt(i);
			if ((c<'a' || c>'z') && (c<'A' || c>'Z') && (c<'0' || c>'9')
					&& c!='_' && c!='-') {
				res = false;
				break;
			}
		}
		if (!res)
			return false;
		for (Connexion cnx:connectes) {
			if (texte.equalsIgnoreCase(cnx.getAlias())) { //alias d�j� utilis�
				res = false;
				break;
			}
		}
		connexion.setAlias(texte);
		return true;
	}

	/**
	 * Retourne la liste des alias des connect�s au serveur dans une cha�ne de caract�res.
	 *
	 * @return String cha�ne de caract�res contenant la liste des alias des membres connect�s sous la
	 * forme alias1:alias2:alias3 ...
	 */
	public String list() {
		String s = "";
		for (Connexion cnx:connectes)
			s+=cnx.getAlias()+":";
		return s;
	}
	/**
	 * Retourne la liste des messages de l'historique de chat dans une cha�ne
	 * de caract�res.
	 *
	 * @return String cha�ne de caract�res contenant la liste des alias des membres connect�s sous la
	 * forme message1\nmessage2\nmessage3 ...
	 */
	public String historique() {
		StringBuilder historiqueList= new StringBuilder();
		for (String message : historique) {
			historiqueList.append(message+"\n");
		}
		//String s = "";
		return historiqueList.toString() ;

	}
}