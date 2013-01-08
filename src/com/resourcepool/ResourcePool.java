/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.resourcepool;

import com.resourcepool.exception.PoolNotOpenException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gsuero
 */
public class ResourcePool<R> {

    private boolean open = false;
    private Queue<R> checkedInResources; //Collections.synchronizedList()
    private Queue<R> checkedOutResources; //Collections.synchronizedList()

    public ResourcePool() {
        checkedInResources = new ConcurrentLinkedQueue<R>();
        checkedOutResources = new ConcurrentLinkedQueue<R>(); 
    }

    public void open() {
        open = true;
    }

    public boolean isOpen() {
        return open;
    }

    public synchronized void close() {
        open = false;
        while(checkedOutResources.size() >  0){
            try {
                wait();
                System.out.println("I waited");
            } catch (InterruptedException ex) {
                Logger.getLogger(ResourcePool.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        notifyAll();
    }

    public void closeNow() {
        open = false;
        checkedInResources.addAll(checkedOutResources);
        checkedOutResources.clear();
    }

    public synchronized R acquire() throws PoolNotOpenException, InterruptedException {
        if (!isOpen()) {
            throw new PoolNotOpenException();
        }
        while (checkedInResources.size() == 0) {
            wait();
        }
        R resource = checkedInResources.poll();
        checkedOutResources.add(resource);
        return resource;
    }

    public synchronized R acquire(long timeout, TimeUnit timeUnit) throws PoolNotOpenException, InterruptedException {
        if (!isOpen()) {
            throw new PoolNotOpenException();
        }
        if (checkedInResources.size() == 0) {
            timeUnit.timedWait(this, timeout);
        }

        R resource = checkedInResources.poll();
        if (resource != null) {
            checkedOutResources.add(resource);
        }
        return resource;
    }

    public synchronized void release(R resource) throws IllegalArgumentException {
        if (resource == null || !checkedOutResources.contains(resource)) {
            throw new IllegalArgumentException("Resource doesn't exist.");
        }
        checkedOutResources.remove(resource);
        checkedInResources.offer(resource);
        notify();
    }

    public synchronized boolean add(R resource) {
        if (!checkedInResources.contains(resource) || !checkedOutResources.contains(resource)) {
            boolean added = checkedInResources.offer(resource);
            notify();
            return added;
        }
        return false;
    }

    public synchronized boolean remove(R resource) {
        try {
            while(checkedOutResources.contains(resource)) {
                wait();
            }
            if (checkedInResources.contains(resource)) {
                notify();
                return checkedInResources.remove(resource);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ResourcePool.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean removeNow(R resource) {
        boolean modified = false;
        if (checkedInResources.contains(resource)) {
            modified = checkedInResources.remove(resource);
        } else if (checkedOutResources.contains(resource)) {
            modified = checkedOutResources.remove(resource);
        }
        return modified;
    }
}
