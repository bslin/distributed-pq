package edu.stanford.cs244b.projects.priorityqueue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;


class SnapshotUtils {

    public static void loadSnapshot(PriorityQueue pq) {
        File snapshotDir = getSnapshotDir();
        File latestSnapshot = cleanSnapshots(snapshotDir);
        if (latestSnapshot != null) {
            try(DataInputStream dataInputStream = new DataInputStream(
                new BufferedInputStream(new FileInputStream(latestSnapshot))
            )) {
                long kafkaOffset = dataInputStream.readLong();
                SingletonInstances.ADD_PQ_ITEM_CONSUMER_OFFSET.set(kafkaOffset);
                System.out.println("SNAPSHOT offset " + kafkaOffset);

                try {
                    while (true) {
                        PQItem pqItem = PQItem.read(dataInputStream);
                        System.out.println("SNAPSHOT added " + pqItem);
                        pq.add(pqItem);
                    }
                } catch (EOFException ex) {
                    System.out.println("SNAPSHOT Complete! " + ex);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }
    }

    public static String snapshot(long kafkaOffset, Iterator<PQItem> pqItems) {
        try {
            File snapshotDir = getSnapshotDir();
            File tempFile = File.createTempFile(
                String.format("pq-%019d-", System.currentTimeMillis()),
                ".snapshot",
                snapshotDir
            );

            DataOutputStream dataOutputStream = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(tempFile))
            );
            dataOutputStream.writeLong(kafkaOffset);

            while(pqItems.hasNext()) {
                PQItem pqItem = pqItems.next();
                PQItem.write(pqItem, dataOutputStream);
            }

            dataOutputStream.flush();
            dataOutputStream.close();
            File outputFile =  new File(snapshotDir, String.format("pq-%019d.snapshot", System.currentTimeMillis()));
            Files.move(
                tempFile.toPath(),
                outputFile.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING
            );

            File latestSnapshot = cleanSnapshots(snapshotDir);
            return latestSnapshot.getAbsolutePath();

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static File cleanSnapshots(File snapshopDir) {
        File[] files = snapshopDir.listFiles();
        if (files.length > 1) {
            Arrays.sort(files, Comparator.comparing(File::getName));
            for (int i = 0; i < files.length - 1; i++) {
                files[i].delete();
            }
        }
        if (files.length > 0) {
            return files[files.length - 1];
        }
        return null;
    }

    public static File getSnapshotDir() {
        File file = new File(String.format(Constants.SNAPSHOT_DIR, SingletonInstances.INSTANCE_NUMBER));
        boolean created = file.mkdirs();
        System.out.println(String.format("Using snapshot dir %s, created %s", file.getAbsoluteFile(), created));
        return file;
    }
}