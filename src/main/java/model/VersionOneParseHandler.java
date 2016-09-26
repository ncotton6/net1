package model;

import util.ByteUtil;
import util.Converter;

import java.util.Arrays;
import java.util.Map;

/**
 * {@link VersionOneParseHandler} class implements {@link ParseHandler} and allows for the first version
 * of the protocol to be parsed from a byte[] to a Message.
 *
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
            // read the value associated with the name
            Field f = new Field();
            if(identifier == 0){
                // it is an unknown field
                int nameLength = ByteUtil.getInt(bytes,index);
                index += 4;
                f.setId(ByteUtil.getString(bytes,index,index+nameLength));
                index += nameLength;
            }
            int valueLength = ByteUtil.getInt(bytes,index);
            index += 4;
            byte[] value = Arrays.copyOfRange(bytes,index,index+valueLength);
            index += valueLength;
            f.setObjData(value);
            if(identifier != 0) {
                FieldType ft = FieldType.lookup(identifier);
                f.setId(ft.name());
                f.setObjData(Converter.convert(ret.getVersion(), ft.clazz, value));
            }

            ret.addField(f);
        }
        return ret;
    }
}
