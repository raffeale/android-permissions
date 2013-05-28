package com.stericson.permissions.donate.jobs.tasks;

import com.stericson.RootTools.RootTools;
import com.stericson.permissions.donate.Constants;
import com.stericson.permissions.donate.Shared;
import com.stericson.permissions.donate.domain.Permission;
import com.stericson.permissions.donate.domain.Result;
import com.stericson.permissions.donate.jobs.ChangePermission;
import com.stericson.permissions.donate.service.DBService;
import com.stericson.permissions.donate.service.PreferenceService;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;


public class ChangePermissionTask extends BaseTask {

    public static Result executeTask(ChangePermission job, String permission, String packageName)
    {
        Result result = new Result();
        result.setSuccess(true);
        context = job.getContext();

        Permission p = new Permission();
        p.setPackageName(packageName);
        p.setPermission(permission);

        result = commonCheck();

        if (!result.isSuccess()) {
            return result;
        }

        if (RootTools.exists(Constants.storagePath + "/packages1.xml")) {
            //Time to read the file, find the line we are looking for.
            try {
                String readTarget = Constants.storagePath + "/packages1.xml";
                String writeTarget = Constants.storagePath + "/packages.xml";
                LineNumberReader lnr = new LineNumberReader( new FileReader( readTarget ) );
                FileWriter fw = new FileWriter( writeTarget );
                String line;

                while( (line = lnr.readLine()) != null ){
                    //Found the package name
                    if ((line.contains("package") || line.contains("shared-user") || line.contains("updated-package")) && line.contains("name=\"" + packageName + "\"")) {
                        fw.write(line + "\n");
                        RootTools.log(line);
                        //Looking for the permission
                        while( (line = lnr.readLine()) != null ){
                            RootTools.log(line);
                            if (line.contains("/package>") || line.contains("/shared-user") || line.contains("/updated-package"))
                            {
                                break;
                            }
                            //Found the permission
                            if (line.contains(permission)) {
                                String tmp;
                                if (line.contains("stericson.disabled.")) {
                                    tmp = line.replace("stericson.disabled.", "");
                                    p.setActive(true);
                                } else {
                                    tmp = line.replace("name=\"", "name=\"stericson.disabled.");
                                    p.setActive(false);
                                }
                                fw.write(tmp + "\n");
                                line = lnr.readLine();
                                break;
                            } else {
                                fw.write(line + "\n");
                            }
                        }
                    }
                    fw.write(line + "\n");
                }
                fw.close();
                lnr.close();
            } catch (IOException e) {
                e.printStackTrace();
                return result;
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

        } else {
            return result;
        }

        return result;

    }
}
