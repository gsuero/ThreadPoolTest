/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.resourcepool;

import com.resourcepool.exception.PoolNotOpenException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gsuero
 */
class SimpleThread extends Thread {
    ResourcePool<String> pool;
    public SimpleThread(String str, ResourcePool pool) {
	super(str);
        this.pool = pool;
    }
    public void run() {
        try {
            String object = null;
            for (int i = 0; i < 20; i++) {
                object = pool.acquire();
                System.out.println(getName() + " #" + i + " : " + object);
                    pool.release(object);
            }
            System.out.println("DONE! " + getName());
            //pool.remove(object);
            
        } catch (PoolNotOpenException ex) {
            Logger.getLogger(SimpleThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(SimpleThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
