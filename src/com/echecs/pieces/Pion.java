package com.echecs.pieces;

import com.echecs.PartieEchecs;
import com.echecs.Position;

import static com.echecs.util.EchecsUtil.indiceColonne;
import static com.echecs.util.EchecsUtil.indiceLigne;

public class Pion extends Piece{
    private PartieEchecs partie;
    public Pion(char color) {
        super(color);
    }

    @Override
    public boolean peutSeDeplacer(Position pos1, Position pos2, Piece[][] echiquier) {
        if(pos1.estSurLaMemeColonneQue(pos2)){
            if(echiquier[indiceLigne(pos1)+1][indiceColonne(pos1)] != null)
                return false;
            return true;
        }
        if(indiceLigne(pos2) == 7 || indiceLigne(pos2) == 0)
            echiquier[indiceLigne(pos2)][indiceColonne(pos2)] = new Dame(partie.getCouleurJoueur1());
        return false;
    }
}
