package simpledb;

import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

//import java.util.concurrent.ConcurrentHashMap;

/**
 * BufferPool manages the reading and writing of pages into memory from
 * disk. Access methods call into it to retrieve pages, and it fetches
 * pages from the appropriate location.
 * <p>
 * The BufferPool is also responsible for locking;  when a transaction fetches
 * a page, BufferPool checks that the transaction has the appropriate
 * locks to read/write the page.
 * 
 * @Threadsafe, all fields are final
 */
public class BufferPool {
    /** Bytes per page, including header. */
    static final int PAGE_SIZE = 4096;

    private static int pageSize = PAGE_SIZE;
    
    /** Default number of pages passed to the constructor. This is used by
    other classes. BufferPool should use the numPages argument to the
    constructor instead. */
    public static final int DEFAULT_PAGES = 50;
    private int numPages;
    
    private volatile LRUCache<PageId, Page> pages;
    private volatile HashMap<TransactionId, Set<PageId>> pageTransactions;
    private volatile LockManager lockManager;
    /**
     * Creates a BufferPool that caches up to numPages pages.
     *
     * @param numPages maximum number of pages in this buffer pool.
     */
    public BufferPool(int numPages) {
        // some code goes here
    	this.numPages = numPages;
        pageTransactions = new HashMap<TransactionId, Set<PageId>>();
        lockManager = new LockManager();
        pages = new LRUCache<PageId, Page>(numPages){
            @Override
            public synchronized Page evict() {
                Iterator<Page> pages = this.values();
                while(pages.hasNext()){
                    Page page = pages.next();
                    if(page.isDirty() == null){
                        this.remove(page.getId());
                        return page;
                    }
                }
                return null;
            }
        };
    }
    
    public static int getPageSize() {
      return pageSize;
    }
    
    // THIS FUNCTION SHOULD ONLY BE USED FOR TESTING!!
    public static void setPageSize(int pageSize) {
    	BufferPool.pageSize = pageSize;
    }
    
    // THIS FUNCTION SHOULD ONLY BE USED FOR TESTING!!
    public static void resetPageSize() {
    	BufferPool.pageSize = PAGE_SIZE;
    }

    /**
     * Retrieve the specified page with the associated permissions.
     * Will acquire a lock and may block if that lock is held by another
     * transaction.
     * <p>
     * The retrieved page should be looked up in the buffer pool.  If it
     * is present, it should be returned.  If it is not present, it should
     * be added to the buffer pool and returned.  If there is insufficient
     * space in the buffer pool, an page should be evicted and the new page
     * should be added in its place.
     *
     * @param tid the ID of the transaction requesting the page
     * @param pid the ID of the requested page
     * @param perm the requested permissions on the page
     */
    public  Page getPage(TransactionId tid, PageId pid, Permissions perm)
        throws TransactionAbortedException, DbException {
        // some code goes here
    	lockManager.requestLock(tid, pid, perm);
        
        // If page already in buffer
        if(pages.get(pid) != null){
            // Check if transaction can acquire lock
            if(pageTransactions.get(tid) == null)  
                pageTransactions.put(tid, new HashSet<PageId>());
            pageTransactions.get(tid).add(pid);
            // Return the page
            return pages.get(pid);
        }
        
        // Page not in Buffer
        // Check if cache size is greeater than numPages
        if(pages.size() >= numPages)
            this.evictPage();
        // Add page to Buffer
        DbFile dbFile = Database.getCatalog().getDatabaseFile(pid.getTableId());
        pages.put(pid, dbFile.readPage(pid));
        // Add transaction id to page
        if(pageTransactions.get(tid) == null)
            pageTransactions.put(tid, new HashSet<PageId>());
        pageTransactions.get(tid).add(pid);
        // return page
        return pages.get(pid);
    }

