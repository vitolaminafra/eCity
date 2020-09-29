package com.vitolaminafra.ecity;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;


public class ProfileFragment extends Fragment {

    private static final String CHANNEL_ID = "ecitychannel";
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builder;


    private TextView user, email;
    private FirebaseAuth mAuth;

    private Button logoutBtn, editBtn, notificationBtn;

    private User loggedUser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        logoutBtn = v.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.logout();
            }
        });

        editBtn = v.findViewById(R.id.editProfileBtn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent infoActivity = new Intent(getContext(), SignupInfoActivity.class);
                infoActivity.addCategory("edit");
                startActivity(infoActivity);
            }
        });

        notificationManager = NotificationManagerCompat.from(getContext());
        createNotificationChannel();

        Intent intent = new Intent(getContext(), SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

        builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.notlogo)
                .setContentTitle("eCity")
                .setContentText("La tua prenotazione scade tra 5 minuti!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationBtn = v.findViewById(R.id.testNotificationBtn);

        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationManager.notify(1, builder.build());
            }
        });


        user = v.findViewById(R.id.userEdit);
        email = v.findViewById(R.id.emailUserEdit);

        mAuth = FirebaseAuth.getInstance();

        loggedUser = MainActivity.getLoggedUser();
        user.setText(loggedUser.getNome() + " "+ loggedUser.getCognome());
        email.setText(loggedUser.getEmail());


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.getNavBar().setTabIndex(3, false);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ecity";
            String description = "ecity_channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



}
