package com.example.pushdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "general_channel_42";

    NotificationManager notificationManager;

    private String bigContentTitle = "Tawasal SuperApp";
    private String notificationGroup = "messages";
    private int mainNotificationId = 1;
    private Bitmap avatarBitmap;
    private IconCompat avatarIcon;

    private String msgs[] = {
            "text msg 1",
            "text msg 2",
            "text msg 3",
            "text msg 4"
    };

    private int userIds[] = {
            17319,
            22022
    };

    private String userNames[] = {
            "A",
            "B"
    };

    private Person persons[] = {
            new Person.Builder().setName(userNames[0]).setIcon(avatarIcon).build(),
            new Person.Builder().setName(userNames[1]).setIcon(avatarIcon).build()
    };

    private String userNamePlusMessage[] = {
            userNames[0] + ": " + msgs[0],
            userNames[1] + ": " + msgs[1],
            userNames[0] + ": " + msgs[2],
            userNames[1] + ": " + msgs[3]
    };

    private String personPhones[] = {
            "tel:+9996613952",
            "tel:+9996613953"
    };

    public static volatile Handler applicationHandler;
    public static void runOnUIThread(Runnable runnable, long delay) {
        if (MainActivity.applicationHandler == null) {
            return;
        }
        if (delay == 0) {
            MainActivity.applicationHandler.post(runnable);
        } else {
            MainActivity.applicationHandler.postDelayed(runnable, delay);
        }
    }

    public MainActivity() {
        super();
        int diameter = 128;
        avatarBitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(avatarBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(diameter / 2, diameter / 2, diameter / 2, paint);
        avatarIcon = IconCompat.createWithBitmap(avatarBitmap);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applicationHandler = new Handler(this.getMainLooper());

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();

        FrameLayout layout = new FrameLayout(this);
        layout.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(linearLayout, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                android.view.Gravity.CENTER));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        lp.setMargins(0, 40, 0, 40);

        TextView btn1 = new TextView(this);
        btn1.setText(userNamePlusMessage[0]);
        btn1.setPadding(40, 20, 40, 20);
        btn1.setBackgroundColor(Color.YELLOW);
        btn1.setOnClickListener(v -> send1PushNotification());

        TextView btn2 = new TextView(this);
        btn2.setText(userNamePlusMessage[1]);
        btn2.setPadding(40, 20, 40, 20);
        btn2.setBackgroundColor(Color.YELLOW);
        btn2.setOnClickListener(v -> sendPush(2));

        TextView btn3 = new TextView(this);
        btn3.setText(userNamePlusMessage[2]);
        btn3.setPadding(40, 20, 40, 20);
        btn3.setBackgroundColor(Color.YELLOW);
        btn3.setOnClickListener(v -> sendPush(3));

        TextView btn4 = new TextView(this);
        btn4.setText(userNamePlusMessage[3]);
        btn4.setPadding(40, 20, 40, 20);
        btn4.setBackgroundColor(Color.YELLOW);
        btn4.setOnClickListener(v -> sendPush(4));

        linearLayout.addView(btn1, lp);
        linearLayout.addView(btn2, lp);
        linearLayout.addView(btn3, lp);
        linearLayout.addView(btn4, lp);

        setContentView(layout);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "general channel name";
            String description = "general channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void send1PushNotification() {
        long date = System.currentTimeMillis();
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(persons[0]);
        messagingStyle.setGroupConversation(false);
        messagingStyle.addMessage(msgs[0], System.currentTimeMillis(), persons[0]);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(userNames[0])
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentText(userNamePlusMessage[0])
                .setAutoCancel(true)
                .setNumber(1)
                .setColor(0xffffc200)
                .setGroupSummary(false)
                .setWhen(date)
                .setShowWhen(true)
                .setStyle(messagingStyle)
                //.setContentIntent(contentIntent)
                //.extend(wearableExtender)
                //.setSortKey(String.valueOf(Long.MAX_VALUE - date))
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);
        builder.addPerson(personPhones[0]);
        builder.setChannelId(CHANNEL_ID);

        notificationManager.cancel(mainNotificationId);
        notificationManager.cancel(userIds[0]);
        notificationManager.cancel(userIds[1]);
        notificationManager.notify(userIds[0], builder.build());
    }

    private void sendSummaryNotification(int msgNumber) {
        long date = System.currentTimeMillis();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder.setContentText(msgNumber + " messages from 2 chats");
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(bigContentTitle);
        for (int i = 0; i < msgNumber; i++) {
            inboxStyle.addLine(userNamePlusMessage[i]);
        }
        inboxStyle.setSummaryText(msgNumber + " messages from 2 chats");
        mBuilder.setStyle(inboxStyle);

        mBuilder.setContentTitle(bigContentTitle)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
                .setNumber(msgNumber)
                .setGroup(notificationGroup)
                .setGroupSummary(true)
                .setShowWhen(true)
                .setWhen(date)
                .setColor(0xffffc200);
        mBuilder.setCategory(NotificationCompat.CATEGORY_MESSAGE);
        mBuilder.addPerson(personPhones[msgNumber % 2]);
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setTicker(userNamePlusMessage[msgNumber - 1]);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);
        mBuilder.setChannelId(CHANNEL_ID);

        Notification mainNotification = mBuilder.build();
        notificationManager.notify(mainNotificationId, mainNotification);
    }

    private String getDialogString(int msgNumber, int dialogNumber) {
        switch (msgNumber) {
            case 2:
                return dialogNumber == 0
                        ? userNamePlusMessage[0]
                        : userNamePlusMessage[1];
            case 3:
                return dialogNumber == 0
                        ? userNamePlusMessage[0] + "\n\n" + userNamePlusMessage[2]
                        : userNamePlusMessage[1];
            case 4:
            default:
                return dialogNumber == 0
                        ? userNamePlusMessage[0] + "\n\n" + userNamePlusMessage[2]
                        : userNamePlusMessage[1] + "\n\n" + userNamePlusMessage[3];
        }
    }

    private void sendDialogsNotification(int msgNumber) {
        long date = System.currentTimeMillis();
        Person.Builder personBuilder = new Person.Builder().setName("You");
        Person selfPerson = personBuilder.build();

        int[] notificationIds = new int[2];
        NotificationCompat.Builder[] holder = new NotificationCompat.Builder[2];

        for (int i = 0; i < 2; i++) {
            NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(selfPerson);
            messagingStyle.setGroupConversation(false);
            int msgCount = 0;
            for (int j = i; j < msgNumber; j = j + 2) {
                msgCount++;
                long delta = (msgNumber - 1 - j) * 60 * 1000L;
                messagingStyle.addMessage(msgs[j] + " " + delta, date - delta, persons[j % 2]);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(userNames[i])
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentText(getDialogString(msgNumber, i))
                    .setAutoCancel(true)
                    .setNumber(msgCount)
                    .setColor(0xffffc200)
                    .setGroupSummary(false)
                    .setWhen(date)
                    .setShowWhen(true)
                    .setStyle(messagingStyle)
                    //.setSortKey(String.valueOf(Long.MAX_VALUE - date))
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE);
            builder.addPerson(personPhones[i]);
            builder.setGroup(notificationGroup);
            builder.setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY);
            builder.setChannelId(CHANNEL_ID);

            Intent msgHeardIntent = new Intent(this, MainActivity.class);
            //msgHeardIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            //msgHeardIntent.setAction("com.tawasul.messenger.ACTION_MESSAGE_HEARD");
            //msgHeardIntent.putExtra("dialog_id", dialogId);
            //msgHeardIntent.putExtra("max_id", maxId);
            //msgHeardIntent.putExtra("currentAccount", currentAccount);
            PendingIntent readPendingIntent = PendingIntent.getBroadcast(this, userIds[i], msgHeardIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action readAction = new NotificationCompat.Action.Builder(R.drawable.ic_launcher_foreground, "Mark as read", readPendingIntent)
                    .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_MARK_AS_READ)
                    .setShowsUserInterface(false)
                    .build();
            builder.addAction(readAction);

            builder.setSmallIcon(R.drawable.ic_launcher_background);
            builder.setLargeIcon(avatarBitmap);

            notificationIds[i] = userIds[i];
            holder[i] = builder;
        }

        // fix
        //if (msgNumber % 2 == 0) {
        //    notify(notificationIds[1], holder[1].build(), 0);
        //} else {
        //    notify(notificationIds[0], holder[0].build(), 0);
        //}

        // unstable repro
        if (msgNumber % 2 == 0) {
            notify(notificationIds[0], holder[0].setSortKey("1").build(), 0);
            notify(notificationIds[1], holder[1].setSortKey("2").build(), 0);
        } else {
            notify(notificationIds[1], holder[1].setSortKey("1").build(), 0);
            notify(notificationIds[0], holder[0].setSortKey("2").build(), 0);
        }
    }

    private void notify(int id, Notification notification, int delay) {
        runOnUIThread(() -> {
            try {
                notificationManager.notify(id, notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, delay);
    }

    private void sendPush(int msgNumber) {
        sendSummaryNotification(msgNumber);
        sendDialogsNotification(msgNumber);
    }
}