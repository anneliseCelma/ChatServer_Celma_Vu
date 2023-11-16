package com.echecs.pieces;

import com.echecs.Position;

public class Cavalier extends Piece{
    public Cavalier(char color){
        super(color);
    }
    @Override
    public boolean peutSeDeplacer(Position pos1, Position pos2, Piece[][] echiquier) {
        int posVert = Math.abs(pos1.getLigne() - pos2.getLigne());
        int posHori = Math.abs(pos1.getColonne() - pos2.getColonne());

        if(posVert == 2 && posHori == 1 || posVert == 1 && posHori == 2)
            return true;

        return false;
    }
}
