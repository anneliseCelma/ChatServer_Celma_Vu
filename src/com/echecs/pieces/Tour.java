package com.echecs.pieces;

import com.echecs.Position;
import com.echecs.util.EchecsUtil;

import static com.echecs.util.EchecsUtil.*;

public class Tour extends Piece{
	public Tour(char color){
		super(color);
	}

	@Override
	public boolean peutSeDeplacer(Position pos1, Position pos2, Piece[][] echiquier) {
		
		if (pos1.estSurLaMemeLigneQue(pos2)) {
	        if (pos1.getColonne() - pos2.getColonne() > 0) {// vers la gauche
	            int cptr = pos1.getColonne() - pos2.getColonne();
	            for (int i = 1; i < cptr; i++) {
	                if (echiquier[pos1.getLigne()-1][indiceColonne((char) (pos1.getColonne() - i))] != null) {
	                    System.out.println("Il y a une pièce dans le chemin");
	                    return false;
	                }
	            }
	            return true;
	        }
	        if (pos1.getColonne() - pos2.getColonne() < 0) {// vers la droite
	            int cptr = pos2.getColonne() - pos1.getColonne();
	            for (int i = 1; i < cptr; i++) {
	                if (echiquier[pos1.getLigne()-1][indiceColonne((char) (pos1.getColonne() + i))] != null) {
	                    System.out.println("Il y a une pièce dans le chemin");
	                    return false;
	                }
	            }
	            return true;
	        }
	    }

	    if (pos1.estSurLaMemeColonneQue(pos2)) {
	        if (pos1.getLigne() - pos2.getLigne() > 0) {// vers le haut
	            int cptr = pos1.getLigne() - pos2.getLigne();
	            for (int i = 1; i < cptr; i++) {
	                if (echiquier[pos1.getLigne() - i-1][indiceColonne(pos1)] != null) {
	                    System.out.println("Il y a une pièce dans le chemin");
	                    return false;
	                }
	            }
	            return true;
	        }
	        if (pos1.getLigne() - pos2.getLigne() < 0) {// vers le bas
	            int cptr = pos2.getLigne() - pos1.getLigne();
	            for (int i = 1; i < cptr; i++) {
	                if (echiquier[pos1.getLigne() + i-1][indiceColonne(pos1)] != null) {
	                    System.out.println("Il y a une pièce dans le chemin");
	                    return false;
	                }
	            }
	            return true;
	        }
	    }

	    return false;
	}
}
