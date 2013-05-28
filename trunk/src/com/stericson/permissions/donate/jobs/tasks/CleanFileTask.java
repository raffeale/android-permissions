package com.stericson.permissions.donate.jobs.tasks;

import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.Shared;
import com.stericson.permissions.donate.domain.Result;
import com.stericson.permissions.donate.jobs.CleanFile;
import com.stericson.permissions.donate.service.PreferenceService;

import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;


public class CleanFileTask extends BaseTask {

    public static Result executeTask(CleanFile job)
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

            //begin the search
            while( (line = lnr.readLine()) != null ){
                //found permission set
                if (line.equals("<permissions>") || line.equals("<perms>"))
                {
                    //keep the duplicates here for reference later.
                    ArrayList<String> duplicates = new ArrayList<String>();

                    job.publishJobProgress(context.getString(R.string.searching));

                    fw.write(line + "\n");

                    //make sure we have no duplicates
                    while( (line = lnr.readLine()) != null ) {
                        boolean duplicate = false;
                        //Closed permissions
                        if (line.equals("</permissions>") || line.equals("</perms>"))
                        {
                            job.publishJobProgress(context.getString(R.string.verifyingFile));
                            break;
                        }

                        //mark the position
                        lnr.mark(1000000);
                        String tmpLine1 = line;
                        //permission found
                        String tmpLine = line.replace("stericson.disabled.", "");

                        while( (line = lnr.readLine()) != null ) {
                            //Closed permissions
                            if (line.equals("</permissions>") || line.equals("</perms>"))
                            {
                                break;
                            }
                            String tmp = line.replace("stericson.disabled.", "");
                            if (tmp.equals(tmpLine))
                            {
                                duplicate = true;
                                duplicates.add(tmp);
                            }
                        }
                        //reset to position
                        lnr.reset();

                        if (!duplicate)
                        {
                            //if it is not a duplicate, go ahead and write it.
                            //not a duplicate this time, but if it was before then we re-enable it because
                            //it probably cannot be disabled.
                            if (duplicates.contains(tmpLine))
                            {
                                fw.write(tmpLine + "\n");
                            }
                            else
                            {
                                fw.write(tmpLine1 + "\n");
                            }
                        }
                        //done, loop and check the next permission.
                    }
                }
                fw.write(line + "\n");
            }

            //close
            fw.close();
            lnr.close();

        }
        catch (IOException e) {
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
