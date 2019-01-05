package com.denis.moviesapp.localAuth;

import android.content.Context;

import com.denis.moviesapp.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

public class LocalUsers {
    private String filenameUsers;
    private String filenameLogStatus;

    public LocalUsers() {
        this.filenameUsers = "users";
        this.filenameLogStatus = "logStatus";
    }

    public void updateLocalUsers(Context context, String users) throws IOException {
        FileUtils file = new FileUtils();
        file.write(context, filenameUsers, users);
    }

    public Boolean checkUser(Context context, String username, String password) throws Exception {
        FileUtils file = new FileUtils();
        String data;

        try {
            data = file.read(context, filenameUsers);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try {
            JSONArray jsonarray = new JSONArray(data);

            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String hash = jsonobject.getString("hash");

                if (BCrypt.checkpw(username + password, hash)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void login(Context context) {
        FileUtils file = new FileUtils();

        try {
            file.write(context, filenameLogStatus, "in");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logout(Context context) {
        FileUtils file = new FileUtils();

        try {
            file.write(context, filenameLogStatus, "out");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Logged in - True
    // Logged out - False
    public Boolean checkIfLoggedIn(Context context) {
        FileUtils file = new FileUtils();
        String data;

        try {
            data = file.read(context, filenameLogStatus);
        } catch (Exception e) {
            return false;
        }

        return data.equals("in");
    }
}
