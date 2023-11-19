package com.echecs;

import com.echecs.pieces.*; 
import com.echecs.util.EchecsUtil;

import static com.echecs.util.EchecsUtil.*;

import java.util.Random;

/**
 * Représente une partie de jeu d'échecs. Orcheste le déroulement d'une partie :
 * déplacement des pièces, vérification d'échec, d'échec et mat,...
 *
 * @author Abdelmoumène Toudeft (Abdelmoumene.Toudeft@etsmtl.ca)
 * @version 1.0
 * @since 2023-09-01
 */
public class PartieEchecs {
	/**
	 * Grille du jeu d'échecs. La ligne 0 de la grille correspond à la ligne
	 * 8 de l'échiquier. La colonne 0 de la grille correspond à la colonne a
	 * de l'échiquier.
	 */
	private Piece[][] echiquier;
	private String aliasJoueur1, aliasJoueur2;
	private char couleurJoueur1, couleurJoueur2;

	private Roi roi;
	private boolean roque = true;
	/**
	 * La couleur de celui à qui c'est le tour de jouer (n ou b).
	 */
	private char tour = 'b'; //Les blancs commencent toujours

	/**
	 * Crée un échiquier de jeu d'échecs avec les pièces dans leurs positions
	 * initiales de début de partie.
	 * Répartit au hasard les couleurs n et b entre les 2 joueurs.
	 */
	public PartieEchecs() {
		tour = 'b';
		echiquier = new Piece[8][8];
		//Placement des pièces :
		echiquier[7][0] = new Tour('b');
		echiquier[7][7] = new Tour('b');
		echiquier[0][0] = new Tour('n');
		echiquier[0][7] = new Tour('n');

		for (int i = 0; i < echiquier[6].length; i++)
			echiquier[6][i] = new Pion('b');

		for (int i = 0; i < echiquier[1].length; i++)
			echiquier[1][i] = new Pion('n');

		echiquier[0][1] = new Cavalier('n');
		echiquier[0][6] = new Cavalier('n');
		echiquier[7][1] = new Cavalier('b');
		echiquier[7][6] = new Cavalier('b');

		echiquier[0][2] = new Fou('n');
		echiquier[0][5] = new Fou('n');
		echiquier[7][2] = new Fou('b');
		echiquier[7][5] = new Fou('b');

		echiquier[0][3] = new Dame('n');
		echiquier[7][3] = new Dame('b');

		echiquier[0][4] = new Roi('n');
		echiquier[7][4] = new Roi('b');

		Random random = new Random();
		if(random.nextBoolean())
			this.couleurJoueur1 = 'b';
		else
			this.couleurJoueur1 = 'n';

		if(couleurJoueur1 == 'b')
			this.couleurJoueur2 = 'n';
		else
			this.couleurJoueur2 = 'b';
		
	}

	/**
	 * Change la main du jeu (de n à b ou de b à n).
	 */
	public void changerTour() {
		if (tour == 'b')
			tour = 'n';
		else
			tour = 'b';
	}

	public Piece[][] getEtat() {
		return echiquier;
	}

	/**
	 * Tente de déplacer une pièce d'une position à une autre sur l'échiquier.
	 * Le déplacement peut échouer pour plusieurs raisons, selon les règles du
	 * jeu d'échecs. Par exemples :
	 * Une des positions n'existe pas;
	 * Il n'y a pas de pièce à la position initiale;
	 * La pièce de la position initiale ne peut pas faire le mouvement;
	 * Le déplacement met en échec le roi de la même couleur que la pièce.
	 *
	 * @param initiale Position la position initiale
	 * @param finale   Position la position finale
	 * @return boolean true, si le déplacement a été effectué avec succès, false sinon
	 */
	public boolean deplace(Position initiale, Position finale) {

		if (!positionValide(initiale) || !positionValide(finale)) {
			System.out.println("Pos invalide");
			return false;
		}

		Piece piece = echiquier[initiale.getLigne()-1][indiceColonne(initiale)];

		if (piece == null) {
			System.out.println("Piece null");
			return false;
		}
		if (piece.getCouleur() != getTour()) {
			System.out.println("Tour null");
			return false;
		}
		if(echiquier[finale.getLigne()-1][indiceColonne(finale)] != null) {
			if (echiquier[finale.getLigne()-1][indiceColonne(finale)].getCouleur() == getTour()) {
				System.out.println("Tour couleur");
				return false;
			}
		}
		if(estEnEchec() == getTour()) {
			System.out.println("ON est entrer dans echec");
			Piece initTemp;
			Piece finaleTemp;
			if(piece.peutSeDeplacer(initiale, finale, echiquier)) {
				initTemp = echiquier[initiale.getLigne()-1][indiceColonne(initiale)];
				finaleTemp = echiquier[finale.getLigne()-1][indiceColonne(finale)];

				echiquier[finale.getLigne()-1][indiceColonne(finale)] = piece;
				echiquier[initiale.getLigne()-1][indiceColonne(initiale)] = null;

				if(estEnEchec() == getTour()) {
					echiquier[initiale.getLigne()-1][indiceColonne(initiale)] = initTemp;
					echiquier[finale.getLigne()-1][indiceColonne(finale)] = finaleTemp;
					return false;
				}

			}

		}

		if (piece.peutSeDeplacer(initiale, finale, echiquier)) {
			Piece initTemp;
			Piece finaleTemp;

			initTemp = echiquier[initiale.getLigne()-1][indiceColonne(initiale)];
			finaleTemp = echiquier[finale.getLigne()-1][indiceColonne(finale)];

			echiquier[finale.getLigne()-1][indiceColonne(finale)] = piece;
			echiquier[initiale.getLigne()-1][indiceColonne(initiale)] = null;



			System.out.println("se deplace !!");
			if (piece.equals(roi))
				roque = false;
			return true;
		}
		else 
			System.out.println("movement invalide");

		return false;
	}

