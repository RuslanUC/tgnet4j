package me.ruslan_uc.telegram.tgnet.models;

import me.ruslan_uc.telegram.tgnet.TgByteBuffer;

public class IP {
    public String address;
    public int port;
    public int flags;
    public String secret;

    public IP(String address, int port, int flags, String secret) {
        this.address = address;
        this.port = port;
        this.flags = flags;
        this.secret = secret;
    }

    public static IP deserialize(TgByteBuffer buffer, int currentVersion) {
        String address = buffer.readString();
        int port = buffer.readUint32();
        int flags = currentVersion >= 7 ? buffer.readInt32() : 0;
        String secret = currentVersion >= 9 ? buffer.readString() : null;

        return new IP(address, port, flags, secret);
    }

    public void serialize(TgByteBuffer buffer, int version) {
        buffer.writeString(address);
        buffer.writeUint32(port);
        buffer.writeInt32(flags);
        if(version >= 9)
            buffer.writeString(secret);
    }
}
