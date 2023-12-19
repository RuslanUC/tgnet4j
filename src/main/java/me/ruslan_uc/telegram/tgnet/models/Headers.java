package me.ruslan_uc.telegram.tgnet.models;

import me.ruslan_uc.telegram.tgnet.TgByteBuffer;

import java.util.ArrayList;

/*


 * */

public class Headers {
    public int version;
    public boolean testBackend;
    public boolean clientBlocked;
    public String lastInitSystemLangCode;
    public boolean full;
    public int currentDatacenterId;
    public int timeDifference;
    public int lastDcUpdateTime;
    public long pushSessionId;
    public boolean registeredForInternalPush;
    public int lastServerTime;
    public int currentTime;
    public ArrayList<Long> sessionsToDestroy;

    public Headers(int version, boolean testBackend, boolean clientBlocked, String lastInitSystemLangCode, boolean full, int currentDatacenterId, int timeDifference, int lastDcUpdateTime, long pushSessionId, boolean registeredForInternalPush, int lastServerTime, int currentTime, ArrayList<Long> sessionsToDestroy) {
        this.version = version;
        this.testBackend = testBackend;
        this.clientBlocked = clientBlocked;
        this.lastInitSystemLangCode = lastInitSystemLangCode;
        this.full = full;
        this.currentDatacenterId = currentDatacenterId;
        this.timeDifference = timeDifference;
        this.lastDcUpdateTime = lastDcUpdateTime;
        this.pushSessionId = pushSessionId;
        this.registeredForInternalPush = registeredForInternalPush;
        this.lastServerTime = lastServerTime;
        this.currentTime = currentTime;
        this.sessionsToDestroy = sessionsToDestroy;
    }

    public Headers(int version, boolean testBackend, boolean clientBlocked, String lastInitSystemLangCode, boolean full) {
        this.version = version;
        this.testBackend = testBackend;
        this.clientBlocked = clientBlocked;
        this.lastInitSystemLangCode = lastInitSystemLangCode;
        this.full = full;
    }

    public static Headers deserialize(TgByteBuffer buffer) {
        int version = buffer.readUint32();
        if (version > 99999)
            throw new UnsupportedOperationException("Deserializing this version of config is not currently supported");

        boolean testBackend = buffer.readBool();
        boolean clientBlocked = version >= 3 && buffer.readBool();
        String lastInitSystemLangCode = version >= 4 ? buffer.readString() : null;

        boolean full = buffer.readBool();
        if (!full)
            return new Headers(version, testBackend, clientBlocked, lastInitSystemLangCode, full);

        int currentDatacenterId = buffer.readUint32();
        int timeDifference = buffer.readInt32();
        int lastDcUpdateTime = buffer.readInt32();
        long pushSessionId = buffer.readInt64();

        boolean registeredForInternalPush = version >= 2 && buffer.readBool();
        int lastServerTime = 0;
        int currentTime = (int) (System.currentTimeMillis() / 1000);

        if (version >= 5) {
            lastServerTime = buffer.readInt32();
            if (timeDifference < currentTime && currentTime < lastServerTime)
                timeDifference += (lastServerTime - currentTime);
        }

        ArrayList<Long> sessionsToDestroy = new ArrayList<>();
        int count = buffer.readUint32();
        for (int i = 0; i < count; i++)
            sessionsToDestroy.add(buffer.readInt64());

        return new Headers(version, testBackend, clientBlocked, lastInitSystemLangCode, full, currentDatacenterId,
                timeDifference, lastDcUpdateTime, pushSessionId, registeredForInternalPush, lastServerTime, currentTime, sessionsToDestroy);
    }

    public void serialize(TgByteBuffer buffer) {
        buffer.writeUint32(version);
        buffer.writeBool(testBackend);
        if (version >= 3)
            buffer.writeBool(clientBlocked);
        if (version >= 4)
            buffer.writeString(lastInitSystemLangCode);

        buffer.writeBool(full);
        if (!full)
            return;

        buffer.writeUint32(currentDatacenterId);
        buffer.writeInt32(timeDifference);
        buffer.writeInt32(lastDcUpdateTime);
        buffer.writeInt64(pushSessionId);

        if (version >= 2)
            buffer.writeBool(registeredForInternalPush);
        if (version >= 5)
            buffer.writeInt32(lastServerTime);

        buffer.writeUint32(sessionsToDestroy.size());
        for (long i : sessionsToDestroy)
            buffer.writeInt64(i);
    }
}
