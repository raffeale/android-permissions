package com.stericson.permissions.donate.jobs.tasks;

import android.content.Context;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.Shell;
import com.stericson.permissions.donate.Constants;
import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.Shared;
import com.stericson.permissions.donate.domain.Result;

public abstract class BaseTask {

    protected static Shell shell = null;
    protected static Context context = null;

    public static Result commonCheck()
    {
        Result result = new Result();
        result.setSuccess(true);

        try {
            //See if we have any issue getting a shell
            Shared.fetchPermissionsFile(RootTools.getShell(true));
        } catch (Exception e) {
            e.printStackTrace();
            result.setError(context.getString(R.string.shell_error));
            result.setSuccess(false);
        }

        if (!RootTools.exists(Constants.storagePath + "/packages1.xml")) {
            //The file does not exist
            result.setSuccess(false);
            result.setError(context.getString(R.string.file_non_existent));
            return result;
        }

        return result;

    }
}
