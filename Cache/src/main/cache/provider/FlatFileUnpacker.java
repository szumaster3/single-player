package cache.provider;

import core.rs3.Unpack;

import java.io.IOException;

public class FlatFileUnpacker {


    public static void main(String[] args) throws IOException, InterruptedException {
        Unpack.unpackLocal("resources/unpacked", 530, -1);
    }
}
