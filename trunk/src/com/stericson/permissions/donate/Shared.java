package com.stericson.permissions.donate;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.widget.Toast;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.RootTools.execution.Shell;
import com.stericson.permissions.donate.activities.PermissionsActivity;
import com.stericson.permissions.donate.interfaces.Choice;
import com.stericson.permissions.donate.service.PreferenceService;
import com.stericson.permissions.donate.widget.Widget;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;

public class Shared {

    public static boolean cleanup(Shell shell)
    {
        try {
            CommandCapture cmd = new CommandCapture(0, "chattr -ai " + Constants.path(),
                    "dd if=" + Constants.storagePath + "/packages.xml of=" + Constants.path(),
                    "rm " + Constants.storagePath + "/packages1.xml",
                    "rm " + Constants.storagePath + "/packages.xml");

            if (shell == null)
            {
                shell = RootTools.getShell(true);
            }

            shell.add(cmd);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean fetchPermissionsFile(Shell shell)
    {
        try {
            CommandCapture cmd = new CommandCapture(0, "dd if=" + Constants.path() + " of=" + Constants.storagePath + "/packages1.xml",
                    "dd if=" + Constants.path() + " of=" + Constants.storagePath + "/packages.xml",
                    "chmod 0777 " + Constants.storagePath + "/packages1.xml",
                    "chmod 0777 " + Constants.storagePath + "/packages.xml");

            shell.add(cmd);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static byte[] getByteArray(Drawable drawable) {
        BitmapDrawable bitDw = ((BitmapDrawable) drawable);
        Bitmap bitmap = bitDw.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        return stream.toByteArray();
    }

    public static Drawable getDrawable(byte[] bytes, Context context) {
        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return new BitmapDrawable(context.getResources(),bm);
    }

    public static boolean lockPermissions(Context context, boolean silent)
    {
        try
        {
            Shell shell = RootTools.getShell(true);
            CommandCapture cmd = new CommandCapture(0, "chmod 0777 " + Constants.path(), "chattr +ai " + Constants.path());
            shell.add(cmd);
            new PreferenceService(context).setLocked(true);
            if (!silent)
            {
                Toast.makeText(context, context.getString(R.string.permissionsLocked), Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        catch (Exception e)
        {
            if (!silent)
            {
                Toast.makeText(context, context.getString(R.string.permissionslockfailed), Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }

    public static void makeBackup(final Context context)
    {
        if (RootTools.exists(Environment.getExternalStorageDirectory() + "/packages.xml")) {
            final SpannableString s = new SpannableString(context.getString(R.string.choosebackupoption));
            Linkify.addLinks(s, Linkify.ALL);
            new AlertDialog.Builder(context).setCancelable(true)
                    .setTitle(R.string.uptoyou).setMessage(s)
                    .setPositiveButton(R.string.create,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    try {
                                        Shell shell = RootTools.getShell(true);
                                        CommandCapture cmd = new CommandCapture(0, "dd if=" + Constants.path() + " of=" + Environment.getExternalStorageDirectory() + "/packages.xml");
                                        shell.add(cmd);

                                        Toast toast = Toast.makeText(context, context.getString(R.string.backupcreate), Toast.LENGTH_SHORT);
                                        toast.show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                    .setNegativeButton(R.string.restore,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    try {

                                        PreferenceService ps = new PreferenceService(context);
                                        Shell shell = RootTools.getShell(true);
                                        CommandCapture cmd = new CommandCapture(0, "chattr -ai " + Constants.path(),
                                                "dd if=" + Environment.getExternalStorageDirectory() + "/packages.xml of=" + Constants.path());
                                        shell.add(cmd);

                                        if (ps.getAlwaysLock())
                                        {
                                            Shared.lockPermissions(context, true);
                                        }

                                        Toast toast = Toast.makeText(context, context.getString(R.string.backuprestored), Toast.LENGTH_SHORT);
                                        toast.show();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                    .show();
        } else {
            final SpannableString s = new SpannableString(context.getString(R.string.nobackup));
            Linkify.addLinks(s, Linkify.ALL);
            new AlertDialog.Builder(context).setCancelable(true)
                    .setTitle(R.string.createBackup).setMessage(s)
                    .setPositiveButton(R.string.create,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    try {
                                        Shell shell = RootTools.getShell(true);
                                        CommandCapture cmd = new CommandCapture(0, "dd if=" + Constants.path() + " of=" + Environment.getExternalStorageDirectory() + "/packages.xml");
                                        shell.add(cmd);

                                        Toast toast = Toast.makeText(context, context.getString(R.string.backupcreate), Toast.LENGTH_SHORT);
                                        toast.show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                    .show();
        }
    }

    public static void makeChoice(final Choice choice, final int id, int title, int content, int positive, int negative, Context context)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        choice.choiceMade(true, id);
                    }
                }).setNegativeButton(negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                choice.choiceMade(false, id);
            }}).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                choice.choiceCancelled(id);
            }
        }).show();
    }

    public static LineNumberReader preparePermissionsFileForRead() throws FileNotFoundException {
        String readTarget = Constants.storagePath + "/packages1.xml";
        LineNumberReader lnr = new LineNumberReader( new FileReader( readTarget ) );
        return lnr;
    }

    public static FileWriter preparePermissionsFileForWrite() throws IOException {
        String writeTarget = Constants.storagePath + "/packages.xml";
        FileWriter fw = new FileWriter( writeTarget );
        return fw;
    }

    public static boolean unlockPermissions(Context context, boolean silent)
    {
        try
        {
            Shell shell = RootTools.getShell(true);
            CommandCapture cmd = new CommandCapture(0, "chmod 0777 " + Constants.path(), "chattr -ai " + Constants.path());
            shell.add(cmd);
            new PreferenceService(context).setLocked(false);
            if (!silent)
            {
                Toast.makeText(context, context.getString(R.string.permissionsUnlocked), Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        catch (Exception e)
        {
            if (!silent)
            {
                Toast.makeText(context, context.getString(R.string.permissionsunlockfailed), Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }

    public static void updateWidget(Context context)
    {
        for (int i : AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context.getPackageName(), Widget.class.getName())))
        {
            //Create an Intent to launch Update
            Intent intent = new Intent(context, Widget.class);
            intent.setAction("UPDATE-TEXT");
            intent.putExtra("ID", i);
            context.sendBroadcast(intent);
        }
    }

    public static void updateStatus(Context context)
    {
        if (new PreferenceService(context).getNotifyLock())
        {
            String message = (new PreferenceService(context).isLocked() ? context.getString(R.string.permissionsLocked) : context.getString(R.string.permissionsUnlocked));

            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);

            int icon = R.drawable.icon_notification;
            CharSequence tickerText = message;
            long when = System.currentTimeMillis();

            Notification notification = new Notification(icon, tickerText, when);
            notification.flags = Notification.FLAG_ONGOING_EVENT;

            CharSequence contentTitle = context.getString(R.string.app_name);
            CharSequence contentText = message;
            Intent notificationIntent = new Intent(context, PermissionsActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

            mNotificationManager.notify(1, notification);
        }
    }
}
