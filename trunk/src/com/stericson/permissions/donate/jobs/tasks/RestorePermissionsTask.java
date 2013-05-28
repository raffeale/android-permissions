package com.stericson.permissions.donate.jobs.tasks;

import com.stericson.permissions.donate.Shared;
import com.stericson.permissions.donate.domain.Result;
import com.stericson.permissions.donate.jobs.RestorePermissions;
import com.stericson.permissions.donate.service.PreferenceService;

import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;

public class RestorePermissionsTask extends BaseTask {

    public static Result executeTask(RestorePermissions job)
    {
        Result result = new Result();
        result.setSuccess(true);
        context = job.getContext();

        result = commonCheck();

        if (!result.isSuccess()) {
            return result;
        }

        //Time to read the file, find the line we are looking for.
        try {
            LineNumberReader lnr = Shared.preparePermissionsFileForRead();
            FileWriter fw = Shared.preparePermissionsFileForWrite();
            String line;

            while( (line = lnr.readLine()) != null ){
                if (line.contains("stericson.disabled.")) {
                    String tmp = line.replace("stericson.disabled.", "");
                    fw.write(tmp + "\n");
                    line = lnr.readLine();
                }
                fw.write(line + "\n");
            }
            fw.close();
            lnr.close();
        } catch (IOException e) {
            e.printStackTrace();
            result.setSuccess(false);
        }

        PreferenceService ps = new PreferenceService(context);

        Shared.cleanup(shell);

        ps.setLocked(ps.getAlwaysLock());
        if (ps.getAlwaysLock())
        {
            Shared.lockPermissions(context, true);
        }

        return result;
    }
}
