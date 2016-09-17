import model.Field;
import model.FieldType;
import model.Message;
import model.Operation;
import util.MessageHandler;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Created by nathaniel on 8/29/16.
 */
public class TestMessages {
    public static void main(String[] args) {
        Message m = new Message();
        m.setVersion((byte)1);
        m.setOp(Operation.GETTIME);
        Field f = new Field();
        f.setId("user");
        f.setObjData("test");
        System.out.println(Arrays.toString(f.getByteArray()));
        m.addField(f);
        m.addField(new Field("password","meter"));
        m.addField(new Field(FieldType.IPPORTSTACK,new String[]{"1","2","junk"}));
        m.addField(new Field(FieldType.TIMESTACK,new Long[]{45L,29L,22L}));
        m.addField(new Field(FieldType.TIME,9382L));
        System.out.println(Arrays.toString(m.getByteArray()));
        Message m2 = MessageHandler.getMessage(m.getByteArray());
        for(Map.Entry<String,Object> ent : m2.getFields().entrySet()){
            System.out.print(ent.getKey() + "  ");
            if(ent.getValue() instanceof Field){
                Field f2 = (Field) ent.getValue();
                System.out.println(f2.getObjData().getClass().getName());
                if(f2.getObjData() instanceof Object[]){
                    System.out.println(Arrays.toString((Object[]) f2.getObjData()));
                }else if(f2.getObjData() instanceof long[]){
                    System.out.println(Arrays.toString((long[])f2.getObjData()));
                } else{
                    System.out.println(f2.getObjData());
                }
            }else{
                System.out.println(ent.getValue());
            }
        }

    }
}
