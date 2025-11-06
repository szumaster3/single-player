package core.cache;

import core.cache.crypto.XTEACryption;
import core.cache.misc.ContainersInformation;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Represents a cache file.
 *
 * @author Dragonkk
 */
public final class CacheFile {

    /**
     * The index file id.
     */
    private int indexFileId;

    /**
     * The cache file buffer.
     */
    private byte[] cacheFileBuffer;

    /**
     * The maximum container size.
     */
    private int maxContainerSize;

    /**
     * The index file.
     */
    private RandomAccessFile indexFile;

    /**
     * The data file.
     */
    private RandomAccessFile dataFile;

    /**
     * Construct a new cache file.
     *
     * @param indexFileId      The index file id.
     * @param indexFile        The index file.
     * @param dataFile         The data file.
     * @param maxContainerSize The maximum container size.
     * @param cacheFileBuffer  The cache file buffer.
     */
    public CacheFile(int indexFileId, RandomAccessFile indexFile, RandomAccessFile dataFile, int maxContainerSize, byte[] cacheFileBuffer) {
        this.cacheFileBuffer = cacheFileBuffer;
        this.indexFileId = indexFileId;
        this.maxContainerSize = maxContainerSize;
        this.indexFile = indexFile;
        this.dataFile = dataFile;
    }

    /**
     * Get the unpacked container data.
     *
     * @param containerId The container id.
     * @param xteaKeys    The container keys.
     * @return The unpacked container data.
     */
    public final byte[] getContainerUnpackedData(int containerId, int[] xteaKeys) {
        byte[] packedData = getContainerData(containerId);
        if (packedData == null) {
            return null;
        }
        if (xteaKeys != null && (xteaKeys[0] != 0 || xteaKeys[1] != 0 || xteaKeys[2] != 0 || xteaKeys[3] != 0)) {
            packedData = XTEACryption.decrypt(xteaKeys, ByteBuffer.wrap(packedData), 5, packedData.length).array();
        }
        return ContainersInformation.unpackCacheContainer(packedData);
    }

