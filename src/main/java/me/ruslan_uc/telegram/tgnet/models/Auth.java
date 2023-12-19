package me.ruslan_uc.telegram.tgnet.models;

import me.ruslan_uc.telegram.tgnet.TgByteBuffer;

public class Auth {
    public byte[] authKeyPerm;
    public long authKeyPermId;
    public byte[] authKeyTemp;
    public long authKeyTempId;
    public byte[] authKeyMediaTemp;
    public long authKeyMediaTempId;
    public int authorized;

    public Auth(byte[] authKeyPerm, long authKeyPermId, byte[] authKeyTemp, long authKeyTempId, byte[] authKeyMediaTemp, long authKeyMediaTempId, int authorized) {
        this.authKeyPerm = authKeyPerm;
        this.authKeyPermId = authKeyPermId;
        this.authKeyTemp = authKeyTemp;
        this.authKeyTempId = authKeyTempId;
        this.authKeyMediaTemp = authKeyMediaTemp;
        this.authKeyMediaTempId = authKeyMediaTempId;
        this.authorized = authorized;
    }

    public static Auth deserialize(TgByteBuffer buffer, int version) {
        int authKeyPermSize, len;
        long authKeyPermId = 0, authKeyTempId = 0, authKeyMediaTempId = 0;
        byte[] authKeyPerm = new byte[0], authKeyTemp = new byte[0], authKeyMediaTemp = new byte[0];

        authKeyPermSize = buffer.readUint32();
        if (authKeyPermSize != 0)
            authKeyPerm = buffer.readByteArray(authKeyPermSize);

        if (version >= 4)
            authKeyPermId = buffer.readInt64();
        else if (buffer.readUint32() != 0)
            authKeyPermId = buffer.readInt64();

        if (version >= 8) {
            if ((len = buffer.readUint32()) != 0)
                authKeyTemp = buffer.readByteArray(len);
            authKeyTempId = buffer.readInt64();
        }

        if (version >= 12) {
            if ((len = buffer.readUint32()) != 0)
                authKeyMediaTemp = buffer.readByteArray(len);
            authKeyMediaTempId = buffer.readInt64();
        }

        int authorized = buffer.readInt32();

        return new Auth(authKeyPerm, authKeyPermId, authKeyTemp, authKeyTempId, authKeyMediaTemp, authKeyMediaTempId, authorized);
    }

    public void serialize(TgByteBuffer buffer, int version) {
        buffer.writeUint32(authKeyPerm.length);
        if (authKeyPerm.length > 0)
            buffer.writeByteArray(authKeyPerm);

        if (version >= 4) {
            buffer.writeInt64(authKeyPermId);
        } else {
            if (authKeyPermId > 0) {
                buffer.writeUint32(8);
                buffer.writeInt64(authKeyPermId);
            } else {
                buffer.writeUint32(0);
            }
        }

        if (version >= 8) {
            if (authKeyTemp.length > 0) {
                buffer.writeUint32(authKeyTemp.length);
                buffer.writeByteArray(authKeyTemp);
            } else {
                buffer.writeUint32(0);
            }
            buffer.writeInt64(authKeyTempId);
        }

        if (version >= 12) {
            if (authKeyMediaTemp.length > 0) {
                buffer.writeUint32(authKeyMediaTemp.length);
                buffer.writeByteArray(authKeyMediaTemp);
            } else {
                buffer.writeUint32(0);
            }
            buffer.writeInt64(authKeyMediaTempId);
        }

        buffer.writeInt32(authorized);
    }
}
