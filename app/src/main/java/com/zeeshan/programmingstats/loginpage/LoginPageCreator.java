package com.zeeshan.programmingstats.loginpage;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zeeshan.programmingstats.dashboard.DashboardActivity;
import com.zeeshan.programmingstats.R;
public class LoginPageCreator {

    private EditText userNameText;
    private Button loginButton;

    public void login(final Activity activity)
    {
        userNameText = activity.findViewById(R.id.userName);
        loginButton = activity.findViewById(R.id.loginButton);

        userNameText.setSingleLine(true);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = userNameText.getText().toString();
                Toast toast = Toast.makeText(activity, "Logging in", Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(activity, DashboardActivity.class);
                intent.putExtra("userName", userName);
                activity.startActivity(intent);
            }
        });

    }
}
