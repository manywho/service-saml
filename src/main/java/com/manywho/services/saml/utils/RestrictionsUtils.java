package com.manywho.services.saml.utils;

import com.manywho.services.saml.entities.UserAllowed;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;

public class RestrictionsUtils {

    public static ArrayList<String> listOfGroups(String groupsString) {
        ArrayList<String> groups = new ArrayList<>();

        if (!StringUtils.isEmpty(groupsString)) {
            String[] stringName = groupsString.split(";");
            Collections.addAll(groups, stringName);
        }

        return groups;
    }

    /**
     *
     * @param usersString format: userId, friendlyName;
     * @return
     */
    public static ArrayList<UserAllowed> listOfUsers(String usersString) {
        ArrayList<UserAllowed> users = new ArrayList<>();

        if (!StringUtils.isEmpty(usersString)) {
            String[] stringName = usersString.split(";");

            for (String userNameAndId : stringName) {
                String[] userParts = userNameAndId.split(",");
                if (userParts.length != 2) {
                    throw new RuntimeException("Error in the format for Supported Users.");
                }

                UserAllowed userRestriction = new UserAllowed(userParts[0], userParts[1]);
                users.add(userRestriction);
            }
        }

        return users;
    }
}
