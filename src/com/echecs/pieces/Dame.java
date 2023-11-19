package com.echecs.pieces;

import com.echecs.Position;

public class Dame extends Piece{
    public Dame(char color){
        super(color);
    }
    @Override
    public boolean peutSeDeplacer(Position pos1, Position pos2, Piece[][] echiquier) {
        if(pos1.estSurLaMemeColonneQue(pos2) || pos1.estSurLaMemeLigneQue(pos2) || pos1.estSurLaMemeDiagonaleQue(pos2)) {
        	
            return true;
        }
        return false;
    }
}
