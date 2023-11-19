package com.echecs.pieces;

import com.echecs.PartieEchecs;
import com.echecs.Position;

import static com.echecs.util.EchecsUtil.indiceColonne;
import static com.echecs.util.EchecsUtil.indiceLigne;

public class Pion extends Piece{
	private boolean deplaceDeux = true;
	public Pion(char color) {
		super(color);
	}

	@Override
	public boolean peutSeDeplacer(Position pos1, Position pos2, Piece[][] echiquier) {
			
			if(pos1.estSurLaMemeColonneQue(pos2)){
				if(pos1.getLigne() - pos2.getLigne() == 1) {
					if(echiquier[pos1.getLigne()-1][indiceColonne(pos1)] == null) {
						deplaceDeux = false;
						return true;
					}
				}
				if(pos1.getLigne() - pos2.getLigne() == 2 && deplaceDeux) {
					if(echiquier[pos1.getLigne()-1][indiceColonne(pos1)] == null && echiquier[pos1.getLigne()-2][indiceColonne(pos1)] == null) {
						deplaceDeux = false;
						return true;
					}
				}
			}

			if(pos2.getLigne() == 0)
				echiquier[indiceLigne(pos2)][indiceColonne(pos2)] = new Dame(couleur);

		return false;
	}
}
