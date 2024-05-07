//package simpledb;

//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Random;
//import java.util.Set;

//import simpledb.TransactionId;

/**
 * LockManager is class which manages locks for transactions.
 * It stores states of various locks on pages and provides atomic grant
 * and release of locks.
 * @author hrishi
 */
//public class LockManager {
    
  //  private static final int BLOCK_DELAY_SHORT = 10;
   // private static final int BLOCK_DELAY_LONG = 100;
    //private static final int MAX_TRIES_SMALL = 250;
    //private static final int MAX_TRIES_LARGE = 500;
    //private static final int RAND_RANGE = 10;
    
    //private HashMap<PageId, Set<TransactionId>> readLocks;
    //private HashMap<PageId, TransactionId> writeLock;
    //private HashMap<TransactionId, Set<PageId>> sharedPages;
   // private HashMap<TransactionId, Set<PageId>> exclusivePages;
   // private HashMap<TransactionId, Thread> transactionThread;

    //public LockManager() {
      //  readLocks = new HashMap<PageId, Set<TransactionId>>();
       // writeLock = new HashMap<PageId, TransactionId>();
        //sharedPages = new HashMap<TransactionId, Set<PageId>>();
        //exclusivePages = new HashMap<TransactionId, Set<PageId>>();
        //transactionThread = new HashMap<TransactionId, Thread>();
    //}
    
    /**
     * Checks if transaction has lock on a page
     * @param tid Transaction Id
     * @param pid Page Id
     * @return boolean True if holds lock
     */
    //public boolean holdsLock(TransactionId tid, PageId pid){
      //  if(readLocks.containsKey(pid) && readLocks.get(pid).contains(tid))
        //    return true;
        //if(writeLock.containsKey(pid) && writeLock.get(pid).equals(tid))
         //   return true;
        //return false;
    //}
    
    //private void addLock(TransactionId tid, PageId pid, Permissions pm){
      //  if(pm.equals(Permissions.READ_ONLY)){
        //    if(!readLocks.containsKey(pid))
          //      readLocks.put(pid, new HashSet<TransactionId>());
            //readLocks.get(pid).add(tid);
            //if(!sharedPages.containsKey(tid))
              //  sharedPages.put(tid, new HashSet<PageId>());
            //sharedPages.get(tid).add(pid);
            //return;
        //}
       // writeLock.put(pid, tid);
       // if(!exclusivePages.containsKey(tid))
         //   exclusivePages.put(tid, new HashSet<PageId>());
        //exclusivePages.get(tid).add(pid);
    //}
    
    //private void abortReadLocks(TransactionId requestingTid, PageId pid) 
      //      throws IOException{
       // if(!readLocks.containsKey(pid)) return;
        //List<TransactionId> tids = new ArrayList<TransactionId>();
        //for(TransactionId tid: readLocks.get(pid))
         //   tids.add(tid);
        //readLocks.get(pid).clear();
        //for(TransactionId tid: tids)
          //  if(!tid.equals(requestingTid))
            //    transactionThread.get(tid).interrupt();
    //}
    
    /**
     * Grants lock to the Transaction.
     * @param tid TransactionId requesting lock.
     * @param pid PageId on which the lock is requested.
     * @param pm The type of permission.
     * @return boolean True if lock is successfully granted.
     */
    //public synchronized boolean grantLock(TransactionId tid, PageId pid,
      //      Permissions pm) {
       // return grantLock(tid, pid, pm, false);
   // }
    
    /**
     * Grants lock to the Transaction.
     * @param tid TransactionId requesting lock.
     * @param pid PageId on which the lock is requested.
     * @param pm The type of permission.
     * @param force Whether to force give this lock
     * @return boolean True if lock is successfully granted.
     */
    //private synchronized boolean grantLock(TransactionId tid, PageId pid, 
      //      Permissions pm, boolean force) {
        // If Page requested is new and not in lock
        //if( ( !readLocks.containsKey(pid) || readLocks.get(pid).isEmpty() ) && 
          //      !writeLock.containsKey(pid)){
            // We can grant any kind of lock
            //addLock(tid, pid, pm);
            //return true;
        //}
        // Page in lock
        // If requested permission is read
        //if(pm.equals(Permissions.READ_ONLY)){
            // If page permission is read write 
            // then we cannot give the lock before release of write lock.
          //  if(writeLock.containsKey(pid) && writeLock.get(pid) != tid)
            //    return false;
            // Else the page permission is read 
            // we can give the lock.
            //addLock(tid, pid, pm);
            //return true;
        //}
        // Requested permission is write.
        // This lock can only be given iff there is no other transaction with 
        // any kind of lock on that page. But this can't happen as we
        // handled that case above.
        // This means we can only give lock iff tid is only transaction on the
        // requested page
        //if(readLocks.containsKey(pid) && 
          //      readLocks.get(pid).contains(tid) &&
            //    readLocks.get(pid).size() == 1){
           // addLock(tid, pid, pm);
            //return true;
        //}
        // But if transaction tid has already a write lock
        // then the result is already success
        //if(exclusivePages.containsKey(tid) &&
          //      exclusivePages.get(tid).contains(pid)){
           // return true;
        //}
        // In case this call is a force call for write lock
        // Check if there is no write lock
        //if(force && 
          //      pm.equals(Permissions.READ_WRITE) &&
            //    !writeLock.containsKey(pid)){
            //try{
              //  abortReadLocks(tid, pid);
                //addLock(tid, pid, pm);
                //return true;
            //}catch(IOException e){
              //  return false;
            //}
       // }
        // In all other cases we fail to grant lock.
       // return false;
   // }
    
    //public void requestLock(TransactionId tid, PageId pid, 
      //      Permissions perm) throws TransactionAbortedException{
       // int blockDelay = BLOCK_DELAY_LONG;
        //int maxTries = MAX_TRIES_SMALL;
        // Check if old tid
        //if(sharedPages.containsKey(tid) || 
          //      exclusivePages.containsKey(tid)){
            //blockDelay = BLOCK_DELAY_SHORT;
            //maxTries = MAX_TRIES_LARGE;
        //}
        // Add this thread to map
        //if(!transactionThread.containsKey(tid))
          //  transactionThread.put(tid, Thread.currentThread());
        //boolean isGranted = this.grantLock(tid, pid, perm);
        //Random random = new Random(System.currentTimeMillis());
        //long startTime = System.currentTimeMillis();
       // while(!isGranted){
         //   if(System.currentTimeMillis() - startTime > maxTries){
           //     if(perm.equals(Permissions.READ_ONLY)){
                    // Remove this thread from map
             //       transactionThread.remove(tid);
               //    throw new TransactionAbortedException();
                //}
                //else{
                  //  isGranted = this.grantLock(tid, pid, perm, true);
                    //startTime = System.currentTimeMillis();
                    //continue;
               // }
            //}
            //try {
                // Block thread
              //  Thread.sleep(blockDelay + random.nextInt(RAND_RANGE));
            //} catch (InterruptedException ex) {
              //  transactionThread.remove(tid);
                //throw new TransactionAbortedException();
            //}
            //isGranted = this.grantLock(tid, pid, perm);
        //}
    //}
    
    
    
    /**
     * Releases locks associated with given transaction and page.
     * @param tid The TransactionId.
     * @param pid The PageId.
     */
    //public synchronized void releaseLock(TransactionId tid, PageId pid){
      //  if(readLocks.containsKey(pid))
       //     readLocks.get(pid).remove(tid);
        //writeLock.remove(pid);
        //if(sharedPages.containsKey(tid))
         //   sharedPages.get(tid).remove(pid);
        //if(exclusivePages.containsKey(tid))
         //   exclusivePages.get(tid).remove(pid);
    //}
    
    /**
     * Releases Lock related to a page
     * @param pid PageId
     */
   // public synchronized void removePage(PageId pid){
     //   readLocks.remove(pid);
      //  writeLock.remove(pid);
    //}
    
    /**
     * Releases all pages associated with given Transaction.
     * @param tid The TransactionId.
     */
    //public void releaseAllPages(TransactionId tid){
      //  if(sharedPages.containsKey(tid)){
        //    for(PageId pid: sharedPages.get(tid))
          //      readLocks.get(pid).remove(tid);
           // sharedPages.remove(tid);
        //}
        //if(exclusivePages.containsKey(tid)){
          //  for(PageId pid: exclusivePages.get(tid))
            //    writeLock.remove(pid);
            //exclusivePages.remove(tid);
        //}
    //}
    
