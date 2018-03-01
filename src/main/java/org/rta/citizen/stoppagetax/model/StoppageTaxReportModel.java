package org.rta.citizen.stoppagetax.model;

/**
 * @author sohan.maurya
 *
 */
public class StoppageTaxReportModel {
	
	private String owner;
	private String other;
	private Boolean garagedPlaceReported;
	private String shiftedComment;
	private String mechanicsStatements;
	private String repairsComments;
	private Boolean isRepairsCondemned;
	private String repairsCondemnedComments;
	private Boolean isVerifiedGVR;
	private String oilBunks;
	private String allCheckPosts;
	private String overAllComments;
	private String applicationNo;
	private Long stoppageTaxReportId;
	
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	public Boolean getGaragedPlaceReported() {
		return garagedPlaceReported;
	}
	public void setGaragedPlaceReported(Boolean garagedPlaceReported) {
		this.garagedPlaceReported = garagedPlaceReported;
	}
	public String getShiftedComment() {
		return shiftedComment;
	}
	public void setShiftedComment(String shiftedComment) {
		this.shiftedComment = shiftedComment;
	}
	public String getMechanicsStatements() {
		return mechanicsStatements;
	}
	public void setMechanicsStatements(String mechanicsStatements) {
		this.mechanicsStatements = mechanicsStatements;
	}
	public String getRepairsComments() {
		return repairsComments;
	}
	public void setRepairsComments(String repairsComments) {
		this.repairsComments = repairsComments;
	}
	public Boolean getIsRepairsCondemned() {
		return isRepairsCondemned;
	}
	public void setIsRepairsCondemned(Boolean isRepairsCondemned) {
		this.isRepairsCondemned = isRepairsCondemned;
	}
	public Boolean getIsVerifiedGVR() {
		return isVerifiedGVR;
	}
	public void setIsVerifiedGVR(Boolean isVerifiedGVR) {
		this.isVerifiedGVR = isVerifiedGVR;
	}
	public String getOilBunks() {
		return oilBunks;
	}
	public void setOilBunks(String oilBunks) {
		this.oilBunks = oilBunks;
	}
	public String getAllCheckPosts() {
		return allCheckPosts;
	}
	public void setAllCheckPosts(String allCheckPosts) {
		this.allCheckPosts = allCheckPosts;
	}
	public String getOverAllComments() {
		return overAllComments;
	}
	public void setOverAllComments(String overAllComments) {
		this.overAllComments = overAllComments;
	}
	public String getRepairsCondemnedComments() {
		return repairsCondemnedComments;
	}
	public void setRepairsCondemnedComments(String repairsCondemnedComments) {
		this.repairsCondemnedComments = repairsCondemnedComments;
	}
	public String getApplicationNo() {
		return applicationNo;
	}
	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
	}
	public Long getStoppageTaxReportId() {
		return stoppageTaxReportId;
	}
	public void setStoppageTaxReportId(Long stoppageTaxReportId) {
		this.stoppageTaxReportId = stoppageTaxReportId;
	}
	
}
