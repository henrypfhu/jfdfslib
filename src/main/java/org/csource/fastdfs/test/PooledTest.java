package org.csource.fastdfs.test;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.csource.fastdfs.pool.PooledFdfsServerFactory;

public class PooledTest {
    private static void testWithPool(String config, String file, int times) {
        long start = 0;

        try {
            start = System.currentTimeMillis();

            ClientGlobal.init(config);
            ClientGlobal.setFactory(new PooledFdfsServerFactory(ClientGlobal.getConfig()));

            // System.out.println("network_timeout=" +
            // ClientGlobal.g_network_timeout + "ms");
            // System.out.println("charset=" + ClientGlobal.g_charset);

            for (int c = 0; c < times; c++) {
                TrackerServer trackerServer = ClientGlobal.getTrackerGroup().getTrackerServer();
                TrackerClient tracker = new TrackerClient(trackerServer);

                StorageServer storageServer = tracker.getStoreStorage();
                StorageClient1 client = new StorageClient1(storageServer);

                NameValuePair[] metaList = new NameValuePair[1];
                metaList[0] = new NameValuePair("fileName", file);
                String fileId = client.upload_file1(file, null, metaList);
                System.out.println("upload success. file id is: " + fileId);

                byte[] result = client.download_file1(fileId);
                System.out.println("download result is: " + result.length);

                trackerServer.close();
                storageServer.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        long end = System.currentTimeMillis();

        ClientGlobal.getFactory().close();

        System.out.println("consume with pool:" + (end - start));
    }

    private static void testWithoutPool(String config, String file, int times) {
        long start = 0;

        try {
            start = System.currentTimeMillis();
            ClientGlobal.init(config);

            for (int c = 0; c < times; c++) {
                TrackerServer trackerServer = ClientGlobal.getTrackerGroup().getTrackerServer();
                TrackerClient tracker = new TrackerClient(trackerServer);

                StorageServer storageServer = tracker.getStoreStorage();
                StorageClient1 client = new StorageClient1(storageServer);

                NameValuePair[] metaList = new NameValuePair[1];
                metaList[0] = new NameValuePair("fileName", file);
                String fileId = client.upload_file1(file, null, metaList);
                System.out.println("upload success. file id is: " + fileId);

                byte[] result = client.download_file1(fileId);
                System.out.println("download result is: " + result.length);

                trackerServer.close();
                storageServer.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        long end = System.currentTimeMillis();

        System.out.println("consume without pool:" + (end - start));
    }

    public static void main(String args[]) {
        if (args.length < 2) {
            System.out.println("Error: Must have 2 parameters, one is config filename, "
                    + "the other is the local filename to upload");
            return;
        }

        System.out.println("java.version=" + System.getProperty("java.version"));

        String config = args[0];
        String file = args[1];

        testWithoutPool(config, file, 200);

        testWithPool(config, file, 200);
    }
}