//}
package simpledb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import simpledb.utils.Pair;

public class LockManager {
  public enum LockType {shared, exclusive}

  private static class Lock {
    private LockType type;
    private final ArrayList<TransactionId> holders;

    public Lock() {
      this.type = LockType.shared;
      this.holders = new ArrayList<>();
    }
  }

  private static class DependencyGraph {
    private final HashMap<TransactionId, ArrayList<TransactionId>> edgesOut;

    public DependencyGraph() {
      edgesOut = new HashMap<>();
    }

    private void dfs(TransactionId tid, TransactionId start, HashSet<TransactionId> visited)
        throws TransactionAbortedException {
      ArrayList<TransactionId> tos = edgesOut.get(tid);
      if (tos == null) {
        return;
      }
      for (TransactionId to : tos) {
        if (to == start) {
          throw new TransactionAbortedException();
        }
        if (visited.contains(to)) {
          continue;
        }
        visited.add(to);
        dfs(to, start, visited);
      }
    }

    private void findCycle(TransactionId tid) throws TransactionAbortedException {
      HashSet<TransactionId> visited = new HashSet<>();
      visited.add(tid);
      dfs(tid, tid, visited);
    }

    public void setEdges(TransactionId from, ArrayList<TransactionId> tos)
        throws TransactionAbortedException {
      edgesOut.putIfAbsent(from, new ArrayList<>());
      ArrayList<TransactionId> edges = edgesOut.get(from);
      ArrayList<TransactionId> oldEdges = new ArrayList<>(edges);
      oldEdges.removeAll(tos);
      for (TransactionId oldTo : oldEdges) {
        edges.remove(oldTo);
      }
      boolean changed = false;
      for (TransactionId to : tos) {
        if (from == to) {
          continue;
        }
        if (edges.contains(to)) {
          continue;
        }
        edges.add(to);
        changed = true;
      }
      if (changed) {
        findCycle(from);
      }
    }