	/**
	 * Vérifie si un roi est en échec et, si oui, retourne sa couleur sous forme
	 * d'un caractère n ou b.
	 * Si la couleur du roi en échec est la même que celle de la dernière pièce
	 * déplacée, le dernier déplacement doit être annulé.
	 * Les 2 rois peuvent être en échec en même temps. Dans ce cas, la méthode doit
	 * retourner la couleur de la pièce qui a été déplacée en dernier car ce
	 * déplacement doit être annulé.
	 *
	 * @return char Le caractère n, si le roi noir est en échec, le caractère b,
	 * si le roi blanc est en échec, tout autre caractère, sinon.
	 */
	public char estEnEchec( ) {
		Piece piece;
		Position posRoiNoir = null;
		Position posRoiBlanc = null;
		boolean roiNoirEchec = false;
		boolean roiBlancEchec = false;

		for (int i = 0; i < echiquier.length; i++)
			for (int j = 0; j < echiquier[i].length; j++) {
				piece = echiquier[i][j];
				if (piece instanceof Roi) {
					if (piece.getCouleur() == 'n') {
						posRoiNoir = getPosition((byte) i, (byte) j);
					}
					if (piece.getCouleur() == 'b') {
						posRoiBlanc = getPosition((byte) i, (byte) j);
					}


				}
			}

		if (!roiNoirEchec) {
			for (int i = 0; i < echiquier.length; i++) {
				for (int j = 0; j < echiquier[i].length; j++) {
					piece = echiquier[i][j];
					if (piece != null && piece.getCouleur() == 'b' && !(piece instanceof Pion)) {
						Position positionPiece = EchecsUtil.getPosition((byte) i, (byte) j);
						System.out.println(positionPiece.getLigne() + ":" + positionPiece.getColonne());
						if (piece.peutSeDeplacer(positionPiece, posRoiNoir, echiquier) && !roiNoirEchec) {
							System.out.println("AUTO NOIR ECHEC");
							roiNoirEchec = true;
						}
					}
				}
			}
		}

		if (!roiBlancEchec) {
			for (int i = 0; i < echiquier.length; i++) {
				for (int j = 0; j < echiquier[i].length; j++) {
					piece = echiquier[i][j];
					if (piece != null && piece.getCouleur() == 'n' && !(piece instanceof Pion)) {
						Position positionPiece = EchecsUtil.getPosition((byte) i, (byte) j);
						if (piece.peutSeDeplacer(positionPiece, posRoiBlanc, echiquier) && !roiBlancEchec) {
							System.out.println("BLANC");
							roiBlancEchec = true;
						}
					}
				}
			}
		}

		if (roiNoirEchec)
			return 'n';
		if (roiBlancEchec)
			return 'b';
		return 0;
	}

	/**
	 * Retourne la couleur n ou b du joueur qui a la main.
	 *
	 * @return char la couleur du joueur à qui c'est le tour de jouer.
	 */
	public char getTour() {
		return tour;
	}

	/**
	 * Retourne l'alias du premier joueur.
	 *
	 * @return String alias du premier joueur.
	 */
	public String getAliasJoueur1() {
		return aliasJoueur1;
	}

	/**
	 * Modifie l'alias du premier joueur.
	 *
	 * @param aliasJoueur1 String nouvel alias du premier joueur.
	 */
	public void setAliasJoueur1(String aliasJoueur1) {
		this.aliasJoueur1 = aliasJoueur1;
	}

	/**
	 * Retourne l'alias du deuxième joueur.
	 *
	 * @return String alias du deuxième joueur.
	 */
	public String getAliasJoueur2() {
		return aliasJoueur2;
	}

	/**
	 * Modifie l'alias du deuxième joueur.
	 *
	 * @param aliasJoueur2 String nouvel alias du deuxième joueur.
	 */
	public void setAliasJoueur2(String aliasJoueur2) {
		this.aliasJoueur2 = aliasJoueur2;
	}

	/**
	 * Retourne la couleur n ou b du premier joueur.
	 *
	 * @return char couleur du premier joueur.
	 */
	public char getCouleurJoueur1() {
		return couleurJoueur1;
	}

	/**
	 * Retourne la couleur n ou b du deuxième joueur.
	 *
	 * @return char couleur du deuxième joueur.
	 */
	public char getCouleurJoueur2() {
		return couleurJoueur2;
	}
}