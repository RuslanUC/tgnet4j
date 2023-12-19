package me.ruslan_uc.telegram.tgnet.models;

import me.ruslan_uc.telegram.tgnet.TgByteBuffer;

import java.util.ArrayList;

public class Datacenter {
    public int currentVersion;
    public int datacenterId;
    public int lastInitVersion;
    public int lastInitMediaVersion;
    public ArrayList<ArrayList<IP>> ips;
    public boolean isCdnDatacenter;
    public Auth auth;
    public ArrayList<Salt> salts;
    public ArrayList<Salt> saltsMedia;

    public Datacenter(int currentVersion, int datacenterId, int lastInitVersion, int lastInitMediaVersion, ArrayList<ArrayList<IP>> ips, boolean isCdnDatacenter, Auth auth, ArrayList<Salt> salts, ArrayList<Salt> saltsMedia) {
        this.currentVersion = currentVersion;
        this.datacenterId = datacenterId;
        this.lastInitVersion = lastInitVersion;
        this.lastInitMediaVersion = lastInitMediaVersion;
        this.ips = ips;
        this.isCdnDatacenter = isCdnDatacenter;
        this.auth = auth;
        this.salts = salts;
        this.saltsMedia = saltsMedia;
    }

    public static Datacenter deserialize(TgByteBuffer buffer) {
        int currentVersion = buffer.readUint32();
        int datacenterId = buffer.readUint32();

        int lastInitVersion = currentVersion >= 3 ? buffer.readUint32() : 0;
        int lastInitMediaVersion = currentVersion >= 10 ? buffer.readUint32() : 0;

        ArrayList<ArrayList<IP>> ips = new ArrayList<>() {{
            add(new ArrayList<>());
            add(new ArrayList<>());
            add(new ArrayList<>());
            add(new ArrayList<>());
        }};
        for (int i = 0; i < (currentVersion >= 5 ? 4 : 1); i++) {
            ArrayList<IP> ipArr = ips.get(i);
            int ipCount = buffer.readUint32();

            for (int j = 0; j < ipCount; j++) {
                ipArr.add(IP.deserialize(buffer, currentVersion));
            }
        }

        boolean isCdnDatacenter = currentVersion >= 6 && buffer.readBool();
        Auth auth = Auth.deserialize(buffer, currentVersion);

        int saltCount = buffer.readUint32();
        ArrayList<Salt> salts = new ArrayList<>();
        ArrayList<Salt> saltsMedia = new ArrayList<>();
        for (int j = 0; j < saltCount; j++) {
            salts.add(Salt.deserialize(buffer));
        }

        if (currentVersion >= 13) {
            saltCount = buffer.readUint32();
            for (int j = 0; j < saltCount; j++) {
                saltsMedia.add(Salt.deserialize(buffer));
            }
        }

        return new Datacenter(currentVersion, datacenterId, lastInitVersion, lastInitMediaVersion, ips, isCdnDatacenter, auth, salts, saltsMedia);
    }

    public void serialize(TgByteBuffer buffer) {
        buffer.writeUint32(currentVersion);
        buffer.writeUint32(datacenterId);
        if (currentVersion >= 3)
            buffer.writeUint32(lastInitVersion);
        if (currentVersion >= 10)
            buffer.writeUint32(lastInitMediaVersion);

        for (int i = 0; i < (currentVersion >= 5 ? 4 : 1); i++) {
            buffer.writeUint32(ips.get(i).size());
            for (IP ip : ips.get(i))
                ip.serialize(buffer, currentVersion);
        }

        if (currentVersion >= 6)
            buffer.writeBool(isCdnDatacenter);

        auth.serialize(buffer, currentVersion);

        buffer.writeUint32(salts.size());
        for (Salt salt : salts)
            salt.serialize(buffer);

        if (currentVersion >= 13) {
            buffer.writeUint32(saltsMedia.size());
            for (Salt salt : saltsMedia)
                salt.serialize(buffer);
        }
    }
}
