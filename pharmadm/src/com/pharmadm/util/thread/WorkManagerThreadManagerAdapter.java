/*
 * WorkManagerThreadManagerAdapter.java
 *
 * Created on June 13, 2005, 5:36 PM
 *
 * (C) PharmaDM n.v.  All rights reserved.
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.thread;

import com.pharmadm.util.work.ContinuationArbiter;
import com.pharmadm.util.work.WorkManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * A class to adapt a WorkManager to look like a ThreadManager.
 * This is to avoid rewriting large parts of DMax while still being able to execute them
 * in a new application that uses a WorkManager.
 *
 * This class extends ThreadManager, but ignores the entire implementation of its superclass
 * and delegates all tasks to its WorkManager back-end.  It's a pity that ThreadManager isn't an interface,
 * because then we wouldn't have to instantiate an unused superclass.
 *
 * Throughout this class, tWork means
 *
 * @author kdg
 */
public class WorkManagerThreadManagerAdapter extends ThreadManager {
    
    private final WorkManager wMan;
    private Map<Work, WorkManagerWorkAdapter> workToAdapter = Collections.synchronizedMap(new HashMap());
    
    public WorkManagerThreadManagerAdapter(WorkManager wManager) {
        super(null, false);
        this.wMan = wManager;
    }
    
    public void addWork(Work tWork) {
        WorkManagerWorkAdapter wWork = new WorkManagerWorkAdapter(tWork);
        wMan.execute(wWork);
    }
    
    public void addWorkAndWait(Work tWork) {
        WorkManagerWorkAdapter wWork = new WorkManagerWorkAdapter(tWork);
        wMan.executeAndWait(wWork);
    }
    
    public void addWorks(Vector tWorks) {
        ArrayList<WorkManagerWorkAdapter> wWorks = new ArrayList(tWorks.size());
        Iterator tWorksIter = tWorks.iterator();
        while (tWorksIter.hasNext()) {
            Work tWork = (Work)tWorksIter.next();
            wWorks.add(new WorkManagerWorkAdapter(tWork));
        }
        wMan.executeAll(wWorks);
    }
    
    public void addWorksAndWait(Vector tWorks) {
        ArrayList<WorkManagerWorkAdapter> wWorks = new ArrayList(tWorks.size());
        Iterator tWorksIter = tWorks.iterator();
        while (tWorksIter.hasNext()) {
            Work tWork = (Work)tWorksIter.next();
            wWorks.add(new WorkManagerWorkAdapter(tWork));
        }
        wMan.executeAllAndWait(wWorks);
    }
    
    public void abortAll() {
        wMan.abortAll();
    }
    
    public void systemExitIfIdle() {
        wMan.systemExitIfIdle();
    }
    
    public boolean mayContinueLight(Work tWork) {
        WorkManagerWorkAdapter wWork = workToAdapter.get(tWork);
        return wWork.getContinuationArbiter().mayContinueLight();
    }
    
    public boolean mayContinue(Work tWork) {
        WorkManagerWorkAdapter wWork = workToAdapter.get(tWork);
        return wWork.getContinuationArbiter().mayContinue();
    }
    
    private class WorkManagerWorkAdapter implements com.pharmadm.util.work.Work {
        
        private Work tWork;
        private ContinuationArbiter arbiter;
        
        public WorkManagerWorkAdapter(Work tWork) {
            this.tWork = tWork;
            workToAdapter.put(tWork, this);
        }
        
        public void execute() throws InterruptedException {
            tWork.execute();
            workToAdapter.remove(tWork);
        }
        
        public int getAmountDone() {
            return tWork.getAmountDone();
        }
        
        public String getDescription() {
            return tWork.getDescription();
        }
        
        public int getTotalAmount() {
            return tWork.getTotalAmount();
        }
        
        public boolean isAbortable() {
            return tWork.canAbort();
        }
        
        public boolean isInterruptible() {
            // ThreadManager Works don't support interrupts
            return false;
        }
        
        public boolean isPausable() {
            return tWork.canAbort();
        }
        
        public void setContinuationArbiter(com.pharmadm.util.work.ContinuationArbiter arbiter) {
            this.arbiter = arbiter;
        }
        
        public ContinuationArbiter getContinuationArbiter() {
            return arbiter;
        }
    }
}
