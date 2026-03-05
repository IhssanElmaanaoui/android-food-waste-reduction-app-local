package com.example.foodwastereductionapp.auth;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.foodwastereductionapp.model.User;

/**
 * Gestion de la session utilisateur avec SharedPreferences.
 * Stocke l'état de connexion, l'id, l'email et le rôle pour rediriger vers la bonne interface.
 */
public class SessionManager {

    private static final String PREF_NAME = "FoodWasteSession";
    private static final String KEY_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /** Enregistre la session après connexion ou inscription. */
    public void saveSession(User user) {
        prefs.edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putInt(KEY_USER_ID, user.id)
                .putString(KEY_EMAIL, user.email)
                .putString(KEY_ROLE, user.role != null ? user.role : "client")
                .apply();
    }

    /** Déconnexion : efface toutes les données de session. */
    public void clearSession() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    /** "client" ou "merchant" (commerçant). */
    public String getRole() {
        return prefs.getString(KEY_ROLE, "client");
    }
}
