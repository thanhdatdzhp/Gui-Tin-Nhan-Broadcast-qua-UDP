import java.io.*;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import javax.sound.sampled.*;

public final class FileTransfer {
    private FileTransfer(){}

    private static final byte[] MAGIC = "LTMF".getBytes(StandardCharsets.US_ASCII);
    private static final byte VER = 1;
    public  static final byte TYPE_META  = 1;
    public  static final byte TYPE_CHUNK = 2;
    public  static final byte TYPE_END   = 3;

    // ===== Sender =====
    public static int randomFileId() { return ThreadLocalRandom.current().nextInt(); }

    public static byte[] buildMeta(int fileId, String fileName, long fileSize, int chunkSize, int totalChunks) {
        byte[] nameBytes = fileName.getBytes(StandardCharsets.UTF_8);
        ByteBuffer bb = ByteBuffer.allocate(4 + 1 + 1 + 4 + 2 + nameBytes.length + 8 + 4 + 4);
        bb.put(MAGIC).put(VER).put(TYPE_META).putInt(fileId)
          .putShort((short) nameBytes.length).put(nameBytes)
          .putLong(fileSize).putInt(chunkSize).putInt(totalChunks);
        return bb.array();
    }

    public static byte[] buildChunk(int fileId, int chunkIndex, byte[] data, int off, int len) {
        ByteBuffer bb = ByteBuffer.allocate(4 + 1 + 1 + 4 + 4 + len);
        bb.put(MAGIC).put(VER).put(TYPE_CHUNK).putInt(fileId).putInt(chunkIndex)
          .put(data, off, len);
        return bb.array();
    }

    public static byte[] buildEnd(int fileId) {
        ByteBuffer bb = ByteBuffer.allocate(4 + 1 + 1 + 4);
        bb.put(MAGIC).put(VER).put(TYPE_END).putInt(fileId);
        return bb.array();
    }

    // ===== Receiver =====
    public static boolean isFilePacket(byte[] buf, int len) {
        if (len < 6) return false;
        for (int i=0;i<4;i++) if (buf[i]!=MAGIC[i]) return false;
        return true;
    }

    public static class Meta {
        public final int fileId, chunkSize, totalChunks;
        public final String fileName;
        public final long fileSize;
        public Meta(int fileId, String fileName, long fileSize, int chunkSize, int totalChunks) {
            this.fileId=fileId; this.fileName=fileName; this.fileSize=fileSize;
            this.chunkSize=chunkSize; this.totalChunks=totalChunks;
        }
    }

    public interface ProgressListener {
        void onProgress(int fileId, String name, long size, long receivedBytes, int receivedChunks, int totalChunks);
        void onCompleted(int fileId, File savedFile);
        void onMeta(int fileId, Meta meta);
    }

    public static Object parsePacket(byte[] data, int len) throws IOException {
        ByteBuffer bb = ByteBuffer.wrap(data, 0, len);
        byte[] magic = new byte[4]; bb.get(magic);
        byte ver = bb.get(); byte type = bb.get();
        if (ver != VER) throw new IOException("Unsupported version");
        if (type == TYPE_META) {
            int fileId = bb.getInt();
            int nameLen = Short.toUnsignedInt(bb.getShort());
            byte[] name = new byte[nameLen]; bb.get(name);
            String fileName = new String(name, StandardCharsets.UTF_8);
            long fileSize = bb.getLong();
            int chunkSize = bb.getInt();
            int totalChunks = bb.getInt();
            return new Meta(fileId, fileName, fileSize, chunkSize, totalChunks);
        } else if (type == TYPE_CHUNK) {
            int fileId = bb.getInt();
            int index = bb.getInt();
            byte[] payload = new byte[bb.remaining()];
            bb.get(payload);
            return new Object[]{fileId, index, payload};
        } else if (type == TYPE_END) {
            int fileId = bb.getInt();
            return new Object[]{fileId, -1, null}; // END
        }
        throw new IOException("Unknown type");
    }

    // Quản lý tái lắp file
    public static class Reassembler implements Closeable {
        private final Map<Integer, Entry> files = new ConcurrentHashMap<>();
        private final File saveDir;
        private final ProgressListener listener;

        public Reassembler(File saveDir, ProgressListener listener) {
            this.saveDir = saveDir; this.listener = listener;
        }

        public void onMeta(Meta m) throws IOException {
            files.compute(m.fileId, (id, old) -> {
                try {
                    if (old != null) old.close();
                    return new Entry(m, saveDir);
                } catch (IOException e) { throw new RuntimeException(e); }
            });
            if (listener!=null) listener.onMeta(m.fileId, m);
        }

        public void onChunk(int fileId, int index, byte[] payload) throws IOException {
            Entry e = files.get(fileId);
            if (e == null) return; // chưa nhận meta
            e.write(index, payload);
            if (listener!=null) listener.onProgress(fileId, e.meta.fileName, e.meta.fileSize, e.receivedBytes, e.receivedChunks(), e.meta.totalChunks);
        }

        public void onEnd(int fileId) throws IOException {
            Entry e = files.get(fileId);
            if (e == null) return;
            File f = e.finish();
            files.remove(fileId);
            if (listener!=null) listener.onCompleted(fileId, f);
        }

        public void closeAll() {
            for (Entry e: files.values()) try { e.close(); } catch (Exception ignore) {}
            files.clear();
        }

        @Override public void close() { closeAll(); }

        private static class Entry implements Closeable {
            final Meta meta;
            final RandomAccessFile raf;
            final File partFile;
            final BitSet received = new BitSet();
            long receivedBytes = 0;

            Entry(Meta m, File dir) throws IOException {
                this.meta = m;
                if (!dir.exists()) dir.mkdirs();
                this.partFile = new File(dir, m.fileName + ".part");
                this.raf = new RandomAccessFile(this.partFile, "rw");
                this.raf.setLength(m.fileSize);
            }

            void write(int idx, byte[] data) throws IOException {
                long pos = (long) idx * meta.chunkSize;
                raf.seek(pos);
                raf.write(data);
                if (!received.get(idx)) {
                    received.set(idx);
                    receivedBytes += data.length;
                }
            }

            int receivedChunks() { return received.cardinality(); }

            File finish() throws IOException {
                close();
                File finalFile = new File(partFile.getParentFile(), meta.fileName);
                if (finalFile.exists()) finalFile.delete();
                if (!partFile.renameTo(finalFile)) throw new IOException("Không thể đổi tên file đích");
                return finalFile;
            }

            @Override public void close() throws IOException { raf.close(); }
        }
    }

    // Phát WAV
    public static void playWav(File file) {
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(file)) {
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
        } catch (Exception e) {
            System.err.println("Không phát được WAV: " + e.getMessage());
        }
    }

    // Tạo DatagramPacket sẵn đích
    public static DatagramPacket pkt(byte[] payload, String ip, int port) throws IOException {
        return new DatagramPacket(payload, payload.length, Config.toAddress(ip), port);
    }
}
