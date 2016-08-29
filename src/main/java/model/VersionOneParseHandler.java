package model;

import util.ByteUtil;

import java.util.HashMap;

/**
 * Created by nathaniel on 8/29/16.
 */
public class VersionOneParseHandler implements ParseHandler {

    @Override
    public Message parse(byte[] bytes) {
        Message ret = new Message();
        ret.setVersion(1);
        int operation = ByteUtil.getInt(new byte[]{bytes[4], bytes[5], bytes[6], bytes[7]});
        ret.setOp(Operation.getOperation(operation));
        ret.setFields(new HashMap<>());
        int index = 8; // starting index after the version id and operation
        // parse out the fields
        while (index < bytes.length) {
            Field f = new Field();
            byte[] fieldId = new byte[4];
            for (int i = 0; i < fieldId.length; ++i)
                fieldId[i] = bytes[i + index];
            byte[] fieldLength = new byte[4];
            for(int i =0; i < fieldLength.length; ++i)
                fieldLength[i] = bytes[i + index + 4];
            int fId = ByteUtil.getInt(fieldId);
            int length = ByteUtil.getInt(fieldLength);
            System.out.println(length);
            f.setId(fId);
            byte[] data = new byte[length];
            System.arraycopy(bytes,index+8,data,0,length);
            f.setData(data);
            index += 8 + length;
            ret.getFields().put(f.getId(),f);
        }
        return ret;
    }
}
