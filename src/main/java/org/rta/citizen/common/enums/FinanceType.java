package org.rta.citizen.common.enums;

public enum FinanceType {

	
    NA(0,"na") ,ONLINE(1,"online") , OFFLINE(2,"offline");
	private int id;
	private String label;
	
	private FinanceType() {
	
	}
	private FinanceType(int id,String label) {
		this.id=id;
		this.label=label;
		
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

}
