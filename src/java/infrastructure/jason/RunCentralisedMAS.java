//----------------------------------------------------------------------------
// Copyright (C) 2003  Rafael H. Bordini and Jomi F. Hubner
// 
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
// 
// To contact the authors:
// http://www.inf.ufrgs.br/~bordini
// http://www.das.ufsc.br/~jomi
//
//----------------------------------------------------------------------------

package infrastructure.jason;

import jason.JasonException;
import jason.asSemantics.Agent;
import jason.asSyntax.directives.DirectiveProcessor;
import jason.asSyntax.directives.Include;
import jason.control.ExecutionControlGUI;
import jason.jeditplugin.Config;
import jason.mas2j.AgentParameters;
import jason.mas2j.ClassParameters;
import jason.mas2j.MAS2JProject;
import jason.mas2j.parser.ParseException;
import jason.runtime.Settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import java.util.logging.Logger;

import javax.swing.JOptionPane;


/**
 * Runs MASProject using centralised infrastructure.
 */
public class RunCentralisedMAS {

    public final static String       logPropFile     = "logging.properties";
    public final static String       stopMASFileName = ".stop___MAS";
    public final static String       defaultProjectFileName = "default.mas2j";

    private   static Logger            logger        = Logger.getLogger(RunCentralisedMAS.class.getName());
    protected static RunCentralisedMAS runner        = null;
    private   static String            urlPrefix     = "";
    private   static boolean           readFromJAR   = false;
    private   static MAS2JProject      project;
    private   static boolean           debug         = false;
    