    /**
     * Get the container data for the specified container id.
     *
     * @param containerId The container id.
     * @return The container data.
     */
    public final byte[] getContainerData(int containerId) {
        synchronized (dataFile) {
            try {
                if (indexFile.length() < (6 * containerId + 6)) {
                    return null;
                }
                indexFile.seek(6 * containerId);
                indexFile.read(cacheFileBuffer, 0, 6);
                int containerSize = (cacheFileBuffer[2] & 0xff) + (((0xff & cacheFileBuffer[0]) << 16) + (cacheFileBuffer[1] << 8 & 0xff00));
                int sector = ((cacheFileBuffer[3] & 0xff) << 16) - (-(0xff00 & cacheFileBuffer[4] << 8) - (cacheFileBuffer[5] & 0xff));
                if (containerSize < 0 || containerSize > maxContainerSize) {
                    return null;
                }
                if (sector <= 0 || dataFile.length() / 520L < sector) {
                    return null;
                }
                byte data[] = new byte[containerSize];
                int dataReadCount = 0;
                int part = 0;
                while (containerSize > dataReadCount) {
                    if (sector == 0) {
                        return null;
                    }
                    dataFile.seek(520 * sector);
                    int dataToReadCount = containerSize - dataReadCount;
                    if (dataToReadCount > 512) {
                        dataToReadCount = 512;
                    }
                    dataFile.read(cacheFileBuffer, 0, 8 + dataToReadCount);
                    int currentContainerId = (0xff & cacheFileBuffer[1]) + (0xff00 & cacheFileBuffer[0] << 8);
                    int currentPart = ((cacheFileBuffer[2] & 0xff) << 8) + (0xff & cacheFileBuffer[3]);
                    int nextSector = (cacheFileBuffer[6] & 0xff) + (0xff00 & cacheFileBuffer[5] << 8) + ((0xff & cacheFileBuffer[4]) << 16);
                    int currentIndexFileId = cacheFileBuffer[7] & 0xff;
                    if (containerId != currentContainerId || currentPart != part || indexFileId != currentIndexFileId) {
                        return null;
                    }
                    if (nextSector < 0 || (dataFile.length() / 520L) < nextSector) {
                        return null;
                    }
                    for (int index = 0; dataToReadCount > index; index++) {
                        data[dataReadCount++] = cacheFileBuffer[8 + index];
                    }
                    part++;
                    sector = nextSector;
                }
                return data;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Reads a container from the cache file.
     */
    public final byte[] read(int containerId, int[] xteaKeys) {
        synchronized (dataFile) {
            try {
                if (indexFile.length() < 6L * (containerId + 1)) {
                    return null;
                }

                indexFile.seek(6L * containerId);
                indexFile.readFully(cacheFileBuffer, 0, 6);

                int containerSize = ((cacheFileBuffer[0] & 0xFF) << 16)
                        | ((cacheFileBuffer[1] & 0xFF) << 8)
                        | (cacheFileBuffer[2] & 0xFF);

                int firstSector = ((cacheFileBuffer[3] & 0xFF) << 16)
                        | ((cacheFileBuffer[4] & 0xFF) << 8)
                        | (cacheFileBuffer[5] & 0xFF);

                if (firstSector <= 0 || containerSize <= 0) {
                    return null;
                }

                byte[] data = new byte[containerSize];
                int offset = 0;
                int sector = firstSector;
                int sectorIndex = 0;

                while (offset < containerSize) {
                    dataFile.seek(520L * sector);

                    int chunkSize = Math.min(512, containerSize - offset);

                    if (dataFile.read(cacheFileBuffer, 0, chunkSize + 8) != chunkSize + 8) {
                        return null;
                    }

                    int readContainerId =
                            ((cacheFileBuffer[0] & 0xFF) << 8) | (cacheFileBuffer[1] & 0xFF);
                    int readSectorIndex =
                            ((cacheFileBuffer[2] & 0xFF) << 8) | (cacheFileBuffer[3] & 0xFF);
                    int nextSector =
                            ((cacheFileBuffer[4] & 0xFF) << 16)
                                    | ((cacheFileBuffer[5] & 0xFF) << 8)
                                    | (cacheFileBuffer[6] & 0xFF);
                    int readIndexFileId = cacheFileBuffer[7] & 0xFF;

                    if (readContainerId != containerId || readSectorIndex != sectorIndex || readIndexFileId != indexFileId) {
                        return null;
                    }

                    System.arraycopy(cacheFileBuffer, 8, data, offset, chunkSize);

                    offset += chunkSize;
                    sector = nextSector;
                    sectorIndex++;

                    if (sector == 0 && offset < containerSize) {
                        return null;
                    }
                }

                if (xteaKeys != null && (xteaKeys[0] != 0 || xteaKeys[1] != 0 || xteaKeys[2] != 0 || xteaKeys[3] != 0)) {
                    ByteBuffer buffer = ByteBuffer.wrap(data);
                    XTEACryption.decrypt(xteaKeys, buffer, 0, containerSize);
                }

                return data;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Writes a container to the cache file.
     */
    public final boolean write(int containerId, byte[] data, int[] xteaKeys) {
        synchronized (dataFile) {
            try {
                if (xteaKeys != null && (xteaKeys[0] != 0 || xteaKeys[1] != 0 || xteaKeys[2] != 0 || xteaKeys[3] != 0)) {
                    ByteBuffer buffer = ByteBuffer.wrap(data);
                    XTEACryption.encrypt(xteaKeys, buffer, 0, data.length);
                }

                int containerSize = data.length;
                int numSectors = (containerSize + 511) / 512;


                int firstSector = 0;
                if (indexFile.length() >= 6L * (containerId + 1)) {
                    indexFile.seek(6L * containerId);
                    indexFile.readFully(cacheFileBuffer, 0, 6);
                    firstSector = ((cacheFileBuffer[3] & 0xFF) << 16)
                            | ((cacheFileBuffer[4] & 0xFF) << 8)
                            | (cacheFileBuffer[5] & 0xFF);
                }


                int[] sectors = new int[numSectors];
                if (firstSector > 0 && dataFile.length() / 520L >= firstSector) {
                    int sector = firstSector;
                    for (int i = 0; i < numSectors; i++) {
                        sectors[i] = sector;
                        dataFile.seek(520L * sector);
                        if (dataFile.read(cacheFileBuffer, 0, 8) != 8) break;
                        int next = ((cacheFileBuffer[4] & 0xFF) << 16)
                                | ((cacheFileBuffer[5] & 0xFF) << 8)
                                | (cacheFileBuffer[6] & 0xFF);
                        sector = next != 0 ? next : (int) (dataFile.length() / 520L + 1);
                    }
                } else {
                    long totalSectors = dataFile.length() / 520L;
                    for (int i = 0; i < numSectors; i++) {
                        sectors[i] = (int) (totalSectors + i + 1);
                    }
                }


                indexFile.seek(6L * containerId);
                indexFile.write((containerSize >> 16) & 0xFF);
                indexFile.write((containerSize >> 8) & 0xFF);
                indexFile.write(containerSize & 0xFF);
                int first = sectors[0];
                indexFile.write((first >> 16) & 0xFF);
                indexFile.write((first >> 8) & 0xFF);
                indexFile.write(first & 0xFF);

                int offset = 0;
                for (int i = 0; i < numSectors; i++) {
                    int sector = sectors[i];
                    int chunkSize = Math.min(512, containerSize - offset);

                    dataFile.seek(520L * sector);

                    cacheFileBuffer[0] = (byte) (containerId >> 8);
                    cacheFileBuffer[1] = (byte) containerId;
                    cacheFileBuffer[2] = (byte) (i >> 8);
                    cacheFileBuffer[3] = (byte) i;

                    int nextSector = (i < numSectors - 1) ? sectors[i + 1] : 0;
                    cacheFileBuffer[4] = (byte) (nextSector >> 16);
                    cacheFileBuffer[5] = (byte) (nextSector >> 8);
                    cacheFileBuffer[6] = (byte) nextSector;

                    cacheFileBuffer[7] = (byte) indexFileId;

                    System.arraycopy(data, offset, cacheFileBuffer, 8, chunkSize);

                    dataFile.write(cacheFileBuffer, 0, chunkSize + 8);
                    offset += chunkSize;
                }

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Get the index file id.
     *
     * @return
     */
    public int getIndexFileId() {
        return indexFileId;
    }

    /**
     * Get the unpacked container data.
     *
     * @param containerId The container id.
     * @return The unpacked container data.
     */
    public final byte[] getContainerUnpackedData(int containerId) {
        return getContainerUnpackedData(containerId, null);
    }
}
