package com.group1.logistics.service;

import com.group1.logistics.datastructures.FreightPackageArray;
import com.group1.logistics.datastructures.InventoryBST;
import com.group1.logistics.datastructures.OrderQueue;
import com.group1.logistics.datastructures.RestockStack;
import com.group1.logistics.models.FreightPackage;
import com.group1.logistics.models.Order;
import com.group1.logistics.models.Product;
import com.group1.logistics.models.RestockAction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class FulfillmentStorage {
    private static final String HEADER = "LHUB|V1";
    private static final long FNV_OFFSET = 0xcbf29ce484222325L;
    private static final long FNV_PRIME = 0x100000001b3L;
    private static final Object IO_LOCK = new Object();

    private FulfillmentStorage() {
    }

    public static File defaultSaveFile() {
        File dir = new File(System.getProperty("user.home"), ".logistics-hub");
        return new File(dir, "logistics-data.txt");
    }

    public static File backupFileFor(File primaryFile) {
        return new File(primaryFile.getParentFile(), primaryFile.getName() + ".bak");
    }

    public static final class LoadResult {
        public final boolean loaded;
        public final String notice;
        public final InventoryBST bst;
        public final OrderQueue queue;
        public final RestockStack stack;
        public final FreightPackageArray packages;
        public final int nextOrderId;
        public final int nextActionId;
        public final int nextPackageId;
        public final int sequenceCounter;

        LoadResult(boolean loaded, String notice, InventoryBST bst, OrderQueue queue, RestockStack stack,
                   FreightPackageArray packages, int nextOrderId, int nextActionId, int nextPackageId,
                   int sequenceCounter) {
            this.loaded = loaded;
            this.notice = notice;
            this.bst = bst;
            this.queue = queue;
            this.stack = stack;
            this.packages = packages;
            this.nextOrderId = nextOrderId;
            this.nextActionId = nextActionId;
            this.nextPackageId = nextPackageId;
            this.sequenceCounter = sequenceCounter;
        }
    }

    public static void save(File targetFile, FulfillmentService service) throws IOException {
        if (targetFile == null || service == null) {
            return;
        }
        synchronized (IO_LOCK) {
            File parent = targetFile.getAbsoluteFile().getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new IOException("Could not create save directory: " + parent.getAbsolutePath());
            }

            byte[] payload = serialize(service).getBytes(StandardCharsets.UTF_8);
            File absTarget = targetFile.getAbsoluteFile();
            File tmpFile = new File(parent, absTarget.getName() + ".tmp");
            File bakFile = backupFileFor(absTarget);

            writeAndSync(tmpFile, payload);

            if (absTarget.exists() && absTarget.length() > 0) {
                File bakTmp = new File(parent, bakFile.getName() + ".tmp");
                Files.copy(absTarget.toPath(), bakTmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                syncFilePath(bakTmp);
                atomicReplace(bakTmp, bakFile);
            }

            atomicReplace(tmpFile, absTarget);
        }
    }

    public static LoadResult load(File targetFile, int queueCapacity, int stackCapacity) {
        if (targetFile == null) {
            return emptyFallback(null);
        }
        synchronized (IO_LOCK) {
            File absTarget = targetFile.getAbsoluteFile();
            File bakFile = backupFileFor(absTarget);

            if (absTarget.exists()) {
                LoadResult primary = tryParse(absTarget, queueCapacity, stackCapacity);
                if (primary != null) {
                    return primary;
                }
                quarantineCorruptFile(absTarget);
                if (bakFile.exists()) {
                    LoadResult recovered = tryParse(bakFile, queueCapacity, stackCapacity);
                    if (recovered != null) {
                        try {
                            File restoreTmp = new File(absTarget.getParentFile(), absTarget.getName() + ".tmp");
                            Files.copy(bakFile.toPath(), restoreTmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            syncFilePath(restoreTmp);
                            atomicReplace(restoreTmp, absTarget);
                        } catch (IOException ignored) {
                        }
                        return new LoadResult(
                                true,
                                "Recovered saved data from backup after primary save file check failed.",
                                recovered.bst,
                                recovered.queue,
                                recovered.stack,
                                recovered.packages,
                                recovered.nextOrderId,
                                recovered.nextActionId,
                                recovered.nextPackageId,
                                recovered.sequenceCounter
                        );
                    }
                    quarantineCorruptFile(bakFile);
                }
                return emptyFallback("Saved file was corrupted and backed aside; started with default sample data.");
            }

            if (bakFile.exists()) {
                LoadResult recovered = tryParse(bakFile, queueCapacity, stackCapacity);
                if (recovered != null) {
                    try {
                        File restoreTmp = new File(absTarget.getParentFile(), absTarget.getName() + ".tmp");
                        Files.copy(bakFile.toPath(), restoreTmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        syncFilePath(restoreTmp);
                        atomicReplace(restoreTmp, absTarget);
                    } catch (IOException ignored) {
                    }
                    return new LoadResult(
                            true,
                            "Restored saved data from backup file.",
                            recovered.bst,
                            recovered.queue,
                            recovered.stack,
                            recovered.packages,
                            recovered.nextOrderId,
                            recovered.nextActionId,
                            recovered.nextPackageId,
                            recovered.sequenceCounter
                    );
                }
                quarantineCorruptFile(bakFile);
                return emptyFallback("Backup save file was corrupted and backed aside; started with default sample data.");
            }

            return emptyFallback(null);
        }
    }

    private static LoadResult emptyFallback(String notice) {
        return new LoadResult(false, notice, null, null, null, null, 1001, 1, 501, 1);
    }

    private static void writeAndSync(File file, byte[] bytes) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file, false)) {
            fos.write(bytes);
            fos.flush();
            fos.getFD().sync();
        }
    }

    private static void syncFilePath(File file) {
        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            fos.getFD().sync();
        } catch (IOException ignored) {
        }
    }

    private static void atomicReplace(File source, File target) throws IOException {
        try {
            Files.move(source.toPath(), target.toPath(),
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void quarantineCorruptFile(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        File quarantine = new File(file.getParentFile(), file.getName() + ".corrupt." + System.currentTimeMillis());
        try {
            Files.move(file.toPath(), quarantine.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignored) {
        }
    }

    private static String serialize(FulfillmentService service) {
        StringBuilder body = new StringBuilder(2048);
        body.append(HEADER).append('\n');
        body.append("META|")
                .append(service.getNextOrderId()).append('|')
                .append(service.getNextActionId()).append('|')
                .append(service.getNextPackageId()).append('|')
                .append(service.getSequenceCounter()).append('\n');

        int recordCount = 0;

        Product[] preOrderProducts = service.getInventoryBst().preOrder();
        for (int i = 0; i < preOrderProducts.length; i++) {
            Product p = preOrderProducts[i];
            if (p != null) {
                body.append("SKU|")
                        .append(p.getSkuId()).append('|')
                        .append(escapeField(p.getName())).append('|')
                        .append(Double.toString(p.getPrice())).append('|')
                        .append(p.getStockQty()).append('\n');
                recordCount++;
            }
        }

        OrderQueue queue = service.getOrderQueue();
        int qSize = queue.size();
        for (int i = 0; i < qSize; i++) {
            Order o = queue.get(i);
            if (o != null) {
                body.append("ORD|")
                        .append(o.getOrderId()).append('|')
                        .append(escapeField(o.getCustomerName())).append('|')
                        .append(o.getSkuId()).append('|')
                        .append(o.getQuantity()).append('|')
                        .append(escapeField(o.getDestination())).append('|')
                        .append(o.getDeliveryPriority()).append('|')
                        .append(o.getSequenceNo()).append('\n');
                recordCount++;
            }
        }

        RestockStack stack = service.getRestockStack();
        int sSize = stack.size();
        for (int i = sSize - 1; i >= 0; i--) {
            RestockAction a = stack.get(i);
            if (a != null) {
                body.append("RST|")
                        .append(a.getActionId()).append('|')
                        .append(a.getSkuId()).append('|')
                        .append(a.getQuantityAdded()).append('|')
                        .append(a.getSequenceNo()).append('\n');
                recordCount++;
            }
        }

        FreightPackageArray pkgs = service.getPackageArray();
        int pSize = pkgs.size();
        for (int i = 0; i < pSize; i++) {
            FreightPackage fp = pkgs.get(i);
            if (fp != null) {
                body.append("PKG|")
                        .append(fp.getPackageId()).append('|')
                        .append(fp.getOrderId()).append('|')
                        .append(escapeField(fp.getDestination())).append('|')
                        .append(Double.toString(fp.getWeightKg())).append('|')
                        .append(fp.getDeliveryPriority()).append('|')
                        .append(fp.getSequenceNo()).append('\n');
                recordCount++;
            }
        }

        String hash = checksumHex(body.toString());
        body.append("END|").append(recordCount).append('|').append(hash).append('\n');
        return body.toString();
    }

    private static LoadResult tryParse(File file, int queueCapacity, int stackCapacity) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();
            if (!HEADER.equals(headerLine)) {
                return null;
            }

            StringBuilder hashInput = new StringBuilder(2048);
            hashInput.append(headerLine).append('\n');

            String metaLine = reader.readLine();
            if (metaLine == null) {
                return null;
            }
            String[] meta = splitEscapedFields(metaLine);
            if (meta.length != 5 || !"META".equals(meta[0])) {
                return null;
            }
            hashInput.append(metaLine).append('\n');

            int nextOrderId = Integer.parseInt(meta[1]);
            int nextActionId = Integer.parseInt(meta[2]);
            int nextPackageId = Integer.parseInt(meta[3]);
            int sequenceCounter = Integer.parseInt(meta[4]);
            if (nextOrderId <= 0 || nextActionId <= 0 || nextPackageId <= 0 || sequenceCounter <= 0) {
                return null;
            }

            InventoryBST bst = new InventoryBST();
            OrderQueue queue = new OrderQueue(queueCapacity);
            RestockStack stack = new RestockStack(stackCapacity);
            FreightPackageArray packages = new FreightPackageArray(16);

            int parsedRecords = 0;
            boolean sawEnd = false;
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = splitEscapedFields(line);
                if (parts.length == 0) {
                    return null;
                }
                String tag = parts[0];
                if ("END".equals(tag)) {
                    if (parts.length != 3) {
                        return null;
                    }
                    int expectedCount = Integer.parseInt(parts[1]);
                    String expectedHash = parts[2];
                    String actualHash = checksumHex(hashInput.toString());
                    if (expectedCount != parsedRecords || !actualHash.equals(expectedHash)) {
                        return null;
                    }
                    sawEnd = true;
                    break;
                }

                hashInput.append(line).append('\n');

                if ("SKU".equals(tag)) {
                    if (parts.length != 5) {
                        return null;
                    }
                    int skuId = Integer.parseInt(parts[1]);
                    String name = unescapeField(parts[2]);
                    double price = Double.parseDouble(parts[3]);
                    int stockQty = Integer.parseInt(parts[4]);
                    if (Double.isNaN(price) || Double.isInfinite(price)) {
                        return null;
                    }
                    if (!bst.insert(new Product(skuId, name, price, stockQty))) {
                        return null;
                    }
                    parsedRecords++;
                } else if ("ORD".equals(tag)) {
                    if (parts.length != 8) {
                        return null;
                    }
                    int orderId = Integer.parseInt(parts[1]);
                    String cust = unescapeField(parts[2]);
                    int skuId = Integer.parseInt(parts[3]);
                    int qty = Integer.parseInt(parts[4]);
                    String dest = unescapeField(parts[5]);
                    int prio = Integer.parseInt(parts[6]);
                    int seq = Integer.parseInt(parts[7]);
                    if (!queue.enqueue(new Order(orderId, cust, skuId, qty, dest, prio, seq))) {
                        return null;
                    }
                    parsedRecords++;
                } else if ("RST".equals(tag)) {
                    if (parts.length != 5) {
                        return null;
                    }
                    int actionId = Integer.parseInt(parts[1]);
                    int skuId = Integer.parseInt(parts[2]);
                    int qtyAdded = Integer.parseInt(parts[3]);
                    int seq = Integer.parseInt(parts[4]);
                    if (!stack.push(new RestockAction(actionId, skuId, qtyAdded, seq))) {
                        return null;
                    }
                    parsedRecords++;
                } else if ("PKG".equals(tag)) {
                    if (parts.length != 7) {
                        return null;
                    }
                    int pkgId = Integer.parseInt(parts[1]);
                    int orderId = Integer.parseInt(parts[2]);
                    String dest = unescapeField(parts[3]);
                    double weightKg = Double.parseDouble(parts[4]);
                    int prio = Integer.parseInt(parts[5]);
                    int seq = Integer.parseInt(parts[6]);
                    packages.add(new FreightPackage(pkgId, orderId, dest, weightKg, prio, seq));
                    parsedRecords++;
                } else {
                    return null;
                }
            }

            if (!sawEnd) {
                return null;
            }
            if (reader.readLine() != null) {
                return null;
            }

            return new LoadResult(true, null, bst, queue, stack, packages,
                    nextOrderId, nextActionId, nextPackageId, sequenceCounter);
        } catch (Exception ex) {
            return null;
        }
    }

    private static String checksumHex(String data) {
        long hash = FNV_OFFSET;
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < bytes.length; i++) {
            hash ^= (bytes[i] & 0xffL);
            hash *= FNV_PRIME;
        }
        StringBuilder hex = new StringBuilder(16);
        String raw = Long.toHexString(hash);
        for (int i = raw.length(); i < 16; i++) {
            hex.append('0');
        }
        hex.append(raw);
        return hex.toString();
    }

    private static String escapeField(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\\') {
                sb.append("\\\\");
            } else if (c == '|') {
                sb.append("\\p");
            } else if (c == '\n') {
                sb.append("\\n");
            } else if (c == '\r') {
                sb.append("\\r");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String unescapeField(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\\' && i + 1 < value.length()) {
                char next = value.charAt(++i);
                if (next == '\\') {
                    sb.append('\\');
                } else if (next == 'p') {
                    sb.append('|');
                } else if (next == 'n') {
                    sb.append('\n');
                } else if (next == 'r') {
                    sb.append('\r');
                } else {
                    sb.append(next);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String[] splitEscapedFields(String line) {
        int count = 1;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '|') {
                count++;
            }
        }
        String[] result = new String[count];
        int start = 0;
        int idx = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '|') {
                result[idx++] = line.substring(start, i);
                start = i + 1;
            }
        }
        result[idx] = line.substring(start);
        return result;
    }
}
