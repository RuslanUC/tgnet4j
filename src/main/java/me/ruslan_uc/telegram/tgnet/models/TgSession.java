package me.ruslan_uc.telegram.tgnet.models;

import me.ruslan_uc.telegram.tgnet.TgByteBuffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class TgSession {
    public Headers headers;
    public ArrayList<Datacenter> datacenters;

    public TgSession(Headers headers, ArrayList<Datacenter> datacenters) {
        this.headers = headers;
        this.datacenters = datacenters;
    }

    public static TgSession deserialize(TgByteBuffer buffer) {
        buffer.readUint32();

        Headers headers = Headers.deserialize(buffer);
        ArrayList<Datacenter> datacenters = new ArrayList<>();

        int numOfDatacenters = buffer.readUint32();
        for(int j = 0; j < numOfDatacenters; j++) {
            datacenters.add(Datacenter.deserialize(buffer));
        }

        return new TgSession(headers, datacenters);
    }

    public byte[] serialize() {
        TgByteBuffer buffer = new TgByteBuffer(ByteBuffer.allocate(2048).order(ByteOrder.LITTLE_ENDIAN));

        buffer.writeUint32(0);

        headers.serialize(buffer);
        buffer.writeUint32(datacenters.size());
        for(Datacenter dc : datacenters)
            dc.serialize(buffer);

        buffer.writeSize();
        return buffer.getByteArray();
    }
}
