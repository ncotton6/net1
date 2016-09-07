package model;

import util.ByteUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nathaniel on 8/29/16.
 */
public class VersionOneParseHandler implements ParseHandler {


    @Override
    public Message parse(byte[] bytes) {
        Message ret = new Message();
        ret.setVersion((byte)1);
        short operation = ByteUtil.getShort(new byte[]{bytes[1], bytes[2]});
        ret.setOp(Operation.getOperation(operation));
        int index = 3; // starting index after the version id and operation
        // parse out the fields
        while (index < bytes.length) {
            // read 4 bytes to determine what data is going to be coming
            int identifier = ByteUtil.getInt(bytes,index);
            index += 4; // move index by 4 for the int
            String name = null;
            if(identifier == 0){
                // it is an unknown field
                int nameLength = ByteUtil.getInt(bytes,index);
                index += 4;
                name = ByteUtil.getString(bytes,index,index+nameLength);
                index += nameLength;
            }else{
                name = FieldMapper.reverseLookup(identifier);
            }
            // read the value associated with the name
            int valueLength = ByteUtil.getInt(bytes,index);
            index += 4;
            byte[] value = Arrays.copyOfRange(bytes,index,index+valueLength);
            index += valueLength;
            Field f = new Field(name,value);
            ret.addField(f);
        }
        cleanData(ret);
        return ret;
    }

    private void cleanData(Message ret) {
        Map<String, Object> map = ret.getFields();
        for(String key : map.keySet()){
            System.out.println(key);
        }
    }
}
