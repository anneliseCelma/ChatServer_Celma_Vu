package com.chat.serveur;


public class Invitation {
	
	private String Host;
	private String Invite;

	
	public Invitation (String Host, String Invite){
		
		this.Host=Host;
		this.Invite=Invite;
		
	}
	
	public String getHost() {
		return Host;
	}
	
	public String getInvite() {
		return Invite;
	}
	
	

}