    public void removeOutEdges(TransactionId from) {
      ArrayList<TransactionId> tos = edgesOut.get(from);
      if (tos != null) {
        tos.clear();
      }
    }
  }

  private final ConcurrentHashMap<TransactionId, ArrayList<PageId>> tid2LockedPages;
  private final ConcurrentHashMap<PageId, Lock> page2Lock;
  private final DependencyGraph depGraph;

  public LockManager() {
    tid2LockedPages = new ConcurrentHashMap<>();
    page2Lock = new ConcurrentHashMap<>();
    depGraph = new DependencyGraph();
  }

  public boolean holdsLock(TransactionId tid, PageId pid, Permissions perm) {
    if (page2Lock.get(pid) == null) {
      assert tid2LockedPages.get(tid) == null || !tid2LockedPages.get(tid).contains(pid);
      return false;
    }
    Lock lock = page2Lock.get(pid);
    assert lock != null;

    synchronized (lock) {
      if (!lock.holders.contains(tid)) {
        assert tid2LockedPages.get(tid) == null || !tid2LockedPages.get(tid).contains(pid);
        return false;
      }
      boolean res = perm == Permissions.READ_ONLY || lock.type == LockType.exclusive;
      if (res) {
        assert tid2LockedPages.get(tid) != null && tid2LockedPages.get(tid).contains(pid);
      } else {
        assert tid2LockedPages.get(tid) == null || !tid2LockedPages.get(tid).contains(pid) || (
            perm == Permissions.READ_WRITE && lock.type == LockType.shared);
      }
      return res;
    }
  }

