package com.tieto.pcm.cop.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = CopStatusRequest.Builder.class)
public class CopStatusRequest {

    private String sortCode;
    private String accountNumber;
    private String accountName;
    private String accountType;


    public CopStatusRequest(Builder builder) {
        this.sortCode = builder.sortCode;
        this.accountNumber = builder.accountNumber;
        this.accountName = builder.accountName;
        this.accountType = builder.accountType;
    }

    public static class Builder {
        private String sortCode;
        private String accountNumber;
        private String accountName;
        private String accountType;


        @JsonCreator
        public Builder(@JsonProperty("sortCode") String sortCode,
                       @JsonProperty("accountNumber") String accountNumber,
                       @JsonProperty("accountName") String accountName,
                       @JsonProperty("accountType") String accountType) {
            this.sortCode = sortCode;
            this.accountNumber = accountNumber;
            this.accountName = accountName;
            this.accountType = accountType;
        }


        public CopStatusRequest build() {
            return new CopStatusRequest(this);
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getSortCode() {
        return sortCode;
    }
}
