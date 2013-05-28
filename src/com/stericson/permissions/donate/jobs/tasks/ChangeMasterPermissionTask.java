package com.stericson.permissions.donate.jobs.tasks;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.permissions.donate.Shared;
import com.stericson.permissions.donate.domain.Permission;
import com.stericson.permissions.donate.domain.Result;
import com.stericson.permissions.donate.jobs.ChangeMasterPermission;
import com.stericson.permissions.donate.service.DBService;
import com.stericson.permissions.donate.service.PreferenceService;

import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.concurrent.TimeoutException;


public class ChangeMasterPermissionTask extends BaseTask {

    public static Result executeTask(ChangeMasterPermission job, String permission, boolean disable)
    {
        Result result = new Result();
        result.setSuccess(true);
        context = job.getContext();

        Permission p = new Permission();
        p.setPackageName("");
        p.setPermission(permission);
        p.setActive(!disable);

        result = commonCheck();

        if (!result.isSuccess()) {
            return result;
        }

        //Time to read the file, find the line we are looking for.
        try {
            shell = RootTools.getShell(true);
            LineNumberReader lnr = Shared.preparePermissionsFileForRead();
            FileWriter fw = Shared.preparePermissionsFileForWrite();
            String line;
            boolean done = false;

            while( (line = lnr.readLine()) != null ){
                if (line.contains("<shared-user")) {
                    done = true;
                }
                if (line.contains("<perms>") && !done) {
                    fw.write(line + "\n");
                    while( (line = lnr.readLine()) != null ){
                        if (line.contains("</perms>")) {
                            break;
                        }
                        if (line.contains(permission)) {
                            String tmp;
                            if (line.contains("stericson.disabled.")) {
                                if (!disable) {
                                    tmp = line.replace("stericson.disabled.", "");
                                } else {
                                    tmp = line;
                                }
                            } else {
                                if (disable) {
                                    tmp = line.replace("name=\"", "name=\"stericson.disabled.");
                                } else {
                                    tmp = line;
                                }
                            }
                            fw.write(tmp + "\n");
                        } else {
                            fw.write(line + "\n");
                        }
                    }
                }
                fw.write(line + "\n");
            }

            //close
            fw.close();
            lnr.close();

        } catch (IOException e) {
            e.printStackTrace();
            result.setSuccess(false);
        } catch (RootDeniedException e) {
            e.printStackTrace();
            result.setSuccess(false);
        } catch (TimeoutException e) {
            e.printStackTrace();
            result.setSuccess(false);
        }

        PreferenceService ps = new PreferenceService(context);

        ps.setNeedsReboot(true);
        Shared.cleanup(shell);

        ps.setLocked(ps.getAlwaysLock());
        if (ps.getAlwaysLock())
        {
            Shared.lockPermissions(context, true);
        }

        DBService db = new DBService(context);
        db.insertOrUpdateChangedPermission(p);


        return result;
    }
}
