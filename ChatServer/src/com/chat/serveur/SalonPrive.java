package com.chat.serveur;

import com.echecs.PartieEchecs;

public class SalonPrive {

	private PartieEchecs partieEchecs = new PartieEchecs();
	private String Host;
	private String Invite;
//	private PartieEchecs partieEchecs;

	
	public SalonPrive (String Host, String Invite){
		
		this.Host=Host;
		this.Invite=Invite;
		
	}
	
	public String getHost() {
		return Host;
	}
	
	public String getInvite() {
		return Invite;
	}
	
	public void setPartieEchecs(PartieEchecs partieEchecs) {
		this.partieEchecs = partieEchecs;
	}
	
	public PartieEchecs getPartieEchecs() {
		return partieEchecs;
	}



	
	

}
