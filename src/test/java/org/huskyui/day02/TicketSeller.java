package org.huskyui.day02;

/**
 * @author huskyui
 */
public class TicketSeller {
    private void sell(){
        System.out.println("售票开始");
        int sleepMillis = 5000;
        try {
            Thread.sleep(sleepMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("售票结束");
    }

    public void sellTicketWithLock() throws Exception{
        MyLock lock = new MyLock();
        lock.acquireLock();
        sell();
        lock.releaseLock();
    }

    public static void main(String[] args) throws Exception {
        TicketSeller ticketSeller = new TicketSeller();
        for (int i = 0;i<10;i++){
            ticketSeller.sellTicketWithLock();
        }
    }


}
