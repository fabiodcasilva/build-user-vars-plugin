package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.triggers.SCMTrigger;
import hudson.triggers.SCMTrigger.SCMTriggerCause;
import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;

import java.util.Map;

public class SCMTriggerCauseDeterminant implements IUsernameSettable<SCMTrigger.SCMTriggerCause> {

    private static final String SCM_TRIGGER_DUMMY_USER_NAME = "SCM Change";
    private static final String SCM_TRIGGER_DUMMY_USER_ID = "scmChange";

    public boolean setJenkinsUserBuildVars(SCMTriggerCause cause, Map<String, String> variables) {
        if (cause != null) {
            UsernameUtils.setUsernameVars(SCM_TRIGGER_DUMMY_USER_NAME, variables);
            variables.put(BUILD_USER_ID, SCM_TRIGGER_DUMMY_USER_ID);

            return true;
        }
        return false;
    }

    public Class<SCMTriggerCause> getUsedCauseClass() {
        return SCMTriggerCause.class;
    }
}
