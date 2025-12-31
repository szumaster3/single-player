package core.rs3.unpack.config;

import core.rs3.unpack.Type;
import core.rs3.unpack.Unpacker;
import core.rs3.unpack.VarDomain;
import core.rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class VarClientUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.VAR_CLIENT, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> {
                var type = packet.g1();
                Unpacker.setVarType(VarDomain.CLIENT, id, Type.byChar(type));
                lines.add("type=" + Unpacker.format(Type.TYPE, type));
            }

            case 2 -> lines.add("scope=perm");
            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
