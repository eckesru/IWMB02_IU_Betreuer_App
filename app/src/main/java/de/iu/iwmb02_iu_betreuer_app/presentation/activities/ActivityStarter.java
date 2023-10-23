package de.iu.iwmb02_iu_betreuer_app.presentation.activities;

import android.content.Context;
import android.content.Intent;

import de.iu.iwmb02_iu_betreuer_app.model.User;

public class ActivityStarter {

    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void startLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void startSupervisorBoardActivity(Context context){
        Intent intent = new Intent(context, SupervisorBoardActivity.class);
        context.startActivity(intent);
    }

    public static void startSupervisorBoardActivity(Context context, User user){
        Intent intent = new Intent(context, SupervisorBoardActivity.class);
        intent.putExtra("user", user);
        context.startActivity(intent);
    }
}
