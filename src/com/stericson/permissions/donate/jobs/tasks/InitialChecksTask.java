package com.stericson.permissions.donate.jobs.tasks;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.permissions.donate.Constants;
import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.domain.Result;
import com.stericson.permissions.donate.jobs.InitialChecks;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class InitialChecksTask extends BaseTask {

    public static Result executeTask(InitialChecks job)
    {
        Result result = new Result();
        result.setSuccess(true);
        context = job.getContext();

        result = commonCheck();

        if (!result.isSuccess()) {
            return result;
        }

        try {
            shell = RootTools.getShell(true);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setError(context.getString(R.string.shell_error));
            e.printStackTrace();
            return result;
        }

        if (!RootTools.isBusyboxAvailable())
        {
            result.setSuccess(false);
            result.setError(context.getString(R.string.busybox));
            return result;
        }
        else
        {
            if (!RootTools.findBinary("chattr"))
            {
                result.setSuccess(false);
                result.setError(context.getString(R.string.chattr));
                return result;
            }
            else
            {
                try {
                    CommandCapture cmd = new CommandCapture(0, "chmod 0777 " + Constants.path(),
                            "chown /dbdata/databases/com.stericson.permissions.donate",
                            "chown /dbdata/databases/com.stericson.permissions.donate/shared_prefs",
                            "chown /dbdata/databases/com.stericson.permissions.donate/shared_prefs/com.stericson.permissions.donate_preferences.xml",
                            "chown /dbdata/databases/com.stericson.permissions.donate/shared_prefs/Permissions.xml",
                            "chown /data/data/databases/com.stericson.permissions.donate",
                            "chown /data/data/databases/com.stericson.permissions.donate/shared_prefs",
                            "chown /data/data/databases/com.stericson.permissions.donate/shared_prefs/com.stericson.permissions.donate_preferences.xml",
                            "chown /data/data/databases/com.stericson.permissions.donate/shared_prefs/Permissions.xml");

                    shell.add(cmd).waitForFinish();
                } catch (InterruptedException e) {
                    result.setSuccess(false);
                    result.setError(context.getString(R.string.shell_error));
                    e.printStackTrace();
                    return result;
                } catch (IOException e) {
                    result.setSuccess(false);
                    result.setError(context.getString(R.string.shell_error));
                    e.printStackTrace();
                    return result;
                }
            }
        }

        return result;
    }
}
