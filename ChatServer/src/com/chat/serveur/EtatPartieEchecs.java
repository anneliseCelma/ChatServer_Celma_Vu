package com.chat.serveur;
//classe etat de la partie
public class EtatPartieEchecs {

	private char etatEchiquier[][];

	public EtatPartieEchecs(){
		this.etatEchiquier=new char[8][8];
		char[][] etatEchiquier = {
		        {'t', 'c', 'f', 'd', 'r', 'f', 'c', 't'},
		        {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
		        {'.', '.', '.', '.', '.', '.', '.', '.'},
		        {'.', '.', '.', '.', '.', '.', '.', '.'},
		        {'.', '.', '.', '.', '.', '.', '.', '.'},
		        {'.', '.', '.', '.', '.', '.', '.', '.'},
		        {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
		        {'T', 'C', 'F', 'D', 'R', 'F', 'C', 'T'}
		    };
		
		this.etatEchiquier = etatEchiquier;

	}

	public char[][] getEtatPartieEchecs() {
		return etatEchiquier;
	}

	public void setEtatEtatEchiquier(char[][] etatEchiquier) {
		this.etatEchiquier=new char[8][8];
	}



	@Override
	public String toString() {
		StringBuilder matrice = new StringBuilder();

	    char[][] etatEchiquier = this.etatEchiquier;

	    for (int i = 0; i < etatEchiquier.length; i++) {
            matrice.append((i + 1)).append(" ");
            for (int j = 0; j < etatEchiquier[i].length; j++) {
                matrice.append(etatEchiquier[i][j]).append(" ");
            }
            matrice.append('\n');
        }
	    matrice.append('\n');
        matrice.append("  a b c d e f g h");

        return matrice.toString();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EtatPartieEchecs echec = new EtatPartieEchecs();
		System.out.println(echec.toString());
	}
}