package com.echecs.pieces;

import com.echecs.Position;

public class Roi extends Piece{
    public Roi(char color){
        super(color);
    }
    @Override
    public boolean peutSeDeplacer(Position pos1, Position pos2, Piece[][] echiquier) {
        if(pos1.estVoisineDe(pos2))
            return true;
        return false;
    }
}