    /**
     * Releases the lock on a page.
     * Calling this is very risky, and may result in wrong behavior. Think hard
     * about who needs to call this and why, and why they can run the risk of
     * calling it.
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param pid the ID of the page to unlock
     */
    public  void releasePage(TransactionId tid, PageId pid) {
        // some code goes here
        // not necessary for lab1|lab2
    }

    /**
     * Release all locks associated with a given transaction.
     *
     * @param tid the ID of the transaction requesting the unlock
     */
    public void transactionComplete(TransactionId tid) throws IOException {
        // some code goes here
        // not necessary for lab1|lab2
    }

    /** Return true if the specified transaction has a lock on the specified page */
    public boolean holdsLock(TransactionId tid, PageId p) {
        // some code goes here
        // not necessary for lab1|lab2
        return false;
    }

    /**
     * Commit or abort a given transaction; release all locks associated to
     * the transaction.
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param commit a flag indicating whether we should commit or abort
     */
    public void transactionComplete(TransactionId tid, boolean commit)
        throws IOException {
        // some code goes here
        // not necessary for lab1|lab2
    }

    /**
     * Add a tuple to the specified table on behalf of transaction tid.  Will
     * acquire a write lock on the page the tuple is added to and any other 
     * pages that are updated (Lock acquisition is not needed for lab2). 
     * May block if the lock(s) cannot be acquired.
     * 
     * Marks any pages that were dirtied by the operation as dirty by calling
     * their markDirty bit, and adds versions of any pages that have 
     * been dirtied to the cache (replacing any existing versions of those pages) so 
     * that future requests see up-to-date pages. 
     *
     * @param tid the transaction adding the tuple
     * @param tableId the table to add the tuple to
     * @param t the tuple to add
     */
    public void insertTuple(TransactionId tid, int tableId, Tuple t)
        throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        // not necessary for lab1
    	//DbFile file = Database.getCatalog().getDatabaseFile(tableId);
    	//ArrayList<Page> pageList = file.insertTuple(tid, t);
    	//for(Page page: pageList) {
    		//page.markDirty(true, tid);
    		//pages.put(page.getId(), page);
    		//pageTransactions.get(tid).add(page.getId());
    	//}
    	DbFile tableFile = Database.getCatalog().getDatabaseFile(tableId);
    	ArrayList<Page> dirtyPages = tableFile.insertTuple(tid, t);
    	System.out.println("Received " + dirtyPages.size() + " dirty pages.");
    	synchronized(this) {
    		for(Page p : dirtyPages) {
    			p.markDirty(true, tid);
    			System.out.println("Putting page " + p.getId().toString());
    			if(pages.get(p.getId()) != null) {
    				pages.put(p.getId(), p);
    			}
    			else {
    				if(pages.size() > numPages) {
    					evictPage();
    				}
    				pages.put(p.getId(), p);
    				
    			}
    		}
    	}
    }

    /**
     * Remove the specified tuple from the buffer pool.
     * Will acquire a write lock on the page the tuple is removed from and any
     * other pages that are updated. May block if the lock(s) cannot be acquired.
     *
     * Marks any pages that were dirtied by the operation as dirty by calling
     * their markDirty bit, and adds versions of any pages that have 
     * been dirtied to the cache (replacing any existing versions of those pages) so 
     * that future requests see up-to-date pages. 
     *
     * @param tid the transaction deleting the tuple.
     * @param t the tuple to delete
     */
    public  void deleteTuple(TransactionId tid, Tuple t)
        throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        // not necessary for lab1
    	//int tableId = t.getRecordId().getPageId().getTableId();
    	//DbFile file = Database.getCatalog().getDatabaseFile(tableId);
    	//ArrayList<Page> page = file.deleteTuple(tid, t);
    	//for(Page pg: page) {
    		//pg.markDirty(true, tid);
    		//pages.put(pg.getId(), pg);
    		//pageTransactions.get(tid).add(pg.getId());
    	//}
    	DbFile tableFile = Database.getCatalog().getDatabaseFile(t.getRecordId().getPageId().getTableId());
    	ArrayList<Page> dirtyPages = tableFile.deleteTuple(tid, t);

    	synchronized(this) {
    		for(Page p : dirtyPages) {
    			System.out.println("Performing delete on page id " + p.getId().toString());
    			p.markDirty(true, tid);
    			if(pages.get(p.getId()) != null) {
    				pages.put(p.getId(), p);
    			}
    			else {
    				if(pages.size() > numPages) {
    				evictPage();
    				}
    				pages.put(p.getId(), p);
				
    			}
    		}
    	}
    	
    }

    /**
     * Flush all dirty pages to disk.
     * NB: Be careful using this routine -- it writes dirty data to disk so will
     *     break simpledb if running in NO STEAL mode.
     */
    public synchronized void flushAllPages() throws IOException {
        // some code goes here
        // not necessary for lab1
    	for(TransactionId tid: pageTransactions.keySet()) {
    		for(PageId pid: pageTransactions.get(tid)) {
    			Page page = pages.get(pid);
    			if(page.isDirty() != null)
    				this.flushPage(page.getId());
    		}
    		
    	}

    }

    /** Remove the specific page id from the buffer pool.
        Needed by the recovery manager to ensure that the
        buffer pool doesn't keep a rolled back page in its
        cache.
        
        Also used by B+ tree files to ensure that deleted pages
        are removed from the cache so they can be reused safely
    */
    public synchronized void discardPage(PageId pid) {
        // some code goes here
        // not necessary for lab1
    	this.pages.remove(pid);
    }

    /**
     * Flushes a certain page to disk
     * @param pid an ID indicating the page to flush
     */
    private synchronized  void flushPage(PageId pid) throws IOException {
        // some code goes here
        // not necessary for lab1
    	//DbFile dbFile = Database.getCatalog().getDatabaseFile(pid.getTableId());
        //dbFile.writePage(pages.get(pid));
        //pages.get(pid).markDirty(false, null);
    	//HeapPage p = (HeapPage) pages.get(pid);
    	//TransactionId tid = p.isDirty();
    	//if(tid != null) {
    	//	int tableId = p.getId().getTableId();
    	//	HeapFile hf = (HeapFile) Database.getCatalog().getDatabaseFile(tableId);
    	//	hf.writePage(p);
    	//	p.markDirty(false, tid);
    	//}
    	Page pToFlush = pages.get(pid);
    	if (pToFlush.isDirty() != null) {
    		int tableId = pid.getTableId();
        	
        	// write page to disk
            Catalog c = Database.getCatalog();
            DbFile f = c.getDatabaseFile(tableId);
            
            // append an update record to the log, with 
            // a before-image and after-image.
            TransactionId dirtier = pToFlush.isDirty();
            if (dirtier != null) {
            	Database.getLogFile().logWrite(dirtier,
            			pToFlush.getBeforeImage(), pToFlush);
            	Database.getLogFile().force();
            }
            
            // write page
            f.writePage(pToFlush);
            pToFlush.markDirty(false, null);
    	}
    
    }

    /** Write all pages of the specified transaction to disk.
     */
    public synchronized  void flushPages(TransactionId tid) throws IOException {
        // some code goes here
        // not necessary for lab1|lab2
    }

    /**
     * Discards a page from the buffer pool.
     * Flushes the page to disk to ensure dirty pages are updated on disk.
     */
    private synchronized  void evictPage() throws DbException {
        // some code goes here
        // not necessary for lab1
    	try{
            Page page = pages.evict();
            // Check if no non-dirty page is in cache
            if(page == null)
                throw new DbException("NOSTEAL: No non-dirty page found for eviction.");
            if(page.isDirty() != null)
                flushPage(page.getId());
            lockManager.removePage(page.getId());
        }catch (IOException ioe){
            throw new DbException("IOException: " + ioe.getMessage());
        }
    }

}