  private synchronized Lock getLock(PageId pid) {
    if (page2Lock.get(pid) != null) {
      return page2Lock.get(pid);
    }
    Lock lock = new Lock();
    page2Lock.put(pid, lock);
    return lock;
  }

  private void addLock(TransactionId tid, PageId pid) {
    tid2LockedPages.putIfAbsent(tid, new ArrayList<>());
    ArrayList<PageId> pageIds = tid2LockedPages.get(tid);
    assert !pageIds.contains(pid);
    pageIds.add(pid);
  }

  public void acquireLock(TransactionId tid, PageId pid, Permissions perm)
      throws TransactionAbortedException {
    if (holdsLock(tid, pid, perm)) {
      return;
    }
    if (perm == Permissions.READ_ONLY) {
      acquireSLock(tid, pid);
    } else {
      acquireXLock(tid, pid);
    }
  }

  private void acquireSLock(TransactionId tid, PageId pid) throws TransactionAbortedException {
    Lock lock = getLock(pid);
    while (true) {
      synchronized (lock) {
        if (lock.holders.isEmpty()) {
          lock.type = LockType.shared;
          lock.holders.add(tid);
          addLock(tid, pid);
          break;
        } else if (lock.type == LockType.shared) {
          lock.holders.add(tid);
          addLock(tid, pid);
          break;
        } else {
          assert lock.type == LockType.exclusive;
          assert lock.holders.size() == 1;
          assert lock.holders.get(0) != tid;
          synchronized (depGraph) {
            depGraph.setEdges(tid, new ArrayList<TransactionId>() {{
              add(lock.holders.get(0));
            }});
          }
        }
      }
    }
    synchronized (depGraph) {
      depGraph.removeOutEdges(tid);
    }
  }

  private void acquireXLock(TransactionId tid, PageId pid) throws TransactionAbortedException {
    Lock lock = getLock(pid);
    while (true) {
      synchronized (lock) {
        if (lock.holders.isEmpty()) {
          lock.type = LockType.exclusive;
          lock.holders.add(tid);
          addLock(tid, pid);
          break;
        } else if (lock.holders.size() == 1 && lock.holders.get(0) == tid) {
          lock.type = LockType.exclusive;
          break;
        } else {
          assert lock.holders.size() == 1 || lock.type == LockType.shared;
          synchronized (depGraph) {
            depGraph.setEdges(tid, lock.holders);
          }
        }
      }
    }
    synchronized (depGraph) {
      depGraph.removeOutEdges(tid);
    }
  }

  public void releaseLock(TransactionId tid, PageId pid) {
    assert holdsLock(tid, pid, Permissions.READ_ONLY);
    Lock lock = getLock(pid);

    synchronized (lock) {
      assert lock.holders.contains(tid);
      ArrayList<PageId> heldLocks = tid2LockedPages.get(tid);
      assert heldLocks != null && heldLocks.contains(pid);
      heldLocks.remove(pid);
      lock.holders.remove(tid);
    }
  }

  public ArrayList<Pair<PageId, LockType>> getLockedPages(TransactionId tid) {
    ArrayList<PageId> lockedPages = tid2LockedPages.get(tid);
    ArrayList<Pair<PageId, LockType>> res = new ArrayList<>();
    if (lockedPages == null) {
      return res;
    }
    for (PageId pid : lockedPages) {
      res.add(new Pair<>(pid, page2Lock.get(pid).type));
    }
    return res;
  }
}