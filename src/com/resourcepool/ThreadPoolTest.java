/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.resourcepool;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gsuero
 */
public class ThreadPoolTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ResourcePool pool = new ResourcePool();
        pool = new ResourcePool<String>();
        pool.open();
        pool.add("task1");
        pool.add("task2");
        pool.add("task3");

        new SimpleThread("Jamaica", pool).start();
        new SimpleThread("Fiji", pool).start();
        new SimpleThread("DR", pool).start();
        new SimpleThread("USA", pool).start();
        new SimpleThread("France", pool).start();
        new SimpleThread("Haiti", pool).start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ex) {
            Logger.getLogger(ThreadPoolTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        //pool.close();
        pool.removeNow("task2");
        pool.removeNow("task1");
        pool.removeNow("task3");
        
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException ex) {
            Logger.getLogger(ThreadPoolTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        pool.add("task1");
        pool.add("task2");
        pool.add("task3");
        pool.add("task4");
        pool.add("task5");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException ex) {
            Logger.getLogger(ThreadPoolTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("I want to close the pool");
        pool.close();
        System.out.println("pool closed?");
    }
}
