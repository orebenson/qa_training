import java.io.*;
import java.net.*;
import java.util.*;

public class PacketMessage{

    byte[] buf;
    

    public PacketMessage(){
    }
    
    public PacketMessage(byte[] data){
        this.buf = data;
    }

    public PacketMessage(byte[] data, int length){
        this.buf = Arrays.copyOfRange(data, 0, length);
    }
    
    public byte[] getBuf(){
        return this.buf;
    }

    public int getOpcode(){
        int opcode = this.buf[1];
        return opcode;
    }

    public int getBlockNo(){
        int num = ((this.buf[2]) << 8) | (this.buf[3]);
        return num;
    }

    public String toString(){
        String fileName;
        int blockNo;
        int opcode = this.buf[1];
        switch(opcode){
            case 1:
                fileName = this.getFileName();
                return String.format("RRQ: %s", fileName);
            case 2:
                fileName = this.getFileName();
                return String.format("WRQ: %s", fileName);
            case 3:
                blockNo = this.buf[3];
                String data = this.getData();
                return String.format("DATA: %d %s", blockNo, data);
            case 4:
                blockNo = this.buf[3];
                return String.format("ACK: %d", blockNo);
            case 5:
                int err = this.buf[3];
                String errMsg = this.getData();
                return String.format("ERROR: %d %s", err, errMsg);
        }
        return "Empty message";
    }

    public String getData(){
        int i = 4;
        while(i < this.buf.length){
            i++;
        }
        String fileData = new String(this.buf, 4, i-4); 
        return fileData;
    }
  
    public byte[] getByteData(){
        int i = 4;
        while(i < this.buf.length){
            i++;
        }
        byte[] fileByteData = Arrays.copyOfRange(this.buf, 4, i-4); 
        return fileByteData;
    }
 
    public String getFileName(){
        int i = 2;
        while(this.buf[i] != 0){
            i++;
        }
        String fileName = new String(this.buf, 2, i-2); 
        return fileName;
    }

    public int length(){
        return this.buf.length;
    }

    public void RRQ(String fileName){
        //format RRQ packet
        byte[] file = fileName.getBytes();
        byte[] mode = "octet".getBytes();
        int n = 2 + file.length + 1 + mode.length + 1;
        this.buf = new byte[n];
        this.buf[1] = 1;
        System.arraycopy(file, 0, this.buf, 2, file.length);
        System.arraycopy(mode, 0, this.buf, 3 + file.length, mode.length);
    }

    public void WRQ(String fileName){
        //format WRQ packet
        byte[] file = fileName.getBytes();
        byte[] mode = "octet".getBytes();
        int n = 2 + file.length + 1 + mode.length + 1;
        this.buf = new byte[n];
        this.buf[1] = 2;
        System.arraycopy(file, 0, this.buf, 2, file.length);
        System.arraycopy(mode, 0, this.buf, 2 + file.length + 1, mode.length);
    }

    public void DATA(int blockNo, byte[] data){
        //format DATA packet
        int n = 2 + 2 + data.length;
        this.buf = new byte[n];
        this.buf[1] = 3;
        this.buf[2] = (byte) (blockNo >> 8);
        this.buf[3] = (byte) blockNo;
        System.arraycopy(data, 0, this.buf, 4, data.length);   
    }

    public void ACK(int blockNo){
        //format ACK packet
        int n = 2 + 2;
        this.buf = new byte[n];
        this.buf[1] = 4;
        this.buf[2] = (byte) (blockNo >> 8);
        this.buf[3] = (byte) blockNo;
    }

    public void ERROR(int errorCode, String errMsg){
        //format ERROR packet
        byte[] err = errMsg.getBytes();
        int n = 2 + 2 + err.length + 1;
        this.buf = new byte[n];
        this.buf[1] = 5;
        this.buf[2] = (byte) (errorCode >> 8);
        this.buf[3] = (byte) errorCode;
        System.arraycopy(err, 0, this.buf, 2 + 2, err.length);
    }
}
