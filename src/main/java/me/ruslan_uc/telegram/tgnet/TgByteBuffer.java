package me.ruslan_uc.telegram.tgnet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TgByteBuffer {
    private static final byte[] BOOL_TRUE = new byte[]{(byte) 0xb5, 0x75, 0x72, (byte) 0x99};
    private static final byte[] BOOL_FALSE = new byte[]{0x37, (byte) 0x97, 0x79, (byte) 0xbc};

    private ByteBuffer buffer;

    public TgByteBuffer(ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        this.buffer = buffer;
    }

    private void checkBufferLimit(int len) {
        if (len <= buffer.remaining())
            return;
        ByteBuffer old_buffer = buffer;
        int new_capacity = Math.min((int) (buffer.capacity() * 1.5), (int) ((buffer.capacity() + len) * 1.1));
        buffer = ByteBuffer.allocate(new_capacity).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(old_buffer.array());
        buffer.position(old_buffer.position());
    }

    public void writeByteArray(byte[] arr) {
        checkBufferLimit(arr.length);
        buffer.put(arr);
    }

    public void writeInt32(int value) {
        checkBufferLimit(4);
        buffer.putInt(value);
    }

    public void writeUint32(int value) {
        checkBufferLimit(4);
        writeByteArray(uintToLittleEndianByteArray(value));
    }

    public void writeInt64(long value) {
        checkBufferLimit(8);
        buffer.putLong(value);
    }

    public void writeBool(boolean value) {
        checkBufferLimit(4);
        buffer.put(value ? BOOL_TRUE : BOOL_FALSE);
    }

    public void writeByte(byte value) {
        checkBufferLimit(1);
        buffer.put(value);
    }

    public void writeString(String value) {
        checkBufferLimit(value.length() + 8);
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);

        if (value.length() <= 253) {
            writeByte((byte) value.length());
        } else {
            writeByte((byte) 254);
            writeByte((byte) (value.length() % 256));
            writeByte((byte) (value.length() >> 8));
            writeByte((byte) (value.length() >> 16));
        }

        writeByteArray(bytes);
        int padding = (value.length() + (value.length() <= 253 ? 1 : 4)) % 4;
        if (padding != 0)
            padding = 4 - padding;

        for (int i = 0; i < padding; i++)
            writeByte((byte) 0);
    }

    public int readInt32() {
        return buffer.getInt();
    }

    public int readUint32() {
        byte[] buf = readByteArray(4);
        return littleEndianByteArrayToUInt(buf);
    }

    public long readInt64() {
        return buffer.getLong();
    }

    public boolean readBool() {
        return readUint32() == 0x997275b5;
    }

    public byte[] readByteArray(int len) {
        byte[] buf = new byte[len];
        buffer.get(buf);
        return buf;
    }

    public String readString() {
        int sl = 1;
        int len = buffer.get();
        if (len >= 254) {
            byte[] l_ = readByteArray(3);
            len = l_[0] | (l_[1] << 8) | (l_[2] << 16);
            sl = 4;
        }

        int padding = (len + sl) % 4;
        if (padding != 0)
            padding = 4 - padding;

        byte[] output = readByteArray(len);
        readByteArray(padding);
        return new String(output);
    }

    private static byte[] uintToLittleEndianByteArray(int value) {
        byte[] byteArray = new byte[4];
        for (int i = 0; i < 4; i++) {
            byteArray[i] = (byte) (value >>> (i * 8));
        }
        return byteArray;
    }

    private static int littleEndianByteArrayToUInt(byte[] byteArray) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result |= (Byte.toUnsignedInt(byteArray[i]) << (i * 8));
        }
        return result;
    }

    public void writeSize() {
        int pos = buffer.position();
        buffer.position(0);
        writeUint32(pos - 4);
        buffer.position(pos);
    }

    public byte[] getByteArray() {
        return Arrays.copyOf(buffer.array(), buffer.position());
    }
}
