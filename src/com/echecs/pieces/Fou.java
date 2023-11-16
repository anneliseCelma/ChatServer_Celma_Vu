package com.echecs.pieces;

import com.echecs.Position;

public class Fou extends Piece{
    public Fou(char color){
        super(color);
    }
    @Override
    public boolean peutSeDeplacer(Position pos1, Position pos2, Piece[][] echiquier) {
        if(pos1.estSurLaMemeDiagonaleQue(pos2))
            return true;
        return false;
    }
}
