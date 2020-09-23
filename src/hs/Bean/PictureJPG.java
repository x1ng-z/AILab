package hs.Bean;

import java.time.Instant;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/8/24 14:20
 */
public class PictureJPG {
    private String pickname;
    private byte[] data;
    private int size;
    private byte[] sizebytesytle;
    private Instant instant;
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        sizebytesytle=new byte[4];
        sizebytesytle[3]=(byte) (size>>24);
        sizebytesytle[2]=(byte) (size>>16);
        sizebytesytle[1]=(byte) (size>>8);
        sizebytesytle[0]=(byte) (size&0xff);
    }

    public byte[] getSizebytesytle() {
        return sizebytesytle;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public String getPickname() {
        return pickname;
    }

    public void setPickname(String pickname) {
        this.pickname = pickname;
    }
}
