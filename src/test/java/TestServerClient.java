import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by nathaniel on 9/17/16.
 */
public class TestServerClient {

    //@Test
    public void TestGetTime(){
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
    public void TestChangeTime(){
        try{
            tsapp.main("-s -T 5 --user usr --pass pw 5000 5001".split(" "));
            Thread.sleep(3000);
            tsapp.main("-c localhost -T 99 --user usr --pass pw -t 5001".split(" "));
            Thread.sleep(3000);
            //tsapp.main("-c localhost 5000".split(" "));
            Thread.sleep(300000);
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
