package infrastructure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import jason.infra.centralised.*;
import jason.jeditplugin.Config;
import jason.jeditplugin.MASLauncherInfraTier;
import jason.mas2j.MAS2JProject;
import jason.mas2j.parser.mas2j;

/**
 * Used to compile the jason code
 * 
 * @author phoebe
 *
 */
public class CustomMasLauncher extends CentralisedMASLauncherAnt {

	public CustomMasLauncher(){
		
	}
	
	public static void main(String[] args){
		mas2j masFile = null;
		String name ="C:\\Users\\phoebe\\workspace\\ecoAgents\\ecoAgents.mas2j";
		try {
			masFile = new mas2j(new FileInputStream(name));

		} catch (FileNotFoundException e) {
			System.out.println("whoops, no file");
			e.printStackTrace();
		}

		if (masFile == null) {
			return;
		}
		// following code adapted from mas2j.java, part of the jason source code
		MASLauncherInfraTier launcher = new CustomMasLauncher();
		Config config = Config.get();
		config.setJavaHome("C:\\Program Files\\Java\\jdk1.8.0_31");
		// parsing
		try {
			MAS2JProject project = masFile.mas();
			Config.get().fix();
			File file = new File(name);
			File directory = file.getAbsoluteFile().getParentFile();
			project.setDirectory(directory.toString());
			project.setProjectFile(file);
			//launcher = project.getInfrastructureFactory().createMASLauncher();
			launcher.setProject(project);
			launcher.writeScripts(false);

		} catch (Exception e) {
			System.err.println("mas2j: parsing errors found... \n" + e);
		}
		// end of mas2j.java code

//		if (launcher == null) {
//			return;
//		}
		new Thread(launcher, "MAS-Launcher").start();
	}
	
	@Override
	public void stopMAS() {
		try {
			// creating this file will stop the MAS, the runner checks for this
			// file creation
			File stop = new File(project.getDirectory() + File.separator
					+ RunCentralisedMAS.stopMASFileName);
			stop.createNewFile();
		} catch (Exception e) {
			System.err.println("Error stoping RunCentMAS: " + e);
			e.printStackTrace();
		} finally {
			stop = true;
		}
	}

	@Override
	protected String replaceMarks(String script, boolean debug) {
		return readAntFile();
	}

	private String readAntFile() {
		File file = new File("C:\\Users\\phoebe\\workspace\\ecoAgents\\src\\java\\infrastructure\\antscript.xml");
		FileInputStream fis;
		String str = null;
		try {
			fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();
			str  = new String(data, "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
		
	}
	
	@Override
	public String[] getStartCommandArray() {
		System.out.println("removed run");
        String build = bindir+"build.xml";
        if (hasCBuild()) build = bindir+"c-build.xml";
        
        return new String[] { Config.get().getJavaHome() + "bin" + File.separator + "java", 
                "-classpath",
                Config.get().getAntLib() + "ant-launcher.jar", "org.apache.tools.ant.launch.Launcher", 
                "-e", "-v", "-f", build};
    }
}
