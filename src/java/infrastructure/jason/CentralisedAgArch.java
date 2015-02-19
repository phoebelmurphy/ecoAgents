//----------------------------------------------------------------------------
// Copyright (C) 2003  Rafael H. Bordini, Jomi F. Hubner, et al.
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
import jason.ReceiverNotFoundException;
import jason.architecture.AgArch;
import jason.architecture.MindInspectorAgArch;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Agent;
import jason.asSemantics.Circumstance;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
import jason.mas2j.ClassParameters;
import jason.runtime.RuntimeServicesInfraTier;
import jason.runtime.Settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides an agent architecture when using Centralised
 * infrastructure to run the MAS inside Jason.
 * 
 * <p>
 * Execution sequence:
 * <ul>
 * <li>initAg,
 * <li>setEnvInfraTier,
 * <li>setControlInfraTier,
 * <li>run (perceive, checkMail, act),
 * <li>stopAg.
 * </ul>
 */
public class CentralisedAgArch extends AgArch implements Runnable {

    protected CentralisedEnvironment    infraEnv     = null;
    private CentralisedExecutionControl infraControl = null;
    private RunCentralisedMAS           masRunner    = RunCentralisedMAS.getRunner();

    private String           agName  = "";
    private volatile boolean running = true;
    private Queue<Message>   mbox    = new ConcurrentLinkedQueue<Message>(); 
    protected Logger         logger  = Logger.getLogger(CentralisedAgArch.class.getName());
    
    private static List<MsgListener> msgListeners = null;
    public static void addMsgListener(MsgListener l) {
        if (msgListeners == null) {
            msgListeners = new ArrayList<MsgListener>();
        }
        msgListeners.add(l);
    }
    public static void removeMsgListener(MsgListener l) {
        msgListeners.remove(l);
    }

    /**
     * Creates the user agent architecture, default architecture is
     * jason.architecture.AgArch. The arch will create the agent that creates
     * the TS.
     */
    public void createArchs(List<String> agArchClasses, String agClass, ClassParameters bbPars, String asSrc, Settings stts, RunCentralisedMAS masRunner) throws JasonException {
        try {
            this.masRunner = masRunner;
            Agent.create(this, agClass, bbPars, asSrc, stts);
            insertAgArch(this);
            
            createCustomArchs(agArchClasses);

            // mind inspector arch
            if (stts.getUserParameter("mindinspector") != null) {
                insertAgArch(new MindInspectorAgArch());
                getFirstAgArch().init();
            }
            
            setLogger();
        } catch (Exception e) {
            running = false;
            throw new JasonException("as2j: error creating the agent class! - "+e.getMessage(), e);
        }
    }

    /** init the agent architecture based on another agent */
    public void createArchs(List<String> agArchClasses, Agent ag, RunCentralisedMAS masRunner) throws JasonException {
        try {
            this.masRunner = masRunner;
            setTS(ag.clone(this).getTS());
            insertAgArch(this);

            createCustomArchs(agArchClasses);

            setLogger();
        } catch (Exception e) {
            running = false;
            throw new JasonException("as2j: error creating the agent class! - ", e);
        }
    }
    
    
    public void stopAg() {
        running = false;
        if (myThread != null) 
            myThread.interrupt();
        synchronized (syncStopRun) {
            masRunner.delAg(agName);
        }
        getTS().getAg().stopAg();
        getUserAgArch().stop(); // stops all archs
    }

    public void setLogger() {
        logger = Logger.getLogger(CentralisedAgArch.class.getName() + "." + getAgName());
        if (getTS().getSettings().verbose() >= 0)
            logger.setLevel(getTS().getSettings().logLevel());       
    }
    
    public Logger getLogger() {
        return logger;
    }

    public void setAgName(String name) {
        agName = name;
    }

    public String getAgName() {
        return agName;
    }
    
    public AgArch getUserAgArch() {
        return getFirstAgArch();
    }

    public void setEnvInfraTier(CentralisedEnvironment env) {
        infraEnv = env;
    }

    public CentralisedEnvironment getEnvInfraTier() {
        return infraEnv;
    }

    public void setControlInfraTier(CentralisedExecutionControl pControl) {
        infraControl = pControl;
    }

    public CentralisedExecutionControl getControlInfraTier() {
        return infraControl;
    }

    private Object syncStopRun = new Object();

    private Thread myThread = null;
    public void setThread(Thread t) { 
        myThread = t;
        myThread.setName(agName);
    }

    public boolean isRunning() {
        return running;
    }

    public void run() {
        synchronized (syncStopRun) {
            TransitionSystem ts = getTS();
            while (running) {
                if (ts.getSettings().isSync()) {
                    waitSyncSignal();
                    ts.reasoningCycle();
                    boolean isBreakPoint = false;
                    try {
                        isBreakPoint = ts.getC().getSelectedOption().getPlan().hasBreakpoint();
                        if (logger.isLoggable(Level.FINE)) logger.fine("Informing controller that I finished a reasoning cycle "+getCycleNumber()+". Breakpoint is " + isBreakPoint);
                    } catch (NullPointerException e) {
                        // no problem, there is no sel opt, no plan ....
                    }
                    informCycleFinished(isBreakPoint, getCycleNumber());
                } else {
                    incCycleNumber();
                    ts.reasoningCycle();
                }
            }
        }
        logger.fine("I finished!");
    }

