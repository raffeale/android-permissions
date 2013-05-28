package com.stericson.permissions.donate.jobs;

import android.content.Context;
import android.content.pm.PackageManager;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.RootTools.execution.Shell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class FixPerms
{
    private static Shell shell = null;

	public static void fix(Context context, String packageName)
	{
		try {
            Command cmd;
            try {
                shell = RootTools.getShell(true);
            } catch (TimeoutException e) {
                //Ignore, we don't care so much if this fails...
                RootTools.log("Fixing permissions failed. Shell Error");
                e.printStackTrace();
                return;
            } catch (IOException e) {
                //Ignore, we don't care so much if this fails...
                RootTools.log("Fixing permissions failed. Shell Error");
                e.printStackTrace();
                return;
            }

			PackageManager pm = context.getPackageManager();
			//RootTools.sendShell("pm install -r " + pm.getApplicationInfo(list.get(position).packageName, 0).sourceDir);

            final List<String> list = new ArrayList<String>();

            cmd = new Command(0, "ls -l -d " + pm.getApplicationInfo(packageName, 0).dataDir) {

                @Override
                public void output(int i, String s) {
                    if (!s.isEmpty()) {
                        list.add(s);
                    }
                }
            };

            shell.add(cmd).waitForFinish();

            String uid = Integer.toString(pm.getApplicationInfo(packageName, 0).uid);
			uid = uid.startsWith("1") ? uid.substring(1) : uid;
			uid = uid.startsWith("0") ? uid.substring(1) : uid;
			String app = list.get(0).split(" ")[1].replace("app_", "");
			
			if (Integer.parseInt(app) == -1 || list.get(0).split(" ")[0].length() != 10 || !uid.equals(app))
			{
                cmd = new CommandCapture(0, "chown " + pm.getApplicationInfo(packageName, 0).uid + "." + pm.getApplicationInfo(packageName, 0).uid + " " + pm.getApplicationInfo(packageName, 0).dataDir,
                        "chmod 755 " + pm.getApplicationInfo(packageName, 0).dataDir);
                shell.add(cmd).waitForFinish();

                for (String f : new File(pm.getApplicationInfo(packageName, 0).dataDir).list() ) {
					if (!f.equals("lib")) {
						if (new File(pm.getApplicationInfo(packageName, 0).dataDir + "/" + f).isDirectory()) {

                            cmd = new CommandCapture(0, "chown " + pm.getApplicationInfo(packageName, 0).uid + "." + pm.getApplicationInfo(packageName, 0).uid + " " + pm.getApplicationInfo(packageName, 0).dataDir + "/" + f);
                            shell.add(cmd).waitForFinish();

                            List<String> tmp = getFiles(f, pm.getApplicationInfo(packageName, 0).dataDir);
							if (tmp != null) {
								for (String file : tmp) {
									RootTools.log(file);

                                    cmd = new CommandCapture(0, "chown " + pm.getApplicationInfo(packageName, 0).uid + "." + pm.getApplicationInfo(packageName, 0).uid + " " + pm.getApplicationInfo(packageName, 0).dataDir + "/" + file);
                                    shell.add(cmd).waitForFinish();
								}
							}
						}
						else {
                            cmd = new CommandCapture(0, "chown " + pm.getApplicationInfo(packageName, 0).uid + "." + pm.getApplicationInfo(packageName, 0).uid + " " + pm.getApplicationInfo(packageName, 0).dataDir + "/" + f);
                            shell.add(cmd).waitForFinish();
						}
		
					}
				}
			}
						
		} catch (Exception e) {
            //Ignore, we don't care so much if this fails...
            RootTools.log("Fixing permissions failed. " + e.getMessage());
            e.printStackTrace();
        }
	}
	
	//So this will go through all of the directories and return the files.
	private static List<String> getFiles(String dir, String parentPath) {
		RootTools.log("Looking for files in " + parentPath + "/" + dir);
		try {

            Command cmd = new CommandCapture(0, "chmod 755 " + parentPath + "/" + dir);
            shell.add(cmd).waitForFinish();

		} catch (Exception e) {
			e.printStackTrace();
		}
		List<String> list = new ArrayList<String>();
			if (new File(parentPath + "/" + dir).list() != null) {
				for (String f : new File(parentPath + "/" + dir).list()) {
					RootTools.log(f);
					if (new File(parentPath + "/" + dir + "/" + f).isDirectory()) {
						list.add(f);
						List<String> tmp = getFiles(f, dir);
						if (tmp != null) {
							for (String file : tmp) {
								RootTools.log(file);
								list.add(file);
							}
						}
					} else {
						list.add(dir + "/" + f);
					}
				}
			}
		return list;
	}

}
