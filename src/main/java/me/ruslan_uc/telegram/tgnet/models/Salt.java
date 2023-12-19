package me.ruslan_uc.telegram.tgnet.models;

import me.ruslan_uc.telegram.tgnet.TgByteBuffer;

public class Salt {
    public int valid_since;
    public int valid_until;
    public long salt;

    public Salt(int valid_since, int valid_until, long salt) {
        this.valid_since = valid_since;
        this.valid_until = valid_until;
        this.salt = salt;
    }

    public static Salt deserialize(TgByteBuffer buffer) {
        return new Salt(buffer.readInt32(), buffer.readInt32(), buffer.readInt64());
    }

    public void serialize(TgByteBuffer buffer) {
        buffer.writeInt32(valid_since);
        buffer.writeInt32(valid_until);
        buffer.writeInt64(salt);
    }
}
