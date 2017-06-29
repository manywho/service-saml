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

    public static ArrayList<UserAllowed> listOfUsers(String groupsString) {
        ArrayList<UserAllowed> users = new ArrayList<>();

        if (!StringUtils.isEmpty(groupsString)) {
            String[] stringName = groupsString.split(";");

            for (String userNameAndId : stringName) {
                String[] userParts = userNameAndId.split(",");
                if (userParts.length != 2) {
                    throw new RuntimeException("Error in the format for Supported Users.");
                }

                UserAllowed userRestriction = new UserAllowed(userParts[1], userParts[0]);
                users.add(userRestriction);
            }
        }

        return users;
    }
}