    private CentralisedEnvironment        env         = null;
    private CentralisedExecutionControl   control     = null;
    private Map<String,CentralisedAgArch> ags         = new ConcurrentHashMap<String,CentralisedAgArch>();

    
    public RunCentralisedMAS() {
        runner = this;  
    }
    
        
    public int init(String[] args) {
        String projectFileName = null;
        if (args.length < 1) {
            if (RunCentralisedMAS.class.getResource("/"+defaultProjectFileName) != null) {
                projectFileName = defaultProjectFileName;
                readFromJAR = true;
                Config.get(false); // to void to call fix/store the configuration in this case everything is read from a jar/jnlp file
            } else {
                System.out.println("Jason "+Config.get().getJasonRunningVersion());
                System.err.println("You should inform the MAS project file.");
                JOptionPane.showMessageDialog(null,"Jason version "+Config.get().getJasonRunningVersion()+" library built on "+Config.get().getJasonBuiltDate(),"Jason", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        } else {
            projectFileName = args[0];
        }
        
        if (args.length >= 2) {
            if (args[1].equals("-debug")) {
                debug = true;
                Logger.getLogger("").setLevel(Level.FINE);
            }
        }

        int errorCode = 0;

        try {
            InputStream inProject;
            if (readFromJAR) {
                inProject = RunCentralisedMAS.class.getResource("/"+defaultProjectFileName).openStream();
                urlPrefix = Include.CRPrefix + "/";
            } else {
                URL file;
                // test if the argument is an URL
                try {
                    file = new URL(projectFileName);
                    if (projectFileName.startsWith("jar")) {
                        urlPrefix = projectFileName.substring(0,projectFileName.indexOf("!")+1) + "/";
                    }
                } catch (Exception e) {
                    file = new URL("file:"+projectFileName);
                }
                inProject = file.openStream();
            }
            jason.mas2j.parser.mas2j parser = new jason.mas2j.parser.mas2j(inProject); 
            project = parser.mas();
            project.setupDefault();

            project.registerDirectives();
            // set the aslSrcPath in the include
            ((Include)DirectiveProcessor.getDirective("include")).setSourcePath(project.getSourcePaths());
            
            project.fixAgentsSrc(urlPrefix);

//            if (MASConsoleGUI.hasConsole()) {
//                MASConsoleGUI.get().setTitle("MAS Console - " + project.getSocName());
//
//                createButtons();
//            }

            //runner.waitEnd();
            errorCode = 0;

        } catch (FileNotFoundException e1) {
            logger.log(Level.SEVERE, "File " + projectFileName + " not found!");
            errorCode = 2;
        } catch (ParseException e) {
            logger.log(Level.SEVERE, "Error parsing file " + projectFileName + "!", e);
            errorCode = 3;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error!?: ", e);
            errorCode = 4;
        }
        
        System.out.flush();
        System.err.flush();

//        if (!MASConsoleGUI.hasConsole() && errorCode != 0) {
//            System.exit(errorCode);
//        }
        return errorCode;
    }

    /** create environment, agents, controller */
    public void create() throws JasonException {
        createEnvironment();
        createAgs();
        createController();        
    }
    
    /** start agents, .... */
    public void start() {
        startAgs();
        startSyncMode();
    }
    
    public static boolean isDebug() {
        return debug;
    }


    public static RunCentralisedMAS getRunner() {
        return runner;
    }

    public CentralisedExecutionControl getControllerInfraTier() {
        return control;
    }

    public CentralisedEnvironment getEnvironmentInfraTier() {
        return env;
    }
    
    public MAS2JProject getProject() {
        return project;
    }

    public void createEnvironment() throws JasonException {
        logger.fine("Creating environment " + project.getEnvClass());
        env = new CentralisedEnvironment(project.getEnvClass(), this);
    }
    
    public void createAgs() throws JasonException {
        boolean isPool = project.getInfrastructure().hasParameter("pool");
        if (isPool) logger.info("Creating agents....");
        int nbAg = 0;
        Agent pag = null;
        
        // create the agents
        for (AgentParameters ap : project.getAgents()) {
            try {
                
                String agName = ap.name;

                for (int cAg = 0; cAg < ap.qty; cAg++) {
                    nbAg++;
                    
                    String numberedAg = agName;
                    if (ap.qty > 1) {
                        numberedAg += (cAg + 1);
                        // cannot add zeros before, it causes many compatibility problems and breaks dynamic creation 
                        // numberedAg += String.format("%0"+String.valueOf(ap.qty).length()+"d", cAg + 1);
                    }
                    logger.fine("Creating agent " + numberedAg + " (" + (cAg + 1) + "/" + ap.qty + ")");
                    CentralisedAgArch agArch;
                    if (isPool) {
                        agArch = new CentralisedAgArchForPool();
                    } else {
                        agArch = new CentralisedAgArch();
                    }
                    agArch.setAgName(numberedAg);
                    agArch.setEnvInfraTier(env);
                    if (isPool && cAg > 0) {
                        // creation by cloning previous agent (which is faster -- no parsing, for instance)
                        agArch.createArchs(ap.getAgArchClasses(), pag, this);
                    } else {
                        // normal creation
                        agArch.createArchs(ap.getAgArchClasses(), ap.agClass.getClassName(), ap.getBBClass(), ap.asSource.toString(), ap.getAsSetts(debug, project.getControlClass() != null), this);
                    }
                    addAg(agArch);
                    
                    pag = agArch.getTS().getAg();
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error creating agent " + ap.name, e);
            }
        }
        
        if (isPool) logger.info("Created "+nbAg+" agents.");
    }

    public void createController() throws JasonException {
        ClassParameters controlClass = project.getControlClass();
        if (debug && controlClass == null) {
            controlClass = new ClassParameters(ExecutionControlGUI.class.getName());
        }
        if (controlClass != null) {
            logger.fine("Creating controller " + controlClass);
            control = new CentralisedExecutionControl(controlClass, this);
        }        
    }
    
    public void addAg(CentralisedAgArch ag) {
        ags.put(ag.getAgName(), ag);
    }
    public CentralisedAgArch delAg(String agName) {
        return ags.remove(agName);
    }
    
    public CentralisedAgArch getAg(String agName) {
        return ags.get(agName);
    }
    
    public Map<String,CentralisedAgArch> getAgs() {
        return ags;
    }
    
    protected void startAgs() {
        // run the agents
        if (project.getInfrastructure().hasParameter("pool")) {
            createThreadPool();
        } else {
            createAgsThreads();
        }
    }
    
    /** creates one thread per agent */
    private void createAgsThreads() {
        for (CentralisedAgArch ag : ags.values()) {
            ag.setControlInfraTier(control);
            
            // create the agent thread
            Thread agThread = new Thread(ag);
            ag.setThread(agThread);
            agThread.start();
        }        
    }
    
    private Set<CentralisedAgArch> sleepingAgs;
    private ExecutorService executor;
    
    /** creates a pool of threads shared by all agents */
    private void createThreadPool() {
        sleepingAgs = Collections.synchronizedSet(new HashSet<CentralisedAgArch>());

        int maxthreads = 10;
        try {
            if (project.getInfrastructure().hasParameters()) {
                maxthreads = Integer.parseInt(project.getInfrastructure().getParameter(1));
                logger.info("Creating a thread pool with "+maxthreads+" thread(s).");
            }
        } catch (Exception e) {
            logger.warning("Error getting the number of thread for the pool.");
        }

        // define pool size
        int poolSize = ags.size();
        if (poolSize > maxthreads) {
            poolSize = maxthreads;
        }
        
        // create the pool
        executor = Executors.newFixedThreadPool(poolSize);

        // initially, add all agents in the tasks
        for (CentralisedAgArch ag : ags.values()) {
            executor.execute(ag);
        }
        

    }
    
    /** an agent architecture for the infra based on thread pool */
    private final class CentralisedAgArchForPool extends CentralisedAgArch {

        private volatile boolean runWakeAfterTS = false;
        
        @Override
        public void sleep() {
            sleepingAgs.add(this);     
        }

        @Override
        public void wake() {                
            if (sleepingAgs.remove(this)) {              
                executor.execute(this);
            } else {
                runWakeAfterTS = true;
            }
        }
        
        
        @Override
        public void run() {
            if (isRunning()) { 
                runWakeAfterTS = false;
                if (getTS().reasoningCycle()) { // the agent run a cycle (did not enter in sleep)
                    executor.execute(this);
                } else if (runWakeAfterTS) {
                    wake();
                }
            }
        }        
    }
    
    protected void stopAgs() {
        // stop the agents
        for (CentralisedAgArch ag : ags.values()) {
            ag.stopAg();
        }
    }

    /** change the current running MAS to debug mode */
    void changeToDebugMode() {
        try {
            if (control == null) {
                control = new CentralisedExecutionControl(new ClassParameters(ExecutionControlGUI.class.getName()), this);
                for (CentralisedAgArch ag : ags.values()) {
                    ag.setControlInfraTier(control);
                    Settings stts = ag.getTS().getSettings();
                    stts.setVerbose(2);
                    stts.setSync(true);
                    ag.getLogger().setLevel(Level.FINE);
                    ag.getTS().getLogger().setLevel(Level.FINE);
                    ag.getTS().getAg().getLogger().setLevel(Level.FINE);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error entering in debug mode", e);
        }
    }

    protected void startSyncMode() {
        if (control != null) {
            // start the execution, if it is controlled
            try {
                Thread.sleep(500); // gives a time to agents enter in wait
                control.informAllAgsToPerformCycle(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void waitEnd() {
        try {
            // wait a file called .stop___MAS to be created!
            File stop = new File(stopMASFileName);
            if (stop.exists()) {
                stop.delete();
            }
            while (!stop.exists()) {
                Thread.sleep(1500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void finish() {
        
        try {
            // creates a thread that guarantees system.exit(0) in 5 seconds
            // (the stop of agents can  block)
            new Thread() {
                public void run() {
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {}
                    System.exit(0);
                }
            }.start();
            
            System.out.flush();
            System.err.flush();


            if (control != null) {
                control.stop();
                control = null;
            }
            if (env != null) {
                env.stop();
                env = null;
            }
            
            stopAgs();

            runner = null;
            
            // remove the .stop___MAS file  (note that GUI console.close(), above, creates this file)
            File stop = new File(stopMASFileName);
            if (stop.exists()) {
                stop.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
    
    /** show the sources of the project */
/*    private static void showProjectSources(MAS2JProject project) {
        JFrame frame = new JFrame("Project "+project.getSocName()+" sources");
        JTabbedPane pane = new JTabbedPane();
        frame.getContentPane().add(pane);
        project.fixAgentsSrc(urlPrefix);

        for (AgentParameters ap : project.getAgents()) {
            try {
                String tmpAsSrc = ap.asSource.toString();
                
                // read sources
                InputStream in = null;
                if (tmpAsSrc.startsWith(Include.CRPrefix)) {
                    in = RunCentralisedMAS.class.getResource(tmpAsSrc.substring(Include.CRPrefix.length())).openStream();
                } else {
                    try {
                        in = new URL(tmpAsSrc).openStream(); 
                    } catch (MalformedURLException e) {
                        in = new FileInputStream(tmpAsSrc);
                    }
                }
                StringBuilder s = new StringBuilder();
                int c = in.read();
                while (c > 0) {
                    s.append((char)c);
                    c = in.read();
                }
                
                // show sources
                JTextArea ta = new JTextArea(40,50);
                ta.setEditable(false);
                ta.setText(s.toString());
                ta.setCaretPosition(0);
                JScrollPane sp = new JScrollPane(ta);
                pane.add(ap.name, sp);
            } catch (Exception e) {
                logger.info("Error:"+e);
            }
        }
        frame.pack();
        frame.setVisible(true);
    }*/
}