    private Object sleepSync = new Object();
    
    public void sleep() {
        try {
            if (!getTS().getSettings().isSync()) {
                logger.fine("Entering in sleep mode....");
                synchronized (sleepSync) {
                    sleepSync.wait(500); // wait for messages
                }
            }
        } catch (InterruptedException e) {
        } catch (Exception e) {
            logger.log(Level.WARNING,"Error in sleep.", e);
        }
    }
    
    public void wake() {
        synchronized (sleepSync) {
            sleepSync.notifyAll(); // notify sleep method
        }
    }

    // Default perception assumes Complete and Accurate sensing.
    public List<Literal> perceive() {
        if (infraEnv == null) return null;
        List<Literal> percepts = infraEnv.getUserEnvironment().getPercepts(getAgName());
        if (logger.isLoggable(Level.FINE) && percepts != null) logger.fine("percepts: " + percepts);
        return percepts;
    }

    // this is used by the .send internal action in stdlib
    public void sendMsg(Message m) throws ReceiverNotFoundException {
        // actually send the message
        if (m.getSender() == null)  m.setSender(getAgName());
        
        CentralisedAgArch rec = masRunner.getAg(m.getReceiver());
            
        if (rec == null) {
            if (isRunning())
                throw new ReceiverNotFoundException("Receiver '" + m.getReceiver() + "' does not exists! Could not send " + m);
            else
                return;
        }
        rec.receiveMsg(m.clone()); // send a cloned message
    
        // notify listeners
        if (msgListeners != null) 
            for (MsgListener l: msgListeners) 
                l.msgSent(m);
    }
    
    public void receiveMsg(Message m) {
        mbox.offer(m);
        wake();     
    }

    public void broadcast(jason.asSemantics.Message m) throws Exception {
        for (String agName: masRunner.getAgs().keySet()) {
            if (!agName.equals(this.getAgName())) {
                m.setReceiver(agName);
                sendMsg(m);
            }
        }
    }

    // Default procedure for checking messages, move message from local mbox to C.mbox
    public void checkMail() {
        Circumstance C = getTS().getC();
        Message im = mbox.poll();
        while (im != null) {
            C.addMsg(im);
            if (logger.isLoggable(Level.FINE)) logger.fine("received message: " + im);
            im = mbox.poll();
        }
    }
    
    public Collection<Message> getMBox() {
        return mbox;
    }

    /** called by the TS to ask the execution of an action in the environment */
    public void act(ActionExec action, List<ActionExec> feedback) {
        if (logger.isLoggable(Level.FINE)) logger.info("doing: " + action.getActionTerm());
        if (isRunning() && infraEnv != null)
            infraEnv.act(getAgName(), action);
    }
    
    /** called the the environment when the action was executed */
    public void actionExecuted(ActionExec action) {
        getTS().getC().addFeedbackAction(action);
        wake();
    }

    public boolean canSleep() {
        return mbox.isEmpty() && isRunning();
    }

    private Object  syncMonitor                = new Object(); 
    private volatile boolean inWaitSyncMonitor = false;

    /**
     * waits for a signal to continue the execution (used in synchronised
     * execution mode)
     */
    private void waitSyncSignal() {
        try {
            synchronized (syncMonitor) {
                inWaitSyncMonitor = true;
                syncMonitor.wait();
                inWaitSyncMonitor = false;
            }
        } catch (InterruptedException e) {
        } catch (Exception e) {
            logger.log(Level.WARNING,"Error waiting sync (1)", e);
        }
    }

    /**
     * inform this agent that it can continue, if it is in sync mode and
     * waiting a signal
     */
    public void receiveSyncSignal() {
        try {
            synchronized (syncMonitor) {
                while (!inWaitSyncMonitor && isRunning()) {
                    // waits the agent to enter in waitSyncSignal
                    syncMonitor.wait(50); 
                }
                syncMonitor.notifyAll();
            }
        } catch (InterruptedException e) {
        } catch (Exception e) {
            logger.log(Level.WARNING,"Error waiting sync (2)", e);
        }
    }

    /** 
     *  Informs the infrastructure tier controller that the agent 
     *  has finished its reasoning cycle (used in sync mode).
     *  
     *  <p><i>breakpoint</i> is true in case the agent selected one plan 
     *  with the "breakpoint" annotation.  
     */ 
    public void informCycleFinished(boolean breakpoint, int cycle) {
        infraControl.receiveFinishedCycle(getAgName(), breakpoint, cycle);
    }

    public RuntimeServicesInfraTier getRuntimeServices() {
        return new CentralisedRuntimeServices(masRunner);
    }
}
