package com.echecs.pieces;

import com.echecs.Position;
import com.echecs.util.EchecsUtil;

import static com.echecs.util.EchecsUtil.*;

public class Fou extends Piece{
    public Fou(char color){
        super(color);
    }
    @Override
    public boolean peutSeDeplacer(Position pos1, Position pos2, Piece[][] echiquier) {
    	 if (pos1.estSurLaMemeDiagonaleQue(pos2)) {
    	        int diffLig = Math.abs(pos1.getLigne() - pos2.getLigne());
    	        int diffCol = Math.abs(pos1.getColonne() - pos2.getColonne());

    	        int ligDir = Integer.compare(pos2.getLigne(), pos1.getLigne());
    	        int colDir = Integer.compare(pos2.getColonne(), pos1.getColonne());

    	        for (int i = 1; i < diffLig || i < diffCol; i++) {
    	        	
    	            int ligInter = (pos1.getLigne()-1) + (i * ligDir);
    	            int colInter = indiceColonne(pos1.getColonne()) + (i * colDir);
    	            
    	            System.out.println(ligInter + " " + colInter + " DIR" + ligInter + " "+ colInter);
    	            if (ligInter < 0 || ligInter >= 8 || colInter < 0 || colInter >= 8) {
    	                return false;
    	            }

    	            if (echiquier[ligInter][colInter] != null) {
    	                System.out.println("Il y a une pièce dans le chemin");
    	                return false;
    	            }
    	        }
    	        return true;
    	    }
        return false;
    }
}
