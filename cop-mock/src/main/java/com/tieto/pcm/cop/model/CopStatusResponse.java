package com.tieto.pcm.cop.model;

//@JsonDeserialize(builder= CopStatusResponse.Builder.class)
public class CopStatusResponse {

    private String copId;
    private String requestStatus;
    private String reasonCode;
    private String reasonText;
    private String nameSuggestion;
    private String acceptedTimestamp;
    private String acceptedBy;
    private Boolean recheckRequired;

    public void setCopId(String copId) {
        this.copId = copId;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public void setReasonText(String reasonText) {
        this.reasonText = reasonText;
    }

    public void setNameSuggestion(String nameSuggestion) {
        this.nameSuggestion = nameSuggestion;
    }

    public void setAcceptedTimestamp(String acceptedTimestamp) {
        this.acceptedTimestamp = acceptedTimestamp;
    }

    public void setAcceptedBy(String acceptedBy) {
        this.acceptedBy = acceptedBy;
    }

    public void setRecheckRequired(Boolean recheckRequired) {
        this.recheckRequired = recheckRequired;
    }

    public String getCopId() {
        return copId;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public String getReasonText() {
        return reasonText;
    }

    public String getNameSuggestion() {
        return nameSuggestion;
    }

    public String getAcceptedTimestamp() {
        return acceptedTimestamp;
    }

    public String getAcceptedBy() {
        return acceptedBy;
    }

    public Boolean getRecheckRequired() {
        return recheckRequired;
    }

}
