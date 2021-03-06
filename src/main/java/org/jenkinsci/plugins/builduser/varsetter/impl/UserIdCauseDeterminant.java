package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.model.User;
import hudson.model.Cause.UserIdCause;
import hudson.security.SecurityRealm;
import hudson.security.ACL;
import hudson.tasks.MailAddressResolver;
import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;
import org.jenkinsci.plugins.saml.SamlSecurityRealm;
import jenkins.model.Jenkins;

import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.acegisecurity.GrantedAuthority;

/**
 * This implementation is used to determine build username variables from <b>{@link UserIdCause}</b>.
 * This will let to get whole set of variables:
 * <ul>
 *   <li>{@link IUsernameSettable#BUILD_USER_ID}</li>
 *   <li>{@link IUsernameSettable#BUILD_USER_VAR_NAME}</li>
 *   <li>{@link IUsernameSettable#BUILD_USER_VAR_GROUPS}</li>
 *   <li>{@link IUsernameSettable#BUILD_USER_FIRST_NAME_VAR_NAME}</li>
 *   <li>{@link IUsernameSettable#BUILD_USER_LAST_NAME_VAR_NAME}</li>
 * </ul>
 *
 * @author GKonovalenko
 */
public class UserIdCauseDeterminant implements IUsernameSettable<UserIdCause> {

    private static final Logger log = Logger.getLogger(UserIdCauseDeterminant.class.getName());

    /**
     * {@inheritDoc}
     * <p>
     * <b>{@link UserIdCause}</b> based implementation.
     */
    public boolean setJenkinsUserBuildVars(UserIdCause cause, Map<String, String> variables) {
        if (cause != null) {
            String username = cause.getUserName();
            UsernameUtils.setUsernameVars(username, variables);

            String trimmedUserId = StringUtils.trimToEmpty(cause.getUserId());
            String originalUserid = trimmedUserId.isEmpty() ? ACL.ANONYMOUS_USERNAME : trimmedUserId;
            String userid = originalUserid;
            StringBuilder groupString = new StringBuilder();
            try {
                Jenkins jenkinsInstance = Jenkins.get();
                SecurityRealm realm = jenkinsInstance.getSecurityRealm();
                userid = mapUserId(userid, realm);
                GrantedAuthority[] authorities = realm.loadUserByUsername(originalUserid).getAuthorities();
                for (GrantedAuthority authority : authorities) {
                    String authorityString = authority.getAuthority();
                    if (StringUtils.isNotEmpty(authorityString)) {
                        groupString.append(authorityString).append(",");
                    }
                }
                groupString.setLength(groupString.length() == 0 ? 0 : groupString.length() - 1);
            } catch (Exception err) {
                // Error
                log.warning(String.format("Failed to get groups for user: %s error: %s ", userid, err.toString()));
            }
            variables.put(BUILD_USER_ID, userid);
            variables.put(BUILD_USER_VAR_GROUPS, groupString.toString());
            variables.put(BUILD_USER_EMAIL, getUserEmail());

            return true;
        }
        return false;
    }

    private String mapUserId(String userid, SecurityRealm realm) {
        try {
            if (realm instanceof SamlSecurityRealm) {
                String conversion = ((SamlSecurityRealm) realm).getUsernameCaseConversion();
                switch (conversion) {
                    case "lowercase":
                        userid = userid.toLowerCase();
                        break;
                    case "uppercase":
                        userid = userid.toUpperCase();
                        break;
                    default:
                }
            }
        } catch (NoClassDefFoundError e) {
            log.fine("It seems the saml plugin is not installed, skipping saml user name mapping.");
        }
        return userid;
    }

    private String getUserEmail() {
        User user = User.current();
        if (user != null) {
            return MailAddressResolver.resolve(user);
        }
        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    public Class<UserIdCause> getUsedCauseClass() {
        return UserIdCause.class;
    }
}
