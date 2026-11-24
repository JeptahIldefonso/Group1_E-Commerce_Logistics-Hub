# Group 1 DSA Project — Strict Engineering Rules

## 1. Environment & Dependencies
- Target **Java 17 LTS** strictly (`javac --release 17`).
- **ZERO third-party libraries:** No FlatLaf, no MigLayout, no external JARs in `src/`. Only standard JDK libraries (`java.lang.*`, `java.io.*`, `javax.swing.*`, `java.awt.*`).

## 2. Zero Java Collections Framework
- **NEVER** import or use classes from the Java Collections Framework anywhere in `src/`:
  - NO `java.util.ArrayList`, `LinkedList`, `Vector`, `HashMap`, `TreeMap`, `HashSet`, `TreeSet`
  - NO `java.util.Queue`, `Stack`, `PriorityQueue`, `Deque`
  - NO `java.util.Arrays`, `Collections`, `Comparator`, `Random`, `Stream`, `List.of`, `Map.of`
- **NEVER** import `java.time.*` or `java.text.*` in `src/`.

## 3. Swing & Table Compliance
- **NEVER** use `DefaultTableModel`, `TableRowSorter`, or `setAutoCreateRowSorter(true)`.
- Always extend `AbstractTableModel` and keep table cells non-editable (`isCellEditable` returns `false`).
- For sorting table headers, always attach custom `MouseAdapter` listeners invoking `FulfillmentService.sortPackages(...)`.

## 4. Architecture & Separation of Concerns
- UI classes (`src/com/group1/logistics/ui/`) must **ONLY** call `FulfillmentService` methods and receive `OpResult`.
- UI classes must **NEVER** directly mutate `OrderQueue`, `RestockStack`, `InventoryBST`, or `FreightPackageArray`.
- Before committing any changes, run the PowerShell compliance regex scan to verify zero forbidden matches.
