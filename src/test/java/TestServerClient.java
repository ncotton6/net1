import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by nathaniel on 9/17/16.
 */
public class TestServerClient {

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
    public void TestProxyUDP(){
        try{
            tsapp.main("-s -T 5 --user usr --pass pw 5000 5001".split(" "));
            Thread.sleep(500);
            tsapp.main("-p localhost --proxy-udp 5000 --proxy-tcp 5001 4000 4001".split(" "));
            Thread.sleep(500);
            tsapp.main("-c localhost -u 4000".split(" "));
            Thread.sleep(500);
        }catch (InterruptedException e){
            Assert.fail();
        }
    }

    @Test
    public void TestProxyUDPChangeTime(){
        try{
            tsapp.main("-s -T 5 --user usr --pass pw 5000 5001".split(" "));
            Thread.sleep(500);
            tsapp.main("-p localhost --proxy-udp 5000 --proxy-tcp 5001 4000 4001".split(" "));
            Thread.sleep(500);
            tsapp.main("-c localhost --user usr --pass pw 4000 -T 99".split(" "));
            Thread.sleep(500);
            tsapp.main("-c localhost -u 4000".split(" "));
            Thread.sleep(500);
        }catch (InterruptedException e){
            Assert.fail();
        }
    }

    @Test
    public void TestProxyTCP(){
        try{
            tsapp.main("-s -T 5 --user usr --pass pw 5000 5001".split(" "));
            Thread.sleep(500);
            tsapp.main("-p localhost -t --proxy-udp 5000 --proxy-tcp 5001 4000 4001".split(" "));
            Thread.sleep(500);
            tsapp.main("-c localhost -t 4001".split(" "));
            Thread.sleep(500);
        }catch (InterruptedException e){
            Assert.fail();
        }
    }
    @Test
    public void TestProxyTCPChangeTime(){
        try{
            tsapp.main("-s -T 5 --user usr --pass pw 5000 5001".split(" "));
            Thread.sleep(500);
            tsapp.main("-p localhost -t --proxy-udp 5000 --proxy-tcp 5001 4000 4001".split(" "));
            Thread.sleep(500);
            tsapp.main("-c localhost --user usr --pass pw 4001 -T 99 -t".split(" "));
            Thread.sleep(500);
            tsapp.main("-c localhost -t 4001".split(" "));
            Thread.sleep(500);
        }catch (InterruptedException e){
            Assert.fail();
        }
    }

    @Test
    public void TestProxyProtocolSwitchUDPtoTCP(){
        try{
            tsapp.main("-s -T 5 --user usr --pass pw 5000 5001".split(" "));
            Thread.sleep(500);
            tsapp.main("-p localhost -t --proxy-udp 5000 --proxy-tcp 5001 4000 4001".split(" "));
            Thread.sleep(500);
            tsapp.main("-c localhost -u 4000".split(" "));
            Thread.sleep(500);
        }catch (InterruptedException e){
            Assert.fail();
        }
    }

    @Test
    public void TestProxyProtocolSwitchTCPtoUDP(){
        try{
            tsapp.main("-s -T 5 --user usr --pass pw 5000 5001".split(" "));
            Thread.sleep(500);
            tsapp.main("-p localhost -u --proxy-udp 5000 --proxy-tcp 5001 4000 4001".split(" "));
            Thread.sleep(500);
            tsapp.main("-c localhost -t 4001".split(" "));
            Thread.sleep(500);
        }catch (InterruptedException e){
            Assert.fail();
        }
    }


    @After
    public void cooldown(){
        System.out.println("======================================================");
        try {
            Thread.sleep(10000); // ensures the teardown of TCP
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}

