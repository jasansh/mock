package com.tieto.pcm.mfa.services.integration.jms.enumeration;

public enum ExternalModule {

    APPROVAL,   // from viewpoint of MFA
    ENGINE,     // from viewpoint of MFA
    BANK,       // from viewpoint of MFA

    MFA_FROM_APPROVAL, // for unit testing (from viewpoint of Approval)
    MFA_FROM_ENGINE,   // for unit testing (from viewpoint of Engine)
    MFA_FROM_BANK      // for unit testing (from viewpoint of Bank)
}
