import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by nathaniel on 9/17/16.
 */
public class TestServerClient {

    //@Test
    public void TestGetTimeUDP(){
        try {
            tsapp.main("-s -T 5 5000 5001".split(" "));
            Thread.sleep(100);
            tsapp.main("-c localhost 5000".split(" "));
            Thread.sleep(300);
        }catch (InterruptedException e){
            Assert.fail();
        }
    }

    //@Test
    public void TestChangeTimeUDP(){
        try{
            tsapp.main("-s -T 5 --user usr --pass pw 5000 5001".split(" "));
            Thread.sleep(500);
            tsapp.main("-c localhost -T 99 --user usr --pass pw -u 5000".split(" "));
            Thread.sleep(3000);
            tsapp.main("-c localhost 5000".split(" "));
        }catch (InterruptedException e){
            Assert.fail();
        }
    }

    //@Test
    public void TestGetTimeTCP(){
        try {
            tsapp.main("-s -T 5 5000 5001".split(" "));
            Thread.sleep(100);
            tsapp.main("-c -t localhost 5001".split(" "));
            Thread.sleep(3000);
        }catch (InterruptedException e){
            Assert.fail();
        }
    }

    //@Test
    public void TestChangeTimeTCP(){
        try{
            tsapp.main("-s -T 5 --user usr --pass pw 5000 5001".split(" "));
            Thread.sleep(500);
            tsapp.main("-c localhost -T 99 -t --user usr --pass pw -u 5001".split(" "));
            Thread.sleep(3000);
            tsapp.main("-c -t localhost 5001".split(" "));
        }catch (InterruptedException e){
            Assert.fail();
        }
    }

    //@Test
    public void TestProxyUDPTCP(){
        try{
            tsapp.main("-s -T 5 --user usr --pass pw 5000 5001".split(" "));
            Thread.sleep(500);
            tsapp.main("-p localhost --proxy-udp 5000 --proxy-tcp 5001 4000 4001".split(" "));
            Thread.sleep(500);
            tsapp.main("-c localhost -n 10 -u 4000".split(" "));
            Thread.sleep(500);
            tsapp.main("-c localhost -n 10 -t 4001".split(" "));
            Thread.sleep(500);
        }catch (InterruptedException e){
            Assert.fail();
        }
    }


    @After
    public void cooldown(){
        System.out.println("============================================");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}

